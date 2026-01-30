package com.agnezdei.hotelmvc.dto;

public class BookingDTO {
    private Long id;
    private String guestName;
    private String roomNumber;
    private String checkInDate;
    private String checkOutDate;
    private boolean active;
    
    public BookingDTO() {}
    
    public BookingDTO(Long id, String guestName, String roomNumber, 
                     String checkInDate, String checkOutDate, boolean active) {
        this.id = id;
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.active = active;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    
    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }
    
    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    @Override
    public String toString() {
        return "Бронирование [id=" + id + ", гость=" + guestName 
               + ", номер=" + roomNumber + ", " + checkInDate 
               + " - " + checkOutDate + ", активен=" + active + "]";
    }
}