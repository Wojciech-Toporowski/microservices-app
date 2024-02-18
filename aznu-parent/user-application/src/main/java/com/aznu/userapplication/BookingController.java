package com.aznu.userapplication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aznu.common.models.gateway.api.RepairRequest;
import org.aznu.common.models.gateway.api.RepairStatus;
import org.aznu.common.models.services.OperationError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Controller
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final RestTemplate restTemplate;
    @Value("${gateway.address}")
    private String gatewayAddress;

    @GetMapping("/bookRepair")
    public String bookRepair(Model model) {
        if (model.getAttribute("repairRequest") == null) {
            model.addAttribute("repairRequest", new RepairRequest());
        }
        return "bookRepair";
    }

    @PostMapping("/bookRepair")
    public String bookRepair(Model model, @ModelAttribute("repairRequest") RepairRequest request) {
        log.info(request.toString());
        try {
            ResponseEntity<RepairStatus> re = restTemplate.postForEntity(
                    gatewayAddress + "/bookRepair",
                    request,
                    RepairStatus.class
            );
            request.setVisitId(re.getBody().getVisitId());
            log.info("Gateway called, returned visitId: {}", request.getVisitId());
            return "bookRepair";
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.info("Calling bookRepair returned {} error", e.getStatusCode(), e);
            return errorPage(e.getResponseBodyAs(OperationError.class), model);
        }
    }

    @GetMapping("/checkBooking")
    public String checkBooking(Model model) {
        if (model.getAttribute("visitId") == null) {
            model.addAttribute("visitId", new CheckBookingRequest());
        }
        return "checkBooking";
    }

    @PostMapping("/checkBooking")
    public String checkBooking(@ModelAttribute("visitId") CheckBookingRequest request, Model model) {
        UUID visitId = request.getVisitId();
        log.info("Visit id: {}", visitId);
        try {
            if (visitId != null) {
                ResponseEntity<RepairStatus> re = restTemplate.getForEntity(
                        String.format("%s/repair/status/%s", gatewayAddress, visitId),
                        RepairStatus.class
                );
                log.info("Gateway called, returned status: {} for {}", re.getBody().getGeneralStatus(), re.getBody().getVisitId());
                model.addAttribute("repairStatus", re.getBody());
            }
            return "checkBooking";
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.info("Calling checkBooking returned {} error", e.getStatusCode(), e);
            return errorPage(e.getResponseBodyAs(OperationError.class), model);
        }
    }

    public String errorPage(OperationError e, Model model) {
        model.addAttribute("error", e);
        return "error";
    }

}
