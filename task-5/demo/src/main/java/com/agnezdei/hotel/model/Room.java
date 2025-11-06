package com.agnezdei.hotel.model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String number;
    private RoomType type;
    private double price;
    private RoomStatus status;
    private int capacity;
    private int stars;
    private Booking currentBooking;
    private Hotel hotel;
    private List<Booking> bookingHistory;

    public Room(String number, RoomType type, double price, int capacity, int stars, Hotel hotel) {
        this.number = number;
        this.type = type;
        this.price = price;
        this.status = RoomStatus.AVAILABLE;
        this.capacity = capacity;
        this.stars = stars;
        this.hotel = hotel;
        this.bookingHistory = new ArrayList<>();
    }

    public List<Booking> getBookingHistory() { return new ArrayList<>(bookingHistory); }

    public void addToHistory(Booking booking) {
        this.bookingHistory.add(booking);
    }
    
    public int getCapacity() { return capacity; }
    public int getStars() { return stars; }
    public String getNumber() { return number; }
    public RoomType getType() { return type; }
    public double getPrice() { return price; }
    public RoomStatus getStatus() { return status; }
    public Booking getCurrentBooking() { return currentBooking; }
    public Hotel getHotel() { return hotel; }
    
    public void setPrice(double price) { this.price = price; }
    public void setStatus(RoomStatus status) { this.status = status; }
    public void setCurrentBooking(Booking booking) { this.currentBooking = booking; }
    
    @Override
    public String toString() {
        return "Номер " + number + " (" + type + ") - " + price + " руб. [" + status + 
               "], Вместимость: " + capacity + ", Звёзды: " + stars;
    }
}