package com.agnezdei.hotelmvc.config;

import java.io.*;
import java.util.Properties;

public class AppConfig {
    private static final String CONFIG_FILE = "hotel_config.properties";
    private Properties properties;
    
    private static final String ALLOW_ROOM_STATUS_CHANGE = "allow.room.status.change";
    private static final String MAX_BOOKING_HISTORY_ENTRIES = "max.booking.history.entries";
    
    private static final boolean DEFAULT_ALLOW_STATUS_CHANGE = true;
    private static final int DEFAULT_MAX_HISTORY_ENTRIES = 10;
    
    public AppConfig() {
        properties = new Properties();
        loadConfig();
    }
    
    private void loadConfig() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            setDefaultValues();
        }
    }
    
    private void setDefaultValues() {
        properties.setProperty(ALLOW_ROOM_STATUS_CHANGE, String.valueOf(DEFAULT_ALLOW_STATUS_CHANGE));
        properties.setProperty(MAX_BOOKING_HISTORY_ENTRIES, String.valueOf(DEFAULT_MAX_HISTORY_ENTRIES));
    }
    
    public boolean isAllowRoomStatusChange() {
        return Boolean.parseBoolean(properties.getProperty(ALLOW_ROOM_STATUS_CHANGE, 
                                String.valueOf(DEFAULT_ALLOW_STATUS_CHANGE)));
    }
    
    public int getMaxBookingHistoryEntries() {
        try {
            return Integer.parseInt(properties.getProperty(MAX_BOOKING_HISTORY_ENTRIES, 
                                    String.valueOf(DEFAULT_MAX_HISTORY_ENTRIES)));
        } catch (NumberFormatException e) {
            return DEFAULT_MAX_HISTORY_ENTRIES;
        }
    }
}