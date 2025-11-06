package com.agnezdei.hotel;

import java.time.LocalDate;

public class HotelAdmin {
    private Hotel hotel;
    
    public HotelAdmin(Hotel hotel) {
        this.hotel = hotel;
    }
    
    public void settleGuest(String roomNumber, Guest guest, LocalDate checkInDate, LocalDate checkOutDate) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            System.out.println("Ошибка: Номер " + roomNumber + " не найден");
            return;
        }
        
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            System.out.println("Ошибка: Номер " + roomNumber + " недоступен для заселения");
            return;
        }
        
        Booking booking = new Booking(guest, room, checkInDate, checkOutDate);

        room.setCurrentBooking(booking);
        room.setStatus(RoomStatus.OCCUPIED);
        System.out.println("Успех: " + guest.getName() + " заселен в номер " + roomNumber + 
        " с " + checkInDate + " по " + checkOutDate);
    }
    
    public void evictGuest(String roomNumber) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            System.out.println("Ошибка: Номер " + roomNumber + " не найден");
            return;
        }
        
        if (room.getStatus() != RoomStatus.OCCUPIED) {
            System.out.println("Ошибка: Номер " + roomNumber + " не занят");
            return;
        }

        Booking currentBooking = room.getCurrentBooking();
        
        if (currentBooking != null) {
            room.addToHistory(currentBooking);
            String guestName = room.getCurrentBooking().getGuest().getName();

        room.setCurrentBooking(null);
        room.setStatus(RoomStatus.AVAILABLE);
        System.out.println("Успех: " + guestName + " выселен из номера " + roomNumber);
        }
        else {
            System.out.println("Ошибка: В номере " + roomNumber + " нет активного бронирования");
        }
    }
    
    public void setRoomUnderMaintenance(String roomNumber) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            System.out.println("Ошибка: Номер " + roomNumber + " не найден");
            return;
        }
        
        if (room.getStatus() == RoomStatus.OCCUPIED) {
            System.out.println("Ошибка: Нельзя перевести занятый номер на ремонт");
            return;
        }
        
        room.setStatus(RoomStatus.UNDER_MAINTENANCE);
        System.out.println("Успех: Номер " + roomNumber + " переведен на ремонт");
    }
    
    public void setRoomAvailable(String roomNumber) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            System.out.println("Ошибка: Номер " + roomNumber + " не найден");
            return;
        }
        
        room.setStatus(RoomStatus.AVAILABLE);
        System.out.println("Успех: Номер " + roomNumber + " доступен для бронирования");
    }
    
    public void changeRoomPrice(String roomNumber, double newPrice) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            System.out.println("Ошибка: Номер " + roomNumber + " не найден");
            return;
        }
        
        room.setPrice(newPrice);
        System.out.println("Успех: Цена номера " + roomNumber + " изменена на " + newPrice + " руб.");
    }
    
    public void changeServicePrice(String serviceName, double newPrice) {
        Service service = hotel.findService(serviceName);
        if (service == null) {
            System.out.println("Ошибка: Услуга '" + serviceName + "' не найдена");
            return;
        }
        
        service.setPrice(newPrice);
        System.out.println("Успех: Цена услуги '" + serviceName + "' изменена на " + newPrice + " руб.");
    }
    
    public void addRoom(String number, RoomType type, double price, int capacity, int stars) {
        if (hotel.findRoom(number) != null) {
            System.out.println("Ошибка: Номер " + number + " уже существует");
            return;
        }
        
        Room room = new Room(number, type, price, capacity, stars, hotel);
        hotel.addRoom(room);
        System.out.println("Успех: Добавлен номер " + number);
    }
    
    public void addService(String name, double price, ServiceCategory category) {
        if (hotel.findService(name) != null) {
            System.out.println("Ошибка: Услуга '" + name + "' уже существует");
            return;
        }
        
        Service service = new Service(name, price, category, hotel);
        hotel.addService(service);
        System.out.println("Успех: Добавлена услуга '" + name + "'");
    }

    public void addServiceToBooking(String roomNumber, String serviceName, LocalDate serviceDate) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null || room.getCurrentBooking() == null) {
            System.out.println("Ошибка: Номер не найден или не занят");
            return;
        }

        Service service = hotel.findService(serviceName);
        if (service == null) {
            System.out.println("Ошибка: Услуга '" + serviceName + 
            "' не найдена");
            return;
        }

        room.getCurrentBooking().addService(service, serviceDate);
        System.out.println("Успех: Услуга '" + serviceName 
        + "' добавлена к бронированию номера " + roomNumber + " на дату " + serviceDate);
    }
}