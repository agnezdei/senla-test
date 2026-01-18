package com.agnezdei.hotelmvc.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Booking implements Serializable {
    private Long id;
    private Guest guest;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private boolean isActive;
    
    public Booking() {
        this.isActive = true;
    }
    
    public Booking(Long id, Guest guest, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        this.id = id;
        this.guest = guest;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.isActive = true;
    }
    
    // Геттеры и сеттеры
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
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public double calculateTotalPrice() {
        long days = checkOutDate.toEpochDay() - checkInDate.toEpochDay();
        return room.getPrice() * days;
    }
    
    @Override
    public String toString() {
        return "Бронирование [id=" + id + ", гость=" + (guest != null ? guest.getName() : "null") 
               + ", номер=" + (room != null ? room.getNumber() : "null") 
               + ", " + checkInDate + " - " + checkOutDate 
               + ", активен=" + isActive + "]";
    }
}