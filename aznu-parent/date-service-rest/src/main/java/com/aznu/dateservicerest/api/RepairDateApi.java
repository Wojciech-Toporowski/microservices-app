package com.aznu.dateservicerest.api;

import com.aznu.dateservicerest.service.ErrorCode;
import com.aznu.dateservicerest.service.RepairDateClientException;
import com.aznu.dateservicerest.service.RepairDateService;
import lombok.extern.slf4j.Slf4j;
import org.aznu.common.models.services.date.RepairDateRequest;
import org.aznu.common.models.services.date.RepairDateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Slf4j
public class RepairDateApi {

    private RepairDateService service;

    @Autowired
    public RepairDateApi(RepairDateService s){
        service = s;
    }

    @PostMapping("/repair/date")
    public RepairDateResponse requestDate(@RequestBody RepairDateRequest request){
        log.info("Incoming request: {}", request);
        RepairDateResponse response = service.bookRepairDate(request);
        log.info("Response: {}", response);
        return response;
    }

    @DeleteMapping("/repair/date/{id}")
    public void deleteDate(@PathVariable UUID id){
        log.info("Incoming request for deleting booking with ID: {}", id);
        if(id == null){
            throw new RepairDateClientException(id, ErrorCode.INCORRECT_ID, "Received ID is null");
        }
        service.cancelBooking(id);

    }
}
