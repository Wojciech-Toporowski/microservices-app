package org.aznu.common.models.services;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoapError extends Exception{
    @NonNull
    protected String time;
    @NonNull
    protected String message;
    @NonNull
    protected String errorCode;
    protected UUID visitId;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void setTime(LocalDateTime dateTime) {
        this.time = dateTime.format(formatter);
    }

    /**
     * <b>Used by jackson to deserialize, do not remove !!!</b>
     */
    public void setTime(String dateTimeString) {
        this.time = dateTimeString;
    }


    public static class SoapErrorBuilder {
        public SoapError.SoapErrorBuilder time(LocalDateTime dateTime) {
            this.time = dateTime.format(formatter);
            return this;
        }

        /**
         * <b>Used by jackson to deserialize, do not remove !!!</b>
         */
        public SoapError.SoapErrorBuilder time(String dateTime) {
            this.time = dateTime;
            return this;
        }

    }
}
