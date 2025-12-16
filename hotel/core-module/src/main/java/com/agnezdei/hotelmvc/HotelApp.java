package com.agnezdei.hotelmvc;

import com.agnezdei.hotelmvc.controller.*;
import com.agnezdei.hotelmvc.model.*;
import com.agnezdei.hotelmvc.ui.*;
import com.agnezdei.hotelmvc.config.*;
import com.agnezdei.hotelmvc.di.*;
import com.agnezdei.hotelmvc.csv.*;

public class HotelApp {
    public static void main(String[] args) {
        System.out.println("=== СИСТЕМА УПРАВЛЕНИЯ ГОСТИНИЦЕЙ ===\n");
        
        try {
            System.out.println("Загрузка конфигурации...");
            AppConfig config = new AppConfig();
            ConfigProcessor.process(config);
            System.out.println("Конфигурация загружена:");
            System.out.println("  - Разрешено менять статус комнат: " + config.isAllowRoomStatusChange());
            System.out.println("  - Макс. записей истории: " + config.getMaxBookingHistoryEntries());
            System.out.println();
            
            final Hotel hotel;
            Hotel loadedHotel = StateManager.loadState();
            if (loadedHotel == null) {
                hotel = new Hotel("Гранд Отель");
                System.out.println("Создан новый отель: " + hotel.getName());
            } else {
                hotel = loadedHotel;
                System.out.println("Отель загружен из сохранения: " + hotel.getName());
            }

            System.out.println("\nИнициализация DI контейнера...");
            DependencyContainer container = new DependencyContainer();
            
            container.register(Hotel.class, hotel);
            container.register(AppConfig.class, config);
            

            CsvExporter csvExporter = new CsvExporter();
            container.register(CsvExporter.class, csvExporter);

            RoomCsvImporter roomImporter = container.create(RoomCsvImporter.class);
            GuestCsvImporter guestImporter = container.create(GuestCsvImporter.class);
            ServiceCsvImporter serviceImporter = container.create(ServiceCsvImporter.class);
            BookingCsvImporter bookingImporter = container.create(BookingCsvImporter.class);
            
            container.register(RoomCsvImporter.class, roomImporter);
            container.register(GuestCsvImporter.class, guestImporter);
            container.register(ServiceCsvImporter.class, serviceImporter);
            container.register(BookingCsvImporter.class, bookingImporter);
            
            System.out.println("DI контейнер инициализирован");
            
            System.out.println("Создание контроллеров...");
            HotelAdmin admin = container.create(HotelAdmin.class);
            HotelReporter reporter = container.create(HotelReporter.class);

            container.register(HotelAdmin.class, admin);
            container.register(HotelReporter.class, reporter);

            ConsoleUI ui = container.create(ConsoleUI.class);

            if (loadedHotel == null) {
                System.out.println("\nИнициализация данных из CSV...");
                initializeFromCsv(admin);
            }

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nСохранение состояния программы...");
                StateManager.saveState(hotel);
            }));

            System.out.println("\n" + "=".repeat(50));
            System.out.println("Приложение готово к работе!");
            System.out.println("=".repeat(50) + "\n");
            
            ui.start();
            
        } catch (Exception e) {
            System.err.println("\n!!! КРИТИЧЕСКАЯ ОШИБКА !!!");
            System.err.println("Причина: " + e.getMessage());
            e.printStackTrace();
            System.err.println("\nПрограмма завершена с ошибкой.");
        }
    }
    
    private static void initializeFromCsv(HotelAdmin admin) {
        try {
            System.out.println(admin.importRoomsFromCsv("data/rooms.csv"));
            System.out.println(admin.importServicesFromCsv("data/services.csv"));
            System.out.println(admin.importGuestsFromCsv("data/guests.csv"));
            System.out.println(admin.importBookingsFromCsv("data/bookings.csv"));
            
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке данных из CSV: " + e.getMessage());
            System.out.println("Продолжение работы с пустыми данными...");
        }
    }
}