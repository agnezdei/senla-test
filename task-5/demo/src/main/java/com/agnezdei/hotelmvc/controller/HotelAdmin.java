package com.agnezdei.hotelmvc.controller;

import java.time.LocalDate;

import com.agnezdei.hotelmvc.model.*;

public class HotelAdmin {
    private Hotel hotel;
    
    public HotelAdmin(Hotel hotel) {
        this.hotel = hotel;
    }
    
    public String settleGuest(String roomNumber, Guest guest, LocalDate checkInDate, LocalDate checkOutDate) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            return "Ошибка: Номер " + roomNumber + " не найден";
        }
        
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            return "Ошибка: Номер " + roomNumber + " недоступен для заселения";
        }
        
        Booking booking = new Booking(guest, room, checkInDate, checkOutDate);
        room.setCurrentBooking(booking);
        room.setStatus(RoomStatus.OCCUPIED);
        return "Успех: " + guest.getName() + " заселен в номер " + roomNumber + " с " + checkInDate + " по " + checkOutDate;
    }
    
    public String evictGuest(String roomNumber) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            return "Ошибка: Номер " + roomNumber + " не найден";
        }
        
        if (room.getStatus() != RoomStatus.OCCUPIED) {
            return "Ошибка: Номер " + roomNumber + " не занят";
        }

        Booking currentBooking = room.getCurrentBooking();
        if (currentBooking != null) {
            room.addToHistory(currentBooking);
            String guestName = currentBooking.getGuest().getName();
            room.setCurrentBooking(null);
            room.setStatus(RoomStatus.AVAILABLE);
            return "Успех: " + guestName + " выселен из номера " + roomNumber;
        }
        return "Ошибка: В номере " + roomNumber + " нет активного бронирования";
    }
    
    public String setRoomUnderMaintenance(String roomNumber) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            return "Ошибка: Номер " + roomNumber + " не найден";
        }
        
        if (room.getStatus() == RoomStatus.OCCUPIED) {
            return "Ошибка: Нельзя перевести занятый номер на ремонт";
        }
        
        room.setStatus(RoomStatus.UNDER_MAINTENANCE);
        return "Успех: Номер " + roomNumber + " переведен на ремонт";
    }
    
    public String setRoomAvailable(String roomNumber) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            return "Ошибка: Номер " + roomNumber + " не найден";
        }
        
        room.setStatus(RoomStatus.AVAILABLE);
        return "Успех: Номер " + roomNumber + " доступен для бронирования";
    }
    
    public String changeRoomPrice(String roomNumber, double newPrice) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            return "Ошибка: Номер " + roomNumber + " не найден";
        }
        
        room.setPrice(newPrice);
        return "Успех: Цена номера " + roomNumber + " изменена на " + newPrice + " руб.";
    }
    
    public String changeServicePrice(String serviceName, double newPrice) {
        Service service = hotel.findService(serviceName);
        if (service == null) {
            return "Ошибка: Услуга '" + serviceName + "' не найдена";
        }
        
        service.setPrice(newPrice);
        return "Успех: Цена услуги '" + serviceName + "' изменена на " + newPrice + " руб.";
    }
    
    public String addRoom(String number, RoomType type, double price, int capacity, int stars) {
        if (hotel.findRoom(number) != null) {
            return "Ошибка: Номер " + number + " уже существует";
        }
        
        Room room = new Room(number, type, price, capacity, stars, hotel);
        hotel.addRoom(room);
        return "Успех: Добавлен номер " + number;
    }
    
    public String addService(String name, double price, ServiceCategory category) {
        if (hotel.findService(name) != null) {
            return "Ошибка: Услуга '" + name + "' уже существует";
        }
        
        Service service = new Service(name, price, category, hotel);
        hotel.addService(service);
        return "Успех: Добавлена услуга '" + name + "'";
    }

    public String addServiceToBooking(String roomNumber, String serviceName, LocalDate serviceDate) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null || room.getCurrentBooking() == null) {
            return "Ошибка: Номер не найден или не занят";
        }

        Service service = hotel.findService(serviceName);
        if (service == null) {
            return "Ошибка: Услуга '" + serviceName + "' не найдена";
        }

        room.getCurrentBooking().addService(service, serviceDate);
        return "Успех: Услуга '" + serviceName + "' добавлена к бронированию номера " + roomNumber + " на дату " + serviceDate;
    }
}