package com.aznu.gateway.service;

import java.util.UUID;

public class ClientErrorException extends GatewayException {

    public ClientErrorException(UUID visitId, String message) {
        super(visitId, message);
    }
}
