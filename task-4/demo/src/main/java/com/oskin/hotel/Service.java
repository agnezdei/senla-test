package com.oskin.hotel;

public class Service {
    private String name;
    private double price;
    private String category;
    private Hotel hotel;
    
    public Service(String name, double price, String category, Hotel hotel) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.hotel = hotel;
    }
    
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public Hotel getHotel() { return hotel; }
    
    public void setPrice(double price) { 
        this.price = price; 
    }
    
    @Override
    public String toString() {
        return category + ": " + name + " - " + price + " руб.";
    }
}