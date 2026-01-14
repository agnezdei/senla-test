package com.agnezdei.hotelmvc.model;

import java.time.LocalDate;

public class GuestService {
    private Long id;
    private Guest guest;
    private Service service;
    private LocalDate serviceDate;
    
    public GuestService() {
    }
    
    public GuestService(Guest guest, Service service, LocalDate serviceDate) {
        this.guest = guest;
        this.service = service;
        this.serviceDate = serviceDate;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Guest getGuest() { return guest; }
    public void setGuest(Guest guest) { this.guest = guest; }
    
    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }
    
    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }
    
    @Override
    public String toString() {
        return "GuestService [id=" + id + ", guest=" + (guest != null ? guest.getName() : "null") 
               + ", service=" + (service != null ? service.getName() : "null") 
               + ", serviceDate=" + serviceDate + "]";
    }
}