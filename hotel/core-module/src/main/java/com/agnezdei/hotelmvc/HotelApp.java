package com.agnezdei.hotelmvc;

import com.agnezdei.hotelmvc.config.AppConfig;
import com.agnezdei.hotelmvc.controller.HotelAdmin;
import com.agnezdei.hotelmvc.controller.HotelReporter;
import com.agnezdei.hotelmvc.csv.BookingCsvImporter;
import com.agnezdei.hotelmvc.csv.CsvExporter;
import com.agnezdei.hotelmvc.csv.GuestCsvImporter;
import com.agnezdei.hotelmvc.csv.RoomCsvImporter;
import com.agnezdei.hotelmvc.csv.ServiceCsvImporter;
import com.agnezdei.hotelmvc.dao.implementations.BookingDAO;
import com.agnezdei.hotelmvc.dao.implementations.GuestDAO;
import com.agnezdei.hotelmvc.dao.implementations.RoomDAO;
import com.agnezdei.hotelmvc.dao.implementations.ServiceDAO;
import com.agnezdei.hotelmvc.di.DependencyContainer;
import com.agnezdei.hotelmvc.model.Hotel;
import com.agnezdei.hotelmvc.ui.ConsoleUI;

public class HotelApp {
    public static void main(String[] args) {
        System.out.println("=== СИСТЕМА УПРАВЛЕНИЯ ГОСТИНИЦЕЙ ===\n");
        
        try {
            System.out.println("Загрузка конфигурации...");
            AppConfig config = new AppConfig();
            System.out.println("Конфигурация загружена:");
            System.out.println("  - Разрешено менять статус комнат: " + config.isAllowRoomStatusChange());
            System.out.println("  - Макс. записей истории: " + config.getMaxBookingHistoryEntries());
            System.out.println();
            
            System.out.println("Инициализация DI контейнера и зависимостей...");
            DependencyContainer container = new DependencyContainer();
            
            // Регистрация DAO
            RoomDAO roomDAO = new RoomDAO();
            GuestDAO guestDAO = new GuestDAO();
            ServiceDAO serviceDAO = new ServiceDAO();
            BookingDAO bookingDAO = new BookingDAO();
            
            container.register(RoomDAO.class, roomDAO);
            container.register(GuestDAO.class, guestDAO);
            container.register(ServiceDAO.class, serviceDAO);
            container.register(BookingDAO.class, bookingDAO);
            
            container.register(AppConfig.class, config);
            
            CsvExporter csvExporter = new CsvExporter();
            RoomCsvImporter roomImporter = new RoomCsvImporter();
            GuestCsvImporter guestImporter = new GuestCsvImporter();
            ServiceCsvImporter serviceImporter = new ServiceCsvImporter();
            BookingCsvImporter bookingImporter = new BookingCsvImporter();
            
            container.register(CsvExporter.class, csvExporter);
            container.register(RoomCsvImporter.class, roomImporter);
            container.register(GuestCsvImporter.class, guestImporter);
            container.register(ServiceCsvImporter.class, serviceImporter);
            container.register(BookingCsvImporter.class, bookingImporter);
            
            container.inject(roomImporter);
            container.inject(guestImporter);
            container.inject(serviceImporter);
            container.inject(bookingImporter);
            container.inject(csvExporter);
            
            Hotel hotel = new Hotel("Гранд Отель");
            container.register(Hotel.class, hotel);
            
            System.out.println("Создание контроллеров...");
            HotelAdmin admin = container.create(HotelAdmin.class);
            HotelReporter reporter = container.create(HotelReporter.class);
            
            container.register(HotelAdmin.class, admin);
            container.register(HotelReporter.class, reporter);
            
            // Проверка состояния базы данных и инициализация при необходимости
            System.out.println("\nПроверка состояния базы данных...");
            try {
                int roomCount = roomDAO.findAll().size();
                int guestCount = guestDAO.findAll().size();
                int serviceCount = serviceDAO.findAll().size();
                
                System.out.println("Найдено в базе данных:");
                System.out.println("  - Комнат: " + roomCount);
                System.out.println("  - Гостей: " + guestCount);
                System.out.println("  - Услуг: " + serviceCount);
                
                if (roomCount == 0 && guestCount == 0 && serviceCount == 0) {
                    System.out.println("\nБаза данных пуста. Инициализация из CSV файлов...");
                    initializeFromCsv(admin);
                    
                    System.out.println("\nПосле импорта:");
                    System.out.println("  - Комнат: " + roomDAO.findAll().size());
                    System.out.println("  - Гостей: " + guestDAO.findAll().size());
                    System.out.println("  - Услуг: " + serviceDAO.findAll().size());
                    System.out.println("  - Бронирований: " + bookingDAO.findAll().size());
                } else {
                    System.out.println("\nИспользуется существующая база данных.");
                }
            } catch (Exception e) {
                System.out.println("Ошибка при проверке базы данных: " + e.getMessage());
                System.out.println("Продолжение работы...");
            }
            
            ConsoleUI ui = container.create(ConsoleUI.class);
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("Приложение готово к работе!");
            System.out.println("Данные хранятся в базе данных.");
            System.out.println("=".repeat(60) + "\n");
            
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
            System.out.println("Импорт комнат...");
            System.out.println("Результат: " + admin.importRoomsFromCsv("data/rooms.csv"));
            
            System.out.println("Импорт услуг...");
            System.out.println("Результат: " + admin.importServicesFromCsv("data/services.csv"));
            
            System.out.println("Импорт гостей...");
            System.out.println("Результат: " + admin.importGuestsFromCsv("data/guests.csv"));
            
            System.out.println("Импорт бронирований...");
            System.out.println("Результат: " + admin.importBookingsFromCsv("data/bookings.csv"));
            
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке данных из CSV: " + e.getMessage());
            System.out.println("Продолжение работы с существующими данными в БД...");
        }
    }
}