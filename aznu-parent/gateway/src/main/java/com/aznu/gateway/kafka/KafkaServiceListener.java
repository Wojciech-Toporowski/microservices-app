package com.aznu.gateway.kafka;

import com.aznu.gateway.service.GatewayService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aznu.common.models.services.OperationError;
import org.aznu.common.models.services.date.RepairDateResponse;
import org.aznu.common.models.services.part.RepairPartResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaServiceListener {

    private final GatewayService gatewayService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public KafkaServiceListener(GatewayService service) {
        gatewayService = service;
    }

    @KafkaListener(topics = "${kafka.date.response.topic}", groupId = "${spring.kafka.date.consumer.group-id}", autoStartup = "${listen.auto.start:true}", concurrency = "${listen.concurrency:1}")
    public void dateListen(String data) {
        try {
            RepairDateResponse response = mapper.readValue(data, RepairDateResponse.class);
            log.info("Received date booking response for {}", response.getVisitId());
            gatewayService.serveDateResponse(response);
        } catch (JsonProcessingException e) {
            log.error("Can not parse incoming message to {} object: ", RepairDateResponse.class.getName(), e);
        }
    }

    @KafkaListener(topics = "${kafka.date.error.topic}", groupId = "${spring.kafka.date.consumer.group-id}", autoStartup = "${listen.auto.start:true}", concurrency = "${listen.concurrency:1}")
    public void dateErrorListen(String data) {
        try {
            OperationError response = mapper.readValue(data, OperationError.class);
            log.info("Received date booking error response for {}", response.getVisitId());
            gatewayService.serveDateErrorResponse(response);
        } catch (JsonProcessingException e) {
            log.error("Can not parse incoming error message to {} object: ", OperationError.class.getName(), e);
        }
    }

    @KafkaListener(topics = "${kafka.part.response.topic}", groupId = "${spring.kafka.part.consumer.group-id}", autoStartup = "${listen.auto.start:true}", concurrency = "${listen.concurrency:1}")
    public void partListen(String data) {
        try {
            RepairPartResponse response = mapper.readValue(data, RepairPartResponse.class);
            log.info("Received part booking response for {}", response.getVisitId());
            gatewayService.servePartResponse(response);
        } catch (JsonProcessingException e) {
            log.error("Can not parse incoming part message to {} object: ", RepairPartResponse.class.getName(), e);
        }
    }

    @KafkaListener(topics = "${kafka.part.error.topic}", groupId = "${spring.kafka.part.consumer.group-id}", autoStartup = "${listen.auto.start:true}", concurrency = "${listen.concurrency:1}")
    public void partErrorListen(String data) {
        try {
            OperationError response = mapper.readValue(data, OperationError.class);
            log.info("Received part booking error response for {}", response.getVisitId());
            gatewayService.servePartErrorResponse(response);
        } catch (JsonProcessingException e) {
            log.error("Can not parse incoming error message to {} object: ", OperationError.class.getName(), e);
        }
    }
}
