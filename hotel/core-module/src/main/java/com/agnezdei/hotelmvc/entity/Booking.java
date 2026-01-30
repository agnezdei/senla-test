package com.agnezdei.hotelmvc.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "booking")
public class Booking {
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
    private String checkInDate;
    
    @Column(name = "check_out_date", nullable = false)
    private String checkOutDate;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
    public Booking() {
        this.isActive = true;
    }
    
    public Booking(Guest guest, Room room, String checkInDate, String checkOutDate) {
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

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
    
    public Double calculateTotalPrice() {
        if (room == null || room.getPrice() == null || checkInDate == null || checkOutDate == null) {
            return 0.0;
        }
        
        try {
            LocalDate in = LocalDate.parse(checkInDate);
            LocalDate out = LocalDate.parse(checkOutDate);
            long days = out.toEpochDay() - in.toEpochDay();
            return room.getPrice() * days;
        } catch (Exception e) {
            return 0.0;
        }
    }
}