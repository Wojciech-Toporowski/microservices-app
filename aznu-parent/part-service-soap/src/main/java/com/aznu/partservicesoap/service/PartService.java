package com.aznu.partservicesoap.service;

import org.aznu.common.models.gateway.api.Brand;
import org.aznu.common.models.gateway.api.Part;
import org.aznu.common.models.services.SoapError;
import org.aznu.common.models.services.part.RepairPartRequest;
import org.aznu.common.models.services.part.RepairPartResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PartService {
    private final Map<UUID, Booking> bookingsMap = new ConcurrentHashMap<>();

    public RepairPartResponse bookPart(RepairPartRequest request) throws SoapError {

        // booking is already present in service
        if (bookingsMap.containsKey(request.getVisitId())) {
            // if booking was cancelled before creation throw error
            if (bookingsMap.get(request.getVisitId()).getState() == State.CANCELLED) {
                String m = "Booking was already cancelled";
                throw SoapError.builder()
                        .visitId(request.getVisitId())
                        .time(LocalDateTime.now())
                        .errorCode(ErrorCode.WAS_CANCELLED.getCode() + "-" + m)
                        .message(m)
                        .build();
            }
            // booking already exists and is active, no operation
            else {
                return RepairPartResponse.builder()
                        .active(Boolean.TRUE)
                        .visitId(request.getVisitId())
                        .comment("Booking already exists and is active")
                        .build();
            }
        }

        // no booking for given id before
        bookingsMap.computeIfAbsent(request.getVisitId(), r -> {
                    Booking b;
                    // Example error
                    if (Brand.valueOf(request.getBrand()) == Brand.Volkswagen) {
                        b = Booking.builder()
                                .visitId(request.getVisitId())
                                .state(State.CANCELLED)
                                .brand(Brand.valueOf(request.getBrand()))
                                .part(Part.valueOf(request.getPart()))
                                .comment("Parts for Volkswagen unavailable")
                                .build();
                    }
                    // create booking
                    else {
                        b = Booking.builder()
                                .visitId(request.getVisitId())
                                .state(State.BOOKED)
                                .part(Part.valueOf(request.getPart()))
                                .brand(Brand.valueOf(request.getBrand()))
                                .comment("Booking created")
                                .build();
                    }
                    return b;
                }
        );
        // throw error if booking was cancelled before creation
        if (bookingsMap.get(request.getVisitId()).getState() == State.CANCELLED) {
            throw SoapError.builder()
                    .visitId(request.getVisitId())
                    .time(LocalDateTime.now())
                    .errorCode(ErrorCode.WAS_CANCELLED.getCode() + "-" + bookingsMap.get(request.getVisitId()).getComment())
                    .message(bookingsMap.get(request.getVisitId()).getComment())
                    .build();
        }
        // return state from service
        else {
            return RepairPartResponse.builder()
                    .visitId(request.getVisitId())
                    .comment(bookingsMap.get(request.getVisitId()).getComment())
                    .active(Boolean.TRUE)
                    .build();
        }
    }

    public void cancelBooking(UUID visitId) {
        // if booking was not present create it and mark as cancelled
        bookingsMap.computeIfAbsent(visitId, r -> Booking.builder()
                .visitId(visitId)
                .state(State.CANCELLED)
                .comment("Booking cancelled before completion")
                .build());
        // if booking was not cancelled, cancel it
        if (bookingsMap.get(visitId).getState() != State.CANCELLED) {
            Booking b = bookingsMap.get(visitId);
            b.setState(State.CANCELLED);
            b.setComment("Booking cancelled");
            bookingsMap.put(visitId, b);
        }
    }
}
