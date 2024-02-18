package com.aznu.gateway.service;

import com.aznu.gateway.kafka.KafkaProducersManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.aznu.common.models.gateway.api.RepairRequest;
import org.aznu.common.models.gateway.api.RepairStatus;
import org.aznu.common.models.gateway.api.Status;
import org.aznu.common.models.services.OperationError;
import org.aznu.common.models.services.date.RepairDateRequest;
import org.aznu.common.models.services.date.RepairDateResponse;
import org.aznu.common.models.services.part.RepairPartRequest;
import org.aznu.common.models.services.part.RepairPartResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class GatewayService {

    private final Producer<UUID, String> kafkaDateProducer;
    private final Producer<UUID, String > kafkaPartProducer;
    private final ObjectMapper om = new ObjectMapper();
    private final Map<UUID, BookingStatus> bookings = new ConcurrentHashMap<UUID, BookingStatus>();
    @Value("${kafka.date.request.topic}")
    private String dataRequestTopic;
    @Value("${kafka.date.cancel.topic}")
    private String dateCancelTopic;

    @Value("${kafka.part.request.topic}")
    private String partRequestTopic;
    @Value("${kafka.part.cancel.topic}")
    private String partCancelTopic;

    public GatewayService(@Autowired KafkaProducersManager producersManager) {
        kafkaDateProducer = producersManager.getDateServiceProducer();
        kafkaPartProducer = producersManager.getDateServiceProducer();
    }

    public RepairStatus bookRepair(RepairRequest request) {
        // null visitId - user app calls gateway first time
        if (request.getVisitId() == null || !bookings.containsKey(request.getVisitId())) {
            try {
                UUID uuid = UUID.randomUUID();
                if (request.getVisitId() == null) {
                    request.setVisitId(uuid);
                } else {
                    uuid = request.getVisitId();
                }

                BookingStatus status = new BookingStatus(uuid);

                RepairStatus response = BookingStatus.BookingStatusToRepairStatus(status);

                bookings.put(uuid, status);
                log.info("Generated visited id: {}. Request: {}", uuid, request);

                kafkaDateProducer.send(
                        new ProducerRecord<UUID, String>(
                                dataRequestTopic,
                                om.writeValueAsString(
                                        RepairDateRequest.builder()
                                                .visitId(request.getVisitId())
                                                .inDays(request.getInDays())
                                                .build()
                                )
                        )
                );
                kafkaDateProducer.flush();
                log.info("Request produced to date stream for {}", request.getVisitId());

                kafkaPartProducer.send(
                        new ProducerRecord<UUID, String>(
                                partRequestTopic,
                                om.writeValueAsString(
                                        RepairPartRequest.builder()
                                                .visitId(request.getVisitId())
                                                .brand(request.getBrand().toString())
                                                .part(request.getPart().toString())
                                                .build()
                                )
                        )
                );
                kafkaPartProducer.flush();
                log.info("Request produced to part stream for {}", request.getVisitId());

                return response;
            } catch (Exception e) {
                log.error("Unexpected exception while booking repair", e);
                throw new GatewayException(request.getVisitId(), "Unexpected exception at gateway. Please contact with administrator");
            }

        } else {
            log.error("Booking for id {} already exists", request.getVisitId());
            throw new ClientErrorException(request.getVisitId(), String.format("Booking for ID %s already exists, request ignored", request.getVisitId()));
        }
    }

    public RepairStatus getRepairStatus(UUID visitId) {
        if (visitId == null) {
            log.error("Null visit ID in getRepairStatus");
            throw new ClientErrorException(visitId, "visitId can not be null. Fill visitId properly");
        }

        if (bookings.containsKey(visitId)) {
            return BookingStatus.BookingStatusToRepairStatus(bookings.get(visitId));
        } else {
            log.error("No booking with ID {}", visitId);
            throw new ClientErrorException(visitId, String.format("No booking for ID %s. Verify your booking ID", visitId));
        }
    }

    public void serveDateResponse(RepairDateResponse response) {
        bookings.get(response.getVisitId()).setDateStatus(Status.SUCCESS, "Date booked successfully");
    }

    public void servePartResponse(RepairPartResponse response) {
        bookings.get(response.getVisitId()).setPartStatus(Status.SUCCESS, "Part booked successfully");
    }

    public void serveDateErrorResponse(OperationError error) {
        bookings.get(error.getVisitId()).setDateStatus(Status.CANCELLED, "Error occurred in date service");
        if (bookings.get(error.getVisitId()).getPartStatus() != Status.CANCELLED)
            bookings.get(error.getVisitId()).setPartStatus(Status.CANCELLED, "Part booking cancelled due to error in date service");
        cancelBothServices(error.getVisitId());
    }

    public void servePartErrorResponse(OperationError error) {
        bookings.get(error.getVisitId()).setPartStatus(Status.CANCELLED, "Error occurred in part service");
        if (bookings.get(error.getVisitId()).getDateStatus() != Status.CANCELLED)
            bookings.get(error.getVisitId()).setDateStatus(Status.CANCELLED, "Date booking cancelled due to error in part service");
        cancelBothServices(error.getVisitId());
    }

    private void cancelBothServices(UUID visitId) {
        this.cancelDateBooking(visitId);
        this.cancelPartBooking(visitId);
    }

    private void cancelDateBooking(UUID visitId) {
        log.info("Sending date booking cancel request for {} to topic {}", visitId, dateCancelTopic);
        kafkaDateProducer.send(
                new ProducerRecord<UUID, String>(
                        dateCancelTopic,
                        visitId.toString()
                )
        );
        kafkaDateProducer.flush();
    }

    private void cancelPartBooking(UUID visitId) {
        log.info("Sending part booking cancel request for {} to topic {}", visitId, partCancelTopic);
        kafkaPartProducer.send(
                new ProducerRecord<UUID, String>(
                        partCancelTopic,
                        visitId.toString()
                )
        );
        kafkaPartProducer.flush();
    }
}
