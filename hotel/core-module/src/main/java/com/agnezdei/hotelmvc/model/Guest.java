package com.agnezdei.hotelmvc.model;

import java.io.Serializable;

public class Guest implements Serializable {
    private Long id;
    private String name;
    private String passportNumber;

    public Guest(Long id, String name, String passportNumber) {
        this.id = id;
        this.name = name;
        this.passportNumber = passportNumber;
    }

    public Guest() {
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String pasportNumber) { this.passportNumber = pasportNumber; }
    
    @Override
    public String toString() {
        return name + " (паспорт: " + passportNumber + ")";
    }
}