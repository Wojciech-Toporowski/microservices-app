package org.aznu.common.models.gateway.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairStatus {
    @NonNull
    private Status dateStatus;
    @NonNull
    private Status partStatus;
    @NonNull
    private Status generalStatus;
    @NonNull
    private String dateComment;
    @NonNull
    private String partComment;
    @NonNull
    private UUID visitId;

}
