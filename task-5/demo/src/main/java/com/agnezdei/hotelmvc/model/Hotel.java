package com.agnezdei.hotelmvc.model;

import java.util.ArrayList;
import java.util.List;

public class Hotel {
    private String name;
    private List<Room> rooms;
    private List<Service> services;
    
    public Hotel(String name) {
        this.name = name;
        this.rooms = new ArrayList<>();
        this.services = new ArrayList<>();
    }
    
    public String getName() { return name; }
    public List<Room> getRooms() { return new ArrayList<>(rooms); }
    public List<Service> getServices() { return new ArrayList<>(services); }
    
    public void addRoom(Room room) {
        rooms.add(room);
    }
    
    public void addService(Service service) {
        services.add(service);
    }
    
    public Room findRoom(String number) {
        for (Room room : rooms) {
            if (room.getNumber().equals(number)) {
                return room;
            }
        }
        return null;
    }
    
    public Service findService(String name) {
        for (Service service : services) {
            if (service.getName().equals(name)) {
                return service;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "Отель '" + name + "' (" + rooms.size() + " номеров, " + services.size() + " услуг)";
    }
}