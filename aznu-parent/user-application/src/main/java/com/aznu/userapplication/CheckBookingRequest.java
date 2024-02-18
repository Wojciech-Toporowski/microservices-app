package com.aznu.userapplication;

import lombok.Data;

import java.util.UUID;

@Data
public class CheckBookingRequest {
    private UUID visitId;
}
