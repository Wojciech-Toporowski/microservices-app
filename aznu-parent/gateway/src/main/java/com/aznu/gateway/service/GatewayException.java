package com.aznu.gateway.service;

import lombok.Getter;

import java.util.UUID;

@Getter
public class GatewayException extends RuntimeException {
    protected final UUID visitId;

    public GatewayException(UUID visitId, String message) {
        super(message);
        this.visitId = visitId;
    }
}