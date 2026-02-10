package com.agnezdei.hotelmvc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {
    @Value("${allow.room.status.change:true}")
    private boolean allowRoomStatusChange;
    
    @Value("${max.booking.history.entries:10}")
    private int maxBookingHistoryEntries;

    public boolean isAllowRoomStatusChange() {
        return allowRoomStatusChange;
    }

    public int getMaxBookingHistoryEntries() {
        return maxBookingHistoryEntries;
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "allowRoomStatusChange=" + allowRoomStatusChange +
                ", maxBookingHistoryEntries=" + maxBookingHistoryEntries +
                '}';
    }
}