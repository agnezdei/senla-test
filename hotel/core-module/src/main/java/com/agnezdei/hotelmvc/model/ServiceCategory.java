package com.agnezdei.hotelmvc.model;

public enum ServiceCategory {
    FOOD("Питание"),
    CLEANING("Обслуживание"),
    COMFORT("Комфорт");
    
    private final String displayName;
    
    ServiceCategory(String displayName) {
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
