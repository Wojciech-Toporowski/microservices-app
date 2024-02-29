package com.aznu.dateservicerest.service;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class DateBooking {
    @NonNull
    private final UUID visitId;
    private Date dateTime;
    private String comment;
    private State state;
}
