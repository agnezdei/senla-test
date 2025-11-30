package com.agnezdei.hotelmvc.model;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

public class Hotel implements Serializable {
    private String name;
    private List<Room> rooms;
    private List<Service> services;
    private List<Guest> guests;
    private List<Booking> bookings;

    private long nextRoomId = 1;
    private long nextServiceId = 1;
    private long nextGuestId = 1;
    private long nextBookingId = 1;
    
    public Hotel(String name) {
        this.name = name;
        this.rooms = new ArrayList<>();
        this.services = new ArrayList<>();
        this.guests = new ArrayList<>();
        this.bookings = new ArrayList<>();
    }
    
    public String getName() { return name; }
    public List<Room> getRooms() { return new ArrayList<>(rooms); }
    public List<Service> getServices() { return new ArrayList<>(services); }
    public List<Guest> getGuests() { return new ArrayList<>(guests); }
    public List<Booking> getBookings() { return new ArrayList<>(bookings); }

    public Long getNextRoomId() { return nextRoomId++; }
    public Long getNextServiceId() { return nextServiceId++; }
    public Long getNextGuestId() { return nextGuestId++; }
    public Long getNextBookingId() { return nextBookingId++; }

    public void addGuest(Guest guest) {
        guests.add(guest);
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }
    
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

    public Room findRoomById(Long id) {
        for (Room room : rooms) {
            if (room.getId().equals(id)) {
                return room;
            }
        }
        return null;
    }

    public Service findServiceById(Long id) {
        for (Service service : services) {
            if (service.getId().equals(id)) {
                return service;
            }
        }
        return null;
    }

    public Guest findGuestById(Long id) {
        for (Guest guest : guests) {
            if (guest.getId().equals(id)) {
                return guest;
            }
        }
        return null;
    }

    public Booking findBookingById(Long id) {
        for (Booking booking : bookings) {
            if (booking.getId().equals(id)) {
                return booking;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "Отель '" + name + "' (" + rooms.size() + " номеров, " + services.size() + " услуг)";
    }
}