package com.agnezdei.hotelmvc.config;

import com.agnezdei.hotelmvc.annotations.ConfigProperty;

public class AppConfig {
    @ConfigProperty(
        configFileName = "hotel_config.properties",
        propertyName = "allow.room.status.change",
        type = Boolean.class
    )
    private boolean allowRoomStatusChange = true;
    
    @ConfigProperty(
        configFileName = "hotel_config.properties",
        propertyName = "max.booking.history.entries", 
        type = Integer.class
    )
    private int maxBookingHistoryEntries = 10;

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