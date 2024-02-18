package com.aznu.partservicesoap.service;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.aznu.common.models.gateway.api.Brand;
import org.aznu.common.models.gateway.api.Part;

import java.util.UUID;

@Data
@Builder
public class Booking {
    @NonNull
    private final UUID visitId;
    private State state;
    private final Part part;
    private final Brand brand;
    private String comment;

}
