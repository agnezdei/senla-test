package com.agnezdei.hotelmvc;

import com.agnezdei.hotelmvc.controller.*;
import com.agnezdei.hotelmvc.model.*;
import com.agnezdei.hotelmvc.ui.*;
import com.agnezdei.hotelmvc.config.*;

public class HotelApp {
    public static void main(String[] args) {
        System.out.println("=== СИСТЕМА УПРАВЛЕНИЯ ГОСТИНИЦЕЙ ===\n");
        
        AppConfig config = new AppConfig();

        final Hotel hotel;
        Hotel loadedHotel = StateManager.loadState();
        if (loadedHotel == null) {
            hotel = new Hotel("Гранд Отель");
            initializeTestData(hotel, config);
        } else {
            hotel = loadedHotel;
        }
        
        HotelAdmin admin = new HotelAdmin(hotel, config);
        HotelReporter reporter = new HotelReporter(hotel);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nСохранение состояния программы...");
            StateManager.saveState(hotel);
        }));
        
        ConsoleUI ui = new ConsoleUI(admin, reporter);
        
        try {
            ui.start();
        } finally {
            StateManager.saveState(hotel);
        }
    }
        
    private static void initializeTestData(Hotel hotel, AppConfig config) {
        try {
            HotelAdmin tempAdmin = new HotelAdmin(hotel, config);

            tempAdmin.addRoom("101", RoomType.STANDARD, 2500.0, 2, 3);
            tempAdmin.addRoom("102", RoomType.LUXURY, 5000.0, 3, 5);
            tempAdmin.addRoom("103", RoomType.STANDARD, 2000.0, 2, 3);
            
            tempAdmin.addService("Завтрак", 500.0, ServiceCategory.FOOD);
            tempAdmin.addService("Ужин", 800.0, ServiceCategory.FOOD);
            tempAdmin.addService("Уборка", 300.0, ServiceCategory.CLEANING);
        
            Guest guest1 = new Guest(hotel.getNextGuestId(), "Иван Иванов", "444444");
            Guest guest2 = new Guest(hotel.getNextGuestId(), "Петр Петров", "888888");
            hotel.addGuest(guest1);
            hotel.addGuest(guest2);
            
            // System.out.println("Тестируем экспорт...");
            // System.out.println(tempAdmin.exportRoomsToCsv("data/rooms.csv"));
            // System.out.println(tempAdmin.exportServicesToCsv("data/services.csv"));
            // System.out.println(tempAdmin.exportGuestsToCsv("data/guests.csv"));
            // System.out.println(tempAdmin.exportBookingsToCsv("data/bookings.csv"));
            
            System.out.println("\nТестируем импорт...");
            System.out.println(tempAdmin.importRoomsFromCsv("data/rooms.csv"));
            System.out.println(tempAdmin.importServicesFromCsv("data/services.csv"));
            System.out.println(tempAdmin.importGuestsFromCsv("data/guests.csv"));
            System.out.println(tempAdmin.importBookingsFromCsv("data/bookings.csv"));
        
            System.out.println("\n=== РЕЗУЛЬТАТЫ ===");
            System.out.println("Комнат: " + hotel.getRooms().size());
            System.out.println("Услуг: " + hotel.getServices().size());
            System.out.println("Гостей: " + hotel.getGuests().size());
            System.out.println("Бронирований: " + hotel.getBookings().size());
            
            } catch (Exception e) {
            System.out.println("Ошибка при инициализации тестовых данных: " + e.getMessage());
            e.printStackTrace();
        }
    }
}