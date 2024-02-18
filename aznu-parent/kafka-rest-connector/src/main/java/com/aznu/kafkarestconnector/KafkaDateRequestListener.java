package com.aznu.kafkarestconnector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aznu.common.models.services.OperationError;
import org.aznu.common.models.services.date.RepairDateRequest;
import org.aznu.common.models.services.date.RepairDateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class KafkaDateRequestListener {

    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate restTemplate;
    private final KafkaTemplate<UUID, String> kafkaTemplate;
    @Value("${repairDate.service.address}")
    private String dateServiceAddress;
    @Value("${repairDate.service.bookingEndpoint}")
    private String dateServiceEndpoint;
    @Value("${kafka.date.response.topic}")
    private String responseTopic;
    @Value("${kafka.date.error.topic}")
    private String errorTopic;


    @Autowired
    public KafkaDateRequestListener(RestTemplate rt, KafkaTemplate<UUID, String> template) {
        restTemplate = rt;
        kafkaTemplate = template;
    }

    @KafkaListener(topics = "${kafka.date.request.topic}", groupId = "${spring.kafka.consumer.group-id}", autoStartup = "${listen.auto.start:true}", concurrency = "${listen.concurrency:1}")
    public void requestListener(String data) {
        try {
            RepairDateRequest request = mapper.readValue(data, RepairDateRequest.class);
            log.info("Incoming date booking request with id: {}. Calling service...", request.getVisitId());
            ResponseEntity<RepairDateResponse> re = restTemplate.postForEntity(
                    "http://" + dateServiceAddress + dateServiceEndpoint,
                    request,
                    RepairDateResponse.class
            );
            log.info("Service call success for {}. Returning result to gateway", re.getBody().getVisitId());
            kafkaTemplate.send(responseTopic, mapper.writeValueAsString(re.getBody()));
            kafkaTemplate.flush();
            log.info("Result for {} pushed to topic {}", re.getBody().getVisitId(), responseTopic);

        } catch (JsonProcessingException e) {
            log.error("Can not parse date request from gateway", e);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.info("Service returned an error: ", e);
            sendToErrorTopic(e.getResponseBodyAs(OperationError.class));
        } catch (Exception e) {
            log.error("Unexpected error at date topic listener", e);
        }
    }

    private void sendToErrorTopic(OperationError error) {
        try {
            kafkaTemplate.send(errorTopic, mapper.writeValueAsString(error));
            log.info("OperationError pushed to topic {}", errorTopic);
        } catch (JsonProcessingException ex) {
            log.error("Error with parsing OperationError message: ", ex);
        }
    }

    @KafkaListener(topics = "${kafka.date.cancel.topic}", groupId = "${spring.kafka.consumer.group-id}", autoStartup = "${listen.auto.start:true}", concurrency = "${listen.concurrency:1}")
    public void cancelListener(String visitId) {
        try {
            log.info("Incoming date booking cancellation for {}. Calling service...", visitId);
            Map<String, String> params = new HashMap<String, String>();
            params.put("visitId", visitId);
            restTemplate.delete(
                    "http://" + dateServiceAddress + dateServiceEndpoint + "/{visitId}",
                    params
            );
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.info("Service returned an error: ", e);
            sendToErrorTopic(e.getResponseBodyAs(OperationError.class));
        }
    }

}
