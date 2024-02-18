package com.aznu.gateway.service;

import lombok.Data;
import lombok.NonNull;
import org.aznu.common.models.gateway.api.RepairStatus;
import org.aznu.common.models.gateway.api.Status;

import java.util.UUID;

@Data
public class BookingStatus {
    @NonNull
    private volatile Status dateStatus = Status.IN_PROGRESS;
    @NonNull
    private volatile Status partStatus = Status.IN_PROGRESS;
    @NonNull
    private volatile Status generalStatus = Status.IN_PROGRESS;
    @NonNull
    private volatile String dateComment = "In progress";
    @NonNull
    private volatile String partComment = "InProgress";
    @NonNull
    private final UUID visitId;

    public synchronized void setDateStatus(@NonNull Status s) {
        if (this.dateStatus != Status.CANCELLED)
            this.dateStatus = s;
        this.verifyGeneralStatus();
    }

    public synchronized void setDateStatus(@NonNull Status s, @NonNull String dateComment) {
        this.dateComment = dateComment;
        this.setDateStatus(s);
    }

    public synchronized void setPartStatus(@NonNull Status s) {
        if (this.partStatus != Status.CANCELLED)
            this.partStatus = s;
        this.verifyGeneralStatus();
    }

    public synchronized void setPartStatus(@NonNull Status s, @NonNull String partComment) {
        this.partComment = partComment;
        this.setPartStatus(s);
    }

    public synchronized void setGeneralStatus(@NonNull Status s) {
        this.generalStatus = s;
    }

    public synchronized @NonNull Status getDateStatus() {
        return this.dateStatus;
    }

    public synchronized @NonNull Status getPartStatus() {
        return this.partStatus;
    }

    private synchronized void verifyGeneralStatus() {
        if (this.getDateStatus() == Status.SUCCESS && this.getPartStatus() == Status.SUCCESS) {
            this.setGeneralStatus(Status.SUCCESS);
        } else if (this.getPartStatus() == Status.CANCELLED || this.dateStatus == Status.CANCELLED) {
            this.setGeneralStatus(Status.CANCELLED);
        }
    }

    public static RepairStatus BookingStatusToRepairStatus(BookingStatus bs) {
        return RepairStatus.builder()
                .dateStatus(bs.getDateStatus())
                .dateComment(bs.getDateComment())
                .partStatus(bs.getPartStatus())
                .partComment(bs.getPartComment())
                .generalStatus(bs.getGeneralStatus())
                .visitId(bs.getVisitId())
                .build();
    }
}
