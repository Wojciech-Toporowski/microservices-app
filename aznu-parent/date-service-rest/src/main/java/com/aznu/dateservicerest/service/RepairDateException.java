package com.aznu.dateservicerest.service;

import lombok.Getter;

import java.util.UUID;

@Getter

public abstract class RepairDateException extends RuntimeException {
    protected final String errorCode;
    protected final String CODE_PREFIX = "DATE-SERVICE:";
    protected final UUID visitId;

    public RepairDateException(UUID visitId , ErrorCode errorCode, String message){
        super(message);
        this.visitId = visitId;
        StringBuilder sb = new StringBuilder(CODE_PREFIX);
        this.errorCode = sb.append(errorCode.getCode()).toString();
    }
}
