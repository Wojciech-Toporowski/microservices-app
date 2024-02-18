package com.aznu.gateway.api;

import com.aznu.gateway.service.GatewayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.aznu.common.models.gateway.api.RepairRequest;
import org.aznu.common.models.gateway.api.RepairStatus;
import org.aznu.common.models.services.OperationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@Slf4j
public class GatewayApi {
    private final GatewayService service;

    @Autowired
    public GatewayApi(GatewayService service) {
        this.service = service;
    }

    @Operation(
            summary = "Booking car repair",
            description = "Endpoint allows to book car repair. After receiving request, service automatically books" +
                    " required parts and demanded repair date. Service returns identifier, " +
                    "that allows to check status of booking.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(
                                                    implementation = RepairStatus.class
                                            )
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Occurred error caused by client mistake",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = OperationError.class))
                            }
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Occurred unexpected server error",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(
                                                    implementation = OperationError.class))
                            }
                    )
            }
    )
    @PostMapping("/bookRepair")
    public RepairStatus bookRepair(@RequestBody RepairRequest request) {
        log.info("Repair request coming from user: {}", request);
        RepairStatus res = service.bookRepair(request);
        log.info("Returned response: {}", res);
        return res;
    }

    @Operation(
            summary = "Check status of repair booking",
            description = "Endpoint allows to check status of client repair booking, based on identifier returned by booking endpoint.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(
                                                    implementation = RepairStatus.class
                                            )
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Occurred error caused by client mistake",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = OperationError.class))
                            }
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Occurred unexpected server error",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(
                                                    implementation = OperationError.class))
                            }
                    )
            }
    )
    @GetMapping("/repair/status/{visitId}")
    public RepairStatus repairStatus(
            @Parameter(
                    name =  "visitId",
                    description  = "Identifier of booking",
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    required = true)
            @PathVariable UUID visitId) {
        log.info("Incoming status check from user to {}", visitId);
        RepairStatus res = service.getRepairStatus(visitId);
        log.info("Repair status: {}", res);
        return res;
    }

}
