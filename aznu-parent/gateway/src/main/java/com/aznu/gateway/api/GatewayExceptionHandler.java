package com.aznu.gateway.api;

import com.aznu.gateway.service.ClientErrorException;
import com.aznu.gateway.service.GatewayException;
import lombok.extern.slf4j.Slf4j;
import org.aznu.common.models.services.OperationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GatewayExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler
    public final ResponseEntity<OperationError> handleClientException(ClientErrorException exception) {
        OperationError errorResponse = OperationError.builder()
                .time(LocalDateTime.now())
                .message(exception.getMessage())
                .errorCode("01-Client exception")
                .visitId(exception.getVisitId())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public final ResponseEntity<OperationError> handleGatewayException(GatewayException exception) {
        OperationError errorResponse = OperationError.builder()
                .time(LocalDateTime.now())
                .message(exception.getMessage())
                .errorCode("02-Gateway error")
                .visitId(exception.getVisitId())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public final ResponseEntity<OperationError> generalExceptionHandler(Exception e) {
        log.error("Unexpected error: ", e);
        GatewayException gatewayException = new GatewayException(null, "Unexpected error at server");
        return handleGatewayException(gatewayException);
    }

}
