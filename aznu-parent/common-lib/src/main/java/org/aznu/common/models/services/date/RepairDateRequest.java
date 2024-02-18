package org.aznu.common.models.services.date;

import lombok.*;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepairDateRequest {
    @NonNull
    private UUID visitId;
    private @NonNull int inDays;
}
