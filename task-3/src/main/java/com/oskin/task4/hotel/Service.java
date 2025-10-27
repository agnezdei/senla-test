package com.oskin.task4.hotel;

public class Service {
    private String name;
    private double price;
    private Hotel hotel;
    
    public Service(String name, double price, Hotel hotel) {
        this.name = name;
        this.price = price;
        this.hotel = hotel;
    }
    
    public String getName() { return name; }
    public double getPrice() { return price; }
    public Hotel getHotel() { return hotel; }
    
    public void setPrice(double price) { 
        this.price = price; 
    }
    
    @Override
    public String toString() {
        return name + " - " + price + " руб.";
    }
}