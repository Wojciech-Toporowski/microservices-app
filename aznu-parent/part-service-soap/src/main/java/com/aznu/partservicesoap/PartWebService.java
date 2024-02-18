package com.aznu.partservicesoap;

import com.aznu.partservicesoap.service.PartService;
import jakarta.jws.WebService;
import lombok.extern.slf4j.Slf4j;
import org.aznu.common.models.services.SoapError;
import org.aznu.common.models.services.part.RepairPartRequest;
import org.aznu.common.models.services.part.RepairPartResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@WebService
@Service
@Slf4j
public class PartWebService {

    private final PartService service;

    @Autowired
    public PartWebService(PartService service) {
        this.service = service;
    }
    public RepairPartResponse bookPart(RepairPartRequest request) throws SoapError {
        log.info("Part call: {}", request);

        RepairPartResponse response = service.bookPart(request);

        log.info("Returning response: {}", response);
        return response;
    }

    public void cancelPartBooking(UUID visitId) {
        log.info("Cancel request for part booking {}", visitId);
        service.cancelBooking(visitId);
    }
}
