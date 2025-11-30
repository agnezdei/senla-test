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
    
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPassportNumber() { return passportNumber; }
    
    @Override
    public String toString() {
        return name + " (паспорт: " + passportNumber + ")";
    }
}