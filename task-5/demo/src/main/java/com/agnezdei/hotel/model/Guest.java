package com.agnezdei.hotel.model;

public class Guest {
    private String name;
    private String passportNumber;
    
    public Guest(String name, String passportNumber) {
        this.name = name;
        this.passportNumber = passportNumber;
    }
    
    public String getName() { return name; }
    public String getPassportNumber() { return passportNumber; }
    
    @Override
    public String toString() {
        return name + " (паспорт: " + passportNumber + ")";
    }
}