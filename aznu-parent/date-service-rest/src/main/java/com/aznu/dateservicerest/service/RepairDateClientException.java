package com.aznu.dateservicerest.service;

import lombok.Builder;

import java.util.UUID;

public class RepairDateClientException extends RepairDateException{
    @Builder
    public RepairDateClientException(UUID visitId , ErrorCode errorCode, String message){
        super(visitId, errorCode, message);

    }
}
