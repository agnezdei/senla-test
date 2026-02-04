package com.agnezdei.hotelmvc.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "booking")
public class Booking implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
    
    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;
    
    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
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
    
    public Booking(Guest guest, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        this();
        this.guest = guest;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
    
    public boolean isActive() {
        return isActive != null && isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
    
    public double calculateTotalPrice() {
        if (room == null || room.getPrice() == null || checkInDate == null || checkOutDate == null) {
            return 0.0;
        }
        
        try {
            long days = checkOutDate.toEpochDay() - checkInDate.toEpochDay();
            return room.getPrice() * days;
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    @Override
    public String toString() {
        return "Бронирование [id=" + id + ", гость=" + (guest != null ? guest.getName() : "null")
                + ", номер=" + (room != null ? room.getNumber() : "null")
                + ", " + checkInDate + " - " + checkOutDate
                + ", активен=" + isActive + "]";
    }
}