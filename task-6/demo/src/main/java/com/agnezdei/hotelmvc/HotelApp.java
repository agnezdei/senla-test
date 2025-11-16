package com.agnezdei.hotelmvc;

import com.agnezdei.hotelmvc.controller.HotelAdmin;
import com.agnezdei.hotelmvc.controller.HotelReporter;
import com.agnezdei.hotelmvc.model.*;
import com.agnezdei.hotelmvc.ui.ConsoleUI;

public class HotelApp {
    public static void main(String[] args) {
        System.out.println("=== СИСТЕМА УПРАВЛЕНИЯ ГОСТИНИЦЕЙ ===\n");
        
        Hotel hotel = new Hotel("Гранд Отель");
        HotelAdmin admin = new HotelAdmin(hotel);
        HotelReporter reporter = new HotelReporter(hotel);
        
        try {
            admin.addRoom("101", RoomType.STANDARD, 2500.0, 2, 3);
            admin.addRoom("102", RoomType.LUXURY, 5000.0, 3, 5);
            admin.addRoom("103", RoomType.STANDARD, 2000.0, 2, 3);
            
            admin.addService("Завтрак", 500.0, ServiceCategory.FOOD);
            admin.addService("Ужин", 800.0, ServiceCategory.FOOD);
            admin.addService("Уборка", 300.0, ServiceCategory.CLEANING);
        
            Guest guest1 = new Guest(hotel.getNextGuestId(), "Иван Иванов", "444444");
            Guest guest2 = new Guest(hotel.getNextGuestId(), "Петр Петров", "888888");
            hotel.addGuest(guest1);
            hotel.addGuest(guest2);
            
            // System.out.println("Тестируем экспорт...");
            // System.out.println(admin.exportRoomsToCsv("rooms.csv"));
            // System.out.println(admin.exportServicesToCsv("services.csv"));
            // System.out.println(admin.exportGuestsToCsv("guests.csv"));
            // System.out.println(admin.exportBookingsToCsv("bookings.csv"));
            
            System.out.println("\nТестируем импорт...");
            System.out.println(admin.importRoomsFromCsv("rooms.csv"));
            System.out.println(admin.importServicesFromCsv("services.csv"));
            System.out.println(admin.importGuestsFromCsv("guests.csv"));
            System.out.println(admin.importBookingsFromCsv("bookings.csv"));
        
            System.out.println("\n=== РЕЗУЛЬТАТЫ ===");
            System.out.println("Комнат: " + hotel.getRooms().size());
            System.out.println("Услуг: " + hotel.getServices().size());
            System.out.println("Гостей: " + hotel.getGuests().size());
            System.out.println("Бронирований: " + hotel.getBookings().size());
        } catch (Exception e) {
            System.out.println("Ошибка при инициализации: " + e.getMessage());
            e.printStackTrace();
        }

        ConsoleUI ui = new ConsoleUI(admin, reporter);
        ui.start();
    }
}