package org.aznu.common.models.gateway.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class RepairRequest {
    private int inDays;
    private Brand brand;
    private Part part;
    private UUID visitId;
}
