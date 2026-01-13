package com.agnezdei.hotelmvc.model;

import java.time.LocalDate;

public class ServiceWithDate {
    private Service service;
    private LocalDate date;
    
    public ServiceWithDate() {
    }
    
    public ServiceWithDate(Service service, LocalDate date) {
        this.service = service;
        this.date = date;
    }
    
    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    @Override
    public String toString() {
        return service.getName() + " - " + service.getPrice() + " руб. (дата: " + date + ")";
    }
}