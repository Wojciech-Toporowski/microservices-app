package com.aznu.kafkasoapconnector;

import com.aznu.partservicesoap.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Component
@Slf4j
public class KafkaPartListener {

    private static final QName SERVICE_NAME = new QName("http://partservicesoap.aznu.com/", "PartWebServiceService");
    private final ObjectMapper mapper = new ObjectMapper();
    private final KafkaTemplate<UUID, String> kafkaTemplate;
    private final PartWebService port;
    @Value("${kafka.part.response.topic}")
    private String responseTopic;
    @Value("${kafka.part.error.topic}")
    private String errorTopic;

    @Autowired
    public KafkaPartListener(KafkaTemplate<UUID, String> template, @Value("${wsdl.path}") Resource wsdlResource) {
        kafkaTemplate = template;
        URL wsdlUrl = null;
        try {
            wsdlUrl = wsdlResource.getURL();
        } catch (IOException e) {
            log.error("Wrong path to wsdl", e);
            throw new RuntimeException(e);
        }
        log.info("Wsdl path: {}", wsdlResource);
        PartWebServiceService soapService = new PartWebServiceService(wsdlUrl, SERVICE_NAME);
        port = soapService.getPartWebServicePort();
    }

    @KafkaListener(topics = "${kafka.part.request.topic}", groupId = "${spring.kafka.consumer.group-id}", autoStartup = "${listen.auto.start:true}", concurrency = "${listen.concurrency:1}")
    public void requestListener(String data) {
        try {
            RepairPartRequest request = mapper.readValue(data, RepairPartRequest.class);
            log.info("Incoming part booking request with id: {}. Calling service...", request.getVisitId());

            RepairPartResponse response = port.bookPart(request);

            log.info("Service call success for {}. Returning result to gateway", response.getVisitId());
            kafkaTemplate.send(responseTopic, mapper.writeValueAsString(response));
            kafkaTemplate.flush();
            log.info("Result for {} pushed to topic {}", response.getVisitId(), responseTopic);

        } catch (JsonProcessingException e) {
            log.error("Can not parse part request from gateway", e);
        } catch (SoapError_Exception e) {
            log.warn("Service returned an error: ", e);
            sendToErrorTopic(soapErrorToOperationError(e.getFaultInfo()));
        } catch (Exception e) {
            log.error("Unexpected error at part topic listener", e);
        }
    }

    private org.aznu.common.models.services.OperationError soapErrorToOperationError(SoapError se) {
        return org.aznu.common.models.services.OperationError.builder()
                .visitId(UUID.fromString(se.getVisitId()))
                .errorCode(se.getErrorCode())
                .message(se.getMessage())
                .time(se.getTime())
                .build();
    }

    private void sendToErrorTopic(org.aznu.common.models.services.OperationError error) {
        try {
            kafkaTemplate.send(errorTopic, mapper.writeValueAsString(error));
            log.info("OperationError pushed to topic {}", errorTopic);
        } catch (JsonProcessingException ex) {
            log.error("Error with parsing OperationError message: ", ex);
        }
    }

    @KafkaListener(topics = "${kafka.part.cancel.topic}", groupId = "${spring.kafka.consumer.group-id}", autoStartup = "${listen.auto.start:true}", concurrency = "${listen.concurrency:1}")
    public void cancelListener(String visitId) {
        try {
            log.info("Incoming part booking cancellation for {}. Calling service...", visitId);
            port.cancelPartBooking(visitId);
            log.info("Calling part booking cancellation for {} success", visitId);

        } catch (Exception e) {
            log.info("Service returned an error: ", e);
        }
    }
}
