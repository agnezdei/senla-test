package com.agnezdei.hotel.model;

public enum RoomType {
    STANDARD("Стандарт"),
    BUSINESS("Бизнес"),
    LUXURY("Люкс");
    
    private final String displayName;
    
    RoomType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
