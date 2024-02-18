package org.aznu.common.models.gateway.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public enum Brand {
    Ford, Opel, Fiat, Volkswagen, Toyota
}
