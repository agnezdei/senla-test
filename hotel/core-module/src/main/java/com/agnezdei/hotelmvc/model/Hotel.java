package com.agnezdei.hotelmvc.model;

import java.util.ArrayList;
import java.util.List;

import com.agnezdei.hotelmvc.dao.implementations.BookingDAO;
import com.agnezdei.hotelmvc.dao.implementations.GuestDAO;
import com.agnezdei.hotelmvc.dao.implementations.RoomDAO;
import com.agnezdei.hotelmvc.dao.implementations.ServiceDAO;
import com.agnezdei.hotelmvc.exceptions.DAOException;

public class Hotel {
    private String name;
    private List<Room> rooms;
    private List<Service> services;
    private List<Guest> guests;
    private List<Booking> bookings;

    private transient RoomDAO roomDAO;
    private transient GuestDAO guestDAO;
    private transient ServiceDAO serviceDAO;
    private transient BookingDAO bookingDAO;
    
    public Hotel(String name) {
        this.name = name;
        this.rooms = new ArrayList<>();
        this.services = new ArrayList<>();
        this.guests = new ArrayList<>();
        this.bookings = new ArrayList<>();
    }

    public Hotel() {
    }
    
    public void loadFromDatabase() throws DAOException {
        if (roomDAO != null) {
            rooms = roomDAO.findAll();
        }
        if (guestDAO != null) {
            guests = guestDAO.findAll();
        }
        if (serviceDAO != null) {
            services = serviceDAO.findAll();
        }
        if (bookingDAO != null) {
            bookings = bookingDAO.findAll();
        }
    }
    
    public void saveToDatabase() throws DAOException {
        for (Room room : rooms) {
            if (room.getId() == null) {
                roomDAO.save(room);
            } else {
                roomDAO.update(room);
            }
        }
        for (Service service : services) {
            if (service.getId() == null) {
                serviceDAO.save(service);
            } else {
                serviceDAO.update(service);
            }
        }

        for (Guest guest : guests) {
            if (guest.getId() == null) {
                guestDAO.save(guest);
            } else {
                guestDAO.update(guest);
            }
        }

        for (Booking booking : bookings) {
            if (booking.getId() == null) {
                bookingDAO.save(booking);
            } else {
                bookingDAO.update(booking);
            }
        }
    }

    public void setRoomDAO(RoomDAO roomDAO) { this.roomDAO = roomDAO; }
    public void setGuestDAO(GuestDAO guestDAO) { this.guestDAO = guestDAO; }
    public void setServiceDAO(ServiceDAO serviceDAO) { this.serviceDAO = serviceDAO; }
    public void setBookingDAO(BookingDAO bookingDAO) { this.bookingDAO = bookingDAO; }
    
    public String getName() { return name; }
    public List<Room> getRooms() { return new ArrayList<>(rooms); }
    public List<Service> getServices() { return new ArrayList<>(services); }
    public List<Guest> getGuests() { return new ArrayList<>(guests); }
    public List<Booking> getBookings() { return new ArrayList<>(bookings); }


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