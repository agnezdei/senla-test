package com.agnezdei.hotelmvc.model;

import java.io.Serializable;

public class Room implements Serializable {
    private Long id;
    private String number;
    private RoomType type;
    private double price;
    private RoomStatus status;
    private int capacity;
    private int stars;

    public Room(String number, RoomType type, double price, int capacity, int stars) {
        this.number = number;
        this.type = type;
        this.price = price;
        this.status = RoomStatus.AVAILABLE;
        this.capacity = capacity;
        this.stars = stars;
    }

    public Room() {
        this.status = RoomStatus.AVAILABLE;
    }

    public Long getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getStars() {
        return stars;
    }

    public String getNumber() {
        return number;
    }

    public RoomType getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Номер " + number + " (" + type + ") - " + price + " руб. [" + status +
                "], Вместимость: " + capacity + ", Звёзды: " + stars;
    }
}