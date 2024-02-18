package org.aznu.common.models.gateway.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public enum Part {
    bumper, engine, battery, absorber
}
