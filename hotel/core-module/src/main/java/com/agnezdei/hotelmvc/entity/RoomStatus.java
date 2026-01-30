package com.agnezdei.hotelmvc.entity;

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
}