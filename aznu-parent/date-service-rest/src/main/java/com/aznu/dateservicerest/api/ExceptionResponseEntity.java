package com.aznu.dateservicerest.api;

import com.aznu.dateservicerest.service.RepairDateClientException;
import com.aznu.dateservicerest.service.RepairDateSystemException;
import lombok.extern.slf4j.Slf4j;
import org.aznu.common.models.services.OperationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class ExceptionResponseEntity extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public final ResponseEntity<OperationError> handleRepairDateClientException(RepairDateClientException exception, WebRequest request){
        log.error("Client exception: ",exception);
        OperationError errorResponse = OperationError.builder()
                .time(LocalDateTime.now())
                .message(exception.getMessage())
                .errorCode(exception.getErrorCode())
                .visitId(exception.getVisitId())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public final ResponseEntity<OperationError> handleRepairDateSystemException(RepairDateSystemException exception, WebRequest request){
        log.error("System exception: ",exception);
        OperationError errorResponse = OperationError.builder()
                .time(LocalDateTime.now())
                .message(exception.getMessage())
                .errorCode(exception.getErrorCode())
                .visitId(exception.getVisitId())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
