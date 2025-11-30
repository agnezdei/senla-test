package com.agnezdei.hotelmvc.model;

import java.io.Serializable;

public class Service implements Serializable {
    private Long id;
    private String name;
    private double price;
    private ServiceCategory category;
    private Hotel hotel;
    
    public Service(Long id, String name, double price, ServiceCategory category, Hotel hotel) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.hotel = hotel;
    }
    
    public Long getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public ServiceCategory getCategory() { return category; }
    public Hotel getHotel() { return hotel; }
    
    public void setPrice(double price) { 
        this.price = price; 
    }
    
    @Override
    public String toString() {
        return category + ": " + name + " - " + price + " руб.";
    }
}