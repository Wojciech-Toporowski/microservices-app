package com.aznu.dateservicerest.service;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@Builder
public class DateBooking {
    @NonNull
    private final UUID visitId;
    private int inDays;
    private String comment;
    private State state;
}
