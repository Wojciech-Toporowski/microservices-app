package com.aznu.dateservicerest.service;

import lombok.Builder;

import java.util.UUID;

public class RepairDateSystemException extends RepairDateException{
    @Builder
    public RepairDateSystemException(UUID visitId , ErrorCode errorCode, String message){
        super(visitId, errorCode, message);

    }
}
