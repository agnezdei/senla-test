package com.agnezdei.hotelmvc;

import com.agnezdei.hotelmvc.config.AppConfig;
import com.agnezdei.hotelmvc.config.ConfigProcessor;
import com.agnezdei.hotelmvc.config.DatabaseConfig;
import com.agnezdei.hotelmvc.controller.HotelAdmin;
import com.agnezdei.hotelmvc.controller.HotelReporter;
import com.agnezdei.hotelmvc.csv.BookingCsvImporter;
import com.agnezdei.hotelmvc.csv.CsvExporter;
import com.agnezdei.hotelmvc.csv.GuestCsvImporter;
import com.agnezdei.hotelmvc.csv.GuestServiceCsvImporter;
import com.agnezdei.hotelmvc.csv.RoomCsvImporter;
import com.agnezdei.hotelmvc.csv.ServiceCsvImporter;
import com.agnezdei.hotelmvc.di.DependencyContainer;
import com.agnezdei.hotelmvc.repository.BookingDAO;
import com.agnezdei.hotelmvc.repository.GuestDAO;
import com.agnezdei.hotelmvc.repository.GuestServiceDAO;
import com.agnezdei.hotelmvc.repository.RoomDAO;
import com.agnezdei.hotelmvc.repository.ServiceDAO;
import com.agnezdei.hotelmvc.ui.ConsoleUI;

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

            DatabaseConfig dbConfig = DatabaseConfig.getInstance();

            System.out.println("Инициализация DI контейнера...");
            DependencyContainer container = new DependencyContainer();

            container.register(AppConfig.class, config);
            container.register(DatabaseConfig.class, dbConfig);

            System.out.println("Создание DAO репозиториев...");

            RoomDAO roomDAO = new RoomDAO();
            GuestDAO guestDAO = new GuestDAO();
            ServiceDAO serviceDAO = new ServiceDAO();
            GuestServiceDAO guestServiceDAO = new GuestServiceDAO();
            BookingDAO bookingDAO = new BookingDAO();

            container.register(BookingDAO.class, bookingDAO);
            container.register(RoomDAO.class, roomDAO);
            container.register(GuestDAO.class, guestDAO);
            container.register(ServiceDAO.class, serviceDAO);
            container.register(GuestServiceDAO.class, guestServiceDAO);

            System.out.println("Создание CSV сервисов...");
            CsvExporter csvExporter = new CsvExporter();
            RoomCsvImporter roomImporter = new RoomCsvImporter();
            GuestCsvImporter guestImporter = new GuestCsvImporter();
            ServiceCsvImporter serviceImporter = new ServiceCsvImporter();
            BookingCsvImporter bookingImporter = new BookingCsvImporter();
            GuestServiceCsvImporter guestServiceImporter = new GuestServiceCsvImporter();

            container.register(CsvExporter.class, csvExporter);
            container.register(RoomCsvImporter.class, roomImporter);
            container.register(GuestCsvImporter.class, guestImporter);
            container.register(ServiceCsvImporter.class, serviceImporter);
            container.register(BookingCsvImporter.class, bookingImporter);
            container.register(GuestServiceCsvImporter.class, guestServiceImporter);

            container.inject(roomImporter);
            container.inject(guestImporter);
            container.inject(serviceImporter);
            container.inject(bookingImporter);
            container.inject(guestServiceImporter);
            container.inject(csvExporter);

            System.out.println("Создание контроллеров...");
            HotelAdmin admin = container.create(HotelAdmin.class);
            HotelReporter reporter = container.create(HotelReporter.class);

            container.register(HotelAdmin.class, admin);
            container.register(HotelReporter.class, reporter);

            System.out.println("\nПроверка состояния базы данных...");
            try {
                int roomCount = roomDAO.findAll().size();
                int guestCount = guestDAO.findAll().size();
                int serviceCount = serviceDAO.findAll().size();
                int bookingCount = bookingDAO.findAll().size();
                int guestServiceCount = guestServiceDAO.findAll().size();

                System.out.println("Найдено в базе данных:");
                System.out.println("  - Комнат: " + roomCount);
                System.out.println("  - Гостей: " + guestCount);
                System.out.println("  - Услуг: " + serviceCount);
                System.out.println("  - Бронирований: " + bookingCount);
                System.out.println("  - Услуг гостей: " + guestServiceCount);

                if (roomCount == 0 && guestCount == 0 && serviceCount == 0) {
                    System.out.println("\nБаза данных пуста. Инициализация из CSV файлов...");
                    initializeFromCsv(admin);

                    System.out.println("\nПосле импорта:");
                    System.out.println("  - Комнат: " + roomDAO.findAll().size());
                    System.out.println("  - Гостей: " + guestDAO.findAll().size());
                    System.out.println("  - Услуг: " + serviceDAO.findAll().size());
                    System.out.println("  - Бронирований: " + bookingDAO.findAll().size());
                    System.out.println("  - Услуг гостей: " + guestServiceDAO.findAll().size());
                } else {
                    System.out.println("\nИспользуется существующая база данных.");
                    System.out.println("Для импорта новых данных используйте соответствующие команды в меню.");
                }
            } catch (Exception e) {
                System.out.println("Ошибка при проверке базы данных: " + e.getMessage());
                e.printStackTrace();
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

            try {
                System.out.println("Импорт услуг гостей...");
                System.out.println("Результат: " + admin.importGuestServicesFromCsv("data/guest_services.csv"));
            } catch (Exception e) {
                System.out.println("Файл guest_services.csv не найден или ошибка импорта: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Ошибка при загрузке данных из CSV: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Продолжение работы с существующими данными в БД...");
        }
    }
}