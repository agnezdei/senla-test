package com.agnezdei.hotelmvc.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Booking implements Serializable {
    private Long id;
    private Guest guest;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private List<ServiceWithDate> services;
    private boolean isActive;
    
    public Booking(Long id, Guest guest, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        this.id = id;
        this.guest = guest;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }
    
    public Booking() {
        this.services = new ArrayList<>();
        this.isActive = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Guest getGuest() { return guest; }
    public void setGuest(Guest guest) { this.guest = guest; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    
    public List<ServiceWithDate> getServices() { 
        if (services == null) {
            services = new ArrayList<>();
        }
        return new ArrayList<>(services); 
    }
    public void setServices(List<ServiceWithDate> services) { this.services = services; }
   
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public static class ServiceWithDate implements Serializable{
        private Service service;
        private LocalDate date;
        
        public ServiceWithDate(Service service, LocalDate date) {
            this.service = service;
            this.date = date;
        }

        public ServiceWithDate(){
        }
        
        public Service getService() { return service; }
        public void setService(Service service) {this.service = service; }
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
    }

    public void addService(Service service, LocalDate serviceDate) {
        this.services.add(new ServiceWithDate(service, serviceDate));
    }

    public double calculateTotalPrice() {
        long days = checkOutDate.toEpochDay() - checkInDate.toEpochDay();
        double roomCost = room.getPrice() * days;
        
        double servicesCost = 0;
        for (ServiceWithDate serviceWithDate : services) {
            servicesCost += serviceWithDate.getService().getPrice();
        }
        
        return roomCost + servicesCost;
    }

    @Override
    public String toString() {
        return "Бронирование: " + guest.getName() + " в номере " + room.getNumber() + 
               " (" + checkInDate + " - " + checkOutDate + ")";
    }
}