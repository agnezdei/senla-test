package com.oskin.task4.hotel;

public class Room {
    private String number;
    private String type;
    private double price;
    private String status;
    private Booking currentBooking;
    private Hotel hotel;
    
    public Room(String number, String type, double price, Hotel hotel) {
        this.number = number;
        this.type = type;
        this.price = price;
        this.status = "available";
        this.hotel = hotel;
    }
    
    public String getNumber() { return number; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }
    public Booking getCurrentBooking() { return currentBooking; }
    public Hotel getHotel() { return hotel; }
    
    public void setPrice(double price) { this.price = price; }
    public void setStatus(String status) { this.status = status; }
    public void setCurrentBooking(Booking booking) { this.currentBooking = booking; }
    
    @Override
    public String toString() {
        return "Номер " + number + " (" + type + ") - " + price + " руб. [" + status + "]";
    }
}