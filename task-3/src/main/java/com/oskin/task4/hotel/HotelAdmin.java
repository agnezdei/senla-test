package com.oskin.task4.hotel;

public class HotelAdmin {
    private Hotel hotel;
    
    public HotelAdmin(Hotel hotel) {
        this.hotel = hotel;
    }
    
    public void settleGuest(String roomNumber, Guest guest) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            System.out.println("Ошибка: Номер " + roomNumber + " не найден");
            return;
        }
        
        if (!"available".equals(room.getStatus())) {
            System.out.println("Ошибка: Номер " + roomNumber + " недоступен для заселения");
            return;
        }
        
        Booking booking = new Booking(guest, room);
        room.setCurrentBooking(booking);
        room.setStatus("occupied");
        System.out.println("Успех: " + guest.getName() + " заселен в номер " + roomNumber);
    }
    
    public void evictGuest(String roomNumber) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            System.out.println("Ошибка: Номер " + roomNumber + " не найден");
            return;
        }
        
        if (!"occupied".equals(room.getStatus())) {
            System.out.println("Ошибка: Номер " + roomNumber + " не занят");
            return;
        }
        
        String guestName = room.getCurrentBooking().getGuest().getName();
        room.setCurrentBooking(null);
        room.setStatus("available");
        System.out.println("Успех: " + guestName + " выселен из номера " + roomNumber);
    }
    
    public void setRoomUnderMaintenance(String roomNumber) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            System.out.println("Ошибка: Номер " + roomNumber + " не найден");
            return;
        }
        
        if ("occupied".equals(room.getStatus())) {
            System.out.println("Ошибка: Нельзя перевести занятый номер на ремонт");
            return;
        }
        
        room.setStatus("under_maintenance");
        System.out.println("Успех: Номер " + roomNumber + " переведен на ремонт");
    }
    
    public void setRoomAvailable(String roomNumber) {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            System.out.println("Ошибка: Номер " + roomNumber + " не найден");
            return;
        }
        
        room.setStatus("available");
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
    
    public void addRoom(String number, String type, double price) {
        if (hotel.findRoom(number) != null) {
            System.out.println("Ошибка: Номер " + number + " уже существует");
            return;
        }
        
        Room room = new Room(number, type, price, hotel);
        hotel.addRoom(room);
        System.out.println("Успех: Добавлен номер " + number);
    }
    
    public void addService(String name, double price) {
        if (hotel.findService(name) != null) {
            System.out.println("Ошибка: Услуга '" + name + "' уже существует");
            return;
        }
        
        Service service = new Service(name, price, hotel);
        hotel.addService(service);
        System.out.println("Успех: Добавлена услуга '" + name + "'");
    }
    
    public void showHotelInfo() {
        System.out.println("\n=== ИНФОРМАЦИЯ ОБ ОТЕЛЕ ===");
        System.out.println(hotel);
        
        System.out.println("\nНОМЕРА:");
        for (Room room : hotel.getRooms()) {
            System.out.println("  " + room);
            if (room.getCurrentBooking() != null) {
                System.out.println("    Занят: " + room.getCurrentBooking().getGuest());
            }
        }
        
        System.out.println("\nУСЛУГИ:");
        for (Service service : hotel.getServices()) {
            System.out.println("  " + service);
        }
    }
}