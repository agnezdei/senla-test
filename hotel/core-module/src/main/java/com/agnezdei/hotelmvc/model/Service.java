package com.agnezdei.hotelmvc.model;

import java.io.Serializable;

public class Service implements Serializable {
    private Long id;
    private String name;
    private double price;
    private ServiceCategory category;
    
    public Service(Long id, String name, double price, ServiceCategory category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public Service() {
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public ServiceCategory getCategory() { return category; }
    public void setCategory(ServiceCategory category) { this.category = category; }
    
    @Override
    public String toString() {
        return category + ": " + name + " - " + price + " руб.";
    }
}