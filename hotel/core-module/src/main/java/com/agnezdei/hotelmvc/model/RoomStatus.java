package com.agnezdei.hotelmvc.model;

public enum RoomStatus {
    AVAILABLE("available"),
    OCCUPIED("occupied"),
    UNDER_MAINTENANCE("under_maintenance");

    private final String status;

    RoomStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status;
    }
}