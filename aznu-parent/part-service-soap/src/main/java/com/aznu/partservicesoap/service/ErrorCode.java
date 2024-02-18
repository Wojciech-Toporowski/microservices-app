package com.aznu.partservicesoap.service;

import lombok.Getter;

@Getter
public enum ErrorCode {
    WAS_CANCELLED("02");

    private final String code;

    private ErrorCode(String code) {
        this.code = code;
    }
}