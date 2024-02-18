package com.aznu.dateservicerest.service;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INCORRECT_DATE("01"), WAS_CANCELLED("02"), ERROR_OCCURRED("03"), INCORRECT_ID("04"), UNEXPECTED_ERROR("05");

    private final String code;
    private ErrorCode(String code){
        this.code = code;
    }
}
