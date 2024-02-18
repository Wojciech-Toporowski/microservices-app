package org.aznu.common.models.services.part;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairPartResponse {
    private UUID visitId;
    @NonNull
    private String comment;
    @NonNull
    private Boolean active;
}
