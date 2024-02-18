package org.aznu.common.models.services.date;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairDateResponse {
    private UUID visitId;
    @NonNull
    private String comment;
    @NonNull
    private Boolean active;
}
