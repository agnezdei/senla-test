package com.agnezdei.hotelmvc.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Booking {
    private Guest guest;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private List<ServiceWithDate> services;
    private boolean isActive;
    
    public Booking(Guest guest, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        this.guest = guest;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.services = new ArrayList<>();
        this.isActive = true;
    }
    
    public Guest getGuest() { return guest; }
    public Room getRoom() { return room; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public List<ServiceWithDate> getServices() { return new ArrayList<>(services); }
    public boolean isActive() { return isActive; }

    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    public void setActive(boolean active) { isActive = active; }
    
    public static class ServiceWithDate {
        private Service service;
        private LocalDate date;
        
        public ServiceWithDate(Service service, LocalDate date) {
            this.service = service;
            this.date = date;
        }
        
        public Service getService() { return service; }
        public LocalDate getDate() { return date; }
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