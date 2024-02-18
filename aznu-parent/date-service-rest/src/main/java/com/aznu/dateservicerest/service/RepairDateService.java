package com.aznu.dateservicerest.service;

import lombok.extern.slf4j.Slf4j;
import org.aznu.common.models.services.date.RepairDateRequest;
import org.aznu.common.models.services.date.RepairDateResponse;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RepairDateService {
    /**
     * {@code Map<UUID, Boolean>}, value keep information if visit booking is still active: true or false
     */
    private final Map<UUID, DateBooking> visitsMap = new ConcurrentHashMap<>();
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public RepairDateResponse bookRepairDate(RepairDateRequest request) {
        try {
            // booking already exists in service
            if (visitsMap.containsKey(request.getVisitId())) {
                // booking was cancelled before creation, throw error
                if (visitsMap.get(request.getVisitId()).getState() == State.CANCELLED) {
                    throw RepairDateClientException.builder()
                            .visitId(request.getVisitId())
                            .message("Booking was cancelled before creation")
                            .errorCode(ErrorCode.WAS_CANCELLED)
                            .build();
                }
                // booking already exists and is active, no operation
                else {
                    return RepairDateResponse.builder()
                            .active(Boolean.TRUE)
                            .visitId(request.getVisitId())
                            .comment("Booking is already active")
                            .build();
                }
            }

            // no booking for given id before
            visitsMap.computeIfAbsent(request.getVisitId(), r -> {
                // example error for date where day is divided by 3
                DateBooking b;
                if (request.getInDays() % 3 == 0) {
                    b = DateBooking.builder()
                            .visitId(request.getVisitId())
                            .state(State.CANCELLED)
                            .inDays(request.getInDays())
                            .comment("Selected date is not available")
                            .build();
                    return b;
                }
                // create booking
                else {
                    b = DateBooking.builder()
                            .visitId(request.getVisitId())
                            .state(State.BOOKED)
                            .inDays(request.getInDays())
                            .comment("Booking created")
                            .build();
                    return b;
                }
            });
        } catch (Exception e) {
            throw RepairDateSystemException.builder()
                    .visitId(request.getVisitId())
                    .errorCode(ErrorCode.UNEXPECTED_ERROR)
                    .message("Unexpected server error")
                    .build();
        }

        // throw error if booking was cancelled before creation
        if (visitsMap.get(request.getVisitId()).getState() == State.CANCELLED) {
            throw RepairDateClientException.builder()
                    .visitId(request.getVisitId())
                    .errorCode(ErrorCode.WAS_CANCELLED)
                    .message("Booking was already cancelled")
                    .build();
        }
        // return state from service
        else {
            return RepairDateResponse.builder()
                    .visitId(request.getVisitId())
                    .comment("Booking created")
                    .active(true)
                    .build();
        }

    }

    public void cancelBooking(UUID visitId) {
        try {
//        if booking was not present before cancellation
            visitsMap.computeIfAbsent(visitId, r ->
                    DateBooking.builder()
                            .visitId(visitId)
                            .state(State.CANCELLED)
                            .comment("Booking cancelled before completion")
                            .build()
            );
            // if booking was not cancelled, cancel it
            DateBooking b = visitsMap.get(visitId);
            if (b.getState() != State.CANCELLED) {
                b.setState(State.CANCELLED);
                b.setComment("Booking cancelled");
                visitsMap.put(visitId, b);
            }
        } catch (Exception e) {
            throw RepairDateSystemException.builder()
                    .visitId(visitId)
                    .errorCode(ErrorCode.UNEXPECTED_ERROR)
                    .message("Unexpected server error")
                    .build();
        }
    }
}
