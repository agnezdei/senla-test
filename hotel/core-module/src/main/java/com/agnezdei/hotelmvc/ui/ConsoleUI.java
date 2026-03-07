package com.agnezdei.hotelmvc.ui;

import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.model.RoomType;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import com.agnezdei.hotelmvc.controller.HotelReporter;
import com.agnezdei.hotelmvc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

@Component
public class ConsoleUI {

    private final BookingService bookingService;
    private final RoomService roomService;
    private final ServiceService serviceService;
    private final GuestService guestService;             // может понадобиться для других операций
    private final GuestServiceService guestServiceService;
    private final HotelReporter reporter;

    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter;

    @Autowired
    public ConsoleUI(BookingService bookingService,
                     RoomService roomService,
                     ServiceService serviceService,
                     GuestService guestService,
                     GuestServiceService guestServiceService,
                     HotelReporter reporter) {
        this.bookingService = bookingService;
        this.roomService = roomService;
        this.serviceService = serviceService;
        this.guestService = guestService;
        this.guestServiceService = guestServiceService;
        this.reporter = reporter;
        this.scanner = new Scanner(System.in);
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public void start() {
        // ... (метод start остаётся без изменений, см. ниже)
    }

    // ---- Методы, вызываемые из start (каждый заменяет вызов admin.xxx на вызов соответствующего сервиса) ----

    private void settleGuest() {
        try {
            System.out.print("Введите номер комнаты: ");
            String roomNumber = scanner.nextLine();

            System.out.print("Введите имя гостя: ");
            String name = scanner.nextLine();

            System.out.print("Введите номер паспорта: ");
            String passport = scanner.nextLine();

            LocalDate checkIn = parseDate("Введите дату заселения (гггг-мм-дд): ");
            LocalDate checkOut = parseDate("Введите дату выселения (гггг-мм-дд): ");

            Guest guest = new Guest();
            guest.setName(name);
            guest.setPassportNumber(passport);

            String result = bookingService.settleGuest(roomNumber, guest, checkIn, checkOut);
            System.out.println(result);

        } catch (EntityNotFoundException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (BusinessLogicException e) {
            System.out.println("Невозможно выполнить операцию: " + e.getMessage());
        }
    }

    private void evictGuest() {
        try {
            System.out.print("Введите номер комнаты для выселения: ");
            String roomNumber = scanner.nextLine();
            String result = bookingService.evictGuest(roomNumber);
            System.out.println(result);

        } catch (EntityNotFoundException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (BusinessLogicException e) {
            System.out.println("Невозможно выполнить операцию: " + e.getMessage());
        }
    }

    private void setRoomMaintenance() {
        try {
            System.out.print("Введите номер комнаты для перевода на ремонт: ");
            String roomNumber = scanner.nextLine();
            String result = roomService.setUnderMaintenance(roomNumber);
            System.out.println(result);

        } catch (EntityNotFoundException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (BusinessLogicException e) {
            System.out.println("Невозможно выполнить операцию: " + e.getMessage());
        }
    }

    private void setRoomAvailable() {
        try {
            System.out.print("Введите номер комнаты для перевода в доступные: ");
            String roomNumber = scanner.nextLine();
            String result = roomService.setAvailable(roomNumber);
            System.out.println(result);
        } catch (EntityNotFoundException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (BusinessLogicException e) {
            System.out.println("Невозможно выполнить операцию: " + e.getMessage());
        }
    }

    private void changeRoomPrice() {
        try {
            System.out.print("Введите номер комнаты: ");
            String roomNumber = scanner.nextLine();

            System.out.print("Введите новую цену: ");
            double newPrice = scanner.nextDouble();
            scanner.nextLine();

            String result = roomService.changePrice(roomNumber, newPrice);
            System.out.println(result);
        } catch (EntityNotFoundException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (BusinessLogicException e) {
            System.out.println("Невозможно выполнить операцию: " + e.getMessage());
        }
    }

    private void changeServicePrice() {
        try {
            System.out.print("Введите название услуги: ");
            String serviceName = scanner.nextLine();

            System.out.print("Введите новую цену: ");
            double newPrice = scanner.nextDouble();
            scanner.nextLine();

            String result = serviceService.changePrice(serviceName, newPrice);
            System.out.println(result);
        } catch (EntityNotFoundException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (BusinessLogicException e) {
            System.out.println("Невозможно выполнить операцию: " + e.getMessage());
        }
    }

    private void addRoom() {
        try {
            System.out.print("Введите номер комнаты: ");
            String number = scanner.nextLine();

            System.out.print("Введите тип (STANDARD/BUSINESS/LUXURY): ");
            String typeInput = scanner.nextLine().toUpperCase();
            RoomType type;
            try {
                type = RoomType.valueOf(typeInput);
            } catch (IllegalArgumentException e) {
                System.out.println("Неверный тип комнаты. Доступные варианты: STANDARD, BUSINESS, LUXURY");
                return;
            }

            System.out.print("Введите цену: ");
            double price = Double.parseDouble(scanner.nextLine());

            System.out.print("Введите вместимость: ");
            int capacity = Integer.parseInt(scanner.nextLine());

            System.out.print("Введите количество звезд (1-5): ");
            int stars = Integer.parseInt(scanner.nextLine());
            if (stars < 1 || stars > 5) {
                System.out.println("Количество звезд должно быть от 1 до 5");
                return;
            }

            String result = roomService.addRoom(number, type, price, capacity, stars);
            System.out.println(result);
        } catch (BusinessLogicException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа: " + e.getMessage());
        }
    }

    private void addService() {
        try {
            System.out.print("Введите название услуги: ");
            String name = scanner.nextLine();

            System.out.print("Введите цену услуги: ");
            double price = Double.parseDouble(scanner.nextLine());

            System.out.print("Введите категорию (FOOD/CLEANING/COMFORT): ");
            String categoryInput = scanner.nextLine().toUpperCase();
            ServiceCategory category;
            try {
                category = ServiceCategory.valueOf(categoryInput);
            } catch (IllegalArgumentException e) {
                System.out.println("Неверная категория. Доступные варианты: FOOD, CLEANING, COMFORT");
                return;
            }

            String result = serviceService.addService(name, price, category);
            System.out.println(result);
        } catch (BusinessLogicException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа: " + e.getMessage());
        }
    }

    private void addServiceToGuest() {
        try {
            System.out.print("Введите номер паспорта: ");
            String passportNumber = scanner.nextLine();

            System.out.print("Введите название услуги: ");
            String serviceName = scanner.nextLine();

            System.out.print("Введите дату услуги (гггг-мм-дд): ");
            String dateInput = scanner.nextLine();
            LocalDate serviceDate = LocalDate.parse(dateInput, dateFormatter);

            String result = guestServiceService.addServiceToGuest(passportNumber, serviceName, serviceDate);
            System.out.println(result);
        } catch (EntityNotFoundException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (BusinessLogicException e) {
            System.out.println("Невозможно выполнить операцию: " + e.getMessage());
        } catch (DateTimeParseException e) {
            System.out.println("Ошибка формата даты. Используйте гггг-мм-дд");
        }
    }

    // ---- Методы экспорта/импорта ----

    private void exportRooms() {
        System.out.print("Введите путь для сохранения файла (например: data/rooms.csv): ");
        String filePath = scanner.nextLine();
        String result = roomService.exportToCsv(filePath);
        System.out.println(result);
    }

    private void exportServices() {
        System.out.print("Введите путь для сохранения файла (например: data/services.csv): ");
        String filePath = scanner.nextLine();
        String result = serviceService.exportToCsv(filePath);
        System.out.println(result);
    }

    private void exportGuests() {
        System.out.print("Введите путь для сохранения файла (например: data/guests.csv): ");
        String filePath = scanner.nextLine();
        String result = guestService.exportToCsv(filePath);
        System.out.println(result);
    }

    private void exportBookings() {
        System.out.print("Введите путь для сохранения файла (например: data/bookings.csv): ");
        String filePath = scanner.nextLine();
        String result = bookingService.exportToCsv(filePath);
        System.out.println(result);
    }

    private void exportGuestServices() {
        System.out.print("Введите путь для сохранения файла (например: data/guest_services.csv): ");
        String filePath = scanner.nextLine();
        String result = guestServiceService.exportToCsv(filePath);
        System.out.println(result);
    }

    private void importRooms() {
        System.out.print("Введите путь к файлу для импорта комнат (например: data/rooms.csv): ");
        String filePath = scanner.nextLine();
        String result = roomService.importFromCsv(filePath);
        System.out.println(result);
    }

    private void importServices() {
        System.out.print("Введите путь к файлу для импорта услуг (например: data/services.csv): ");
        String filePath = scanner.nextLine();
        String result = serviceService.importFromCsv(filePath);
        System.out.println(result);
    }

    private void importGuests() {
        System.out.print("Введите путь к файлу для импорта гостей (например: data/guests.csv): ");
        String filePath = scanner.nextLine();
        String result = guestService.importFromCsv(filePath);
        System.out.println(result);
    }

    private void importBookings() {
        System.out.print("Введите путь к файлу для импорта бронирований (например: data/bookings.csv): ");
        String filePath = scanner.nextLine();
        String result = bookingService.importFromCsv(filePath);
        System.out.println(result);
    }

    private void importGuestServices() {
        System.out.print("Введите путь к файлу для импорта услуг гостей (например: data/guest_services.csv): ");
        String filePath = scanner.nextLine();
        String result = guestServiceService.importFromCsv(filePath);
        System.out.println(result);
    }

    // ---- Методы, которые используют HotelReporter (остаются без изменений) ----

    private void showAllRoomsByPrice() {
        reporter.printAllRoomsSortedByPrice();
    }

    private void showAllRoomsByCapacity() {
        reporter.printAllRoomsSortedByCapacity();
    }

    private void showAllRoomsByStars() {
        reporter.printAllRoomsSortedByStars();
    }

    private void showAvailableRoomsByPrice() {
        reporter.printAvailableRoomsSortedByPrice();
    }

    private void showAvailableRoomsByCapacity() {
        reporter.printAvailableRoomsSortedByCapacity();
    }

    private void showAvailableRoomsByStars() {
        reporter.printAvailableRoomsSortedByStars();
    }

    private void showGuestsByName() {
        reporter.printGuestsSortedByName();
    }

    private void showGuestsByCheckout() {
        reporter.printGuestsSortedByCheckoutDate();
    }

    private void showTotalAvailable() {
        int count = reporter.getTotalAvailableRooms();
        System.out.println("\nОбщее число свободных номеров: " + count);
    }

    private void showTotalGuests() {
        int count = reporter.getTotalGuests();
        System.out.println("\nОбщее число постояльцев: " + count);
    }

    private void showRoomsByDate() {
        System.out.print("Введите дату для проверки (гггг-мм-дд): ");
        String dateInput = scanner.nextLine();
        try {
            LocalDate date = LocalDate.parse(dateInput, dateFormatter);
            reporter.printRoomsAvailableByDate(date);
        } catch (DateTimeParseException e) {
            System.out.println("Ошибка формата даты. Используйте гггг-мм-дд");
        }
    }

    private void showPaymentAmount() {
        System.out.print("Введите номер комнаты для расчета оплаты: ");
        String roomNumber = scanner.nextLine();
        reporter.getPaymentForRoom(roomNumber);
    }

    private void showLastThreeGuests() {
        System.out.print("Введите номер комнаты: ");
        String roomNumber = scanner.nextLine();
        System.out.println("\n=== 3 ПОСЛЕДНИХ ПОСТОЯЛЬЦА НОМЕРА " + roomNumber + " ===");
        var lastThree = reporter.getLastThreeGuestsOfRoom(roomNumber);
        if (lastThree.isEmpty()) {
            System.out.println("Нет истории бронирований для этого номера");
        } else {
            for (var booking : lastThree) {
                System.out.println("• " + booking.getGuest().getName() +
                        " (" + booking.getCheckInDate() + " - " + booking.getCheckOutDate() + ")");
            }
        }
    }

    private void showGuestServicesByPrice() {
        System.out.print("Введите имя гостя: ");
        String guestName = scanner.nextLine();
        reporter.printGuestServicesSortedByPrice(guestName);
    }

    private void showGuestServicesByDate() {
        System.out.print("Введите имя гостя: ");
        String guestName = scanner.nextLine();
        reporter.printGuestServicesSortedByDate(guestName);
    }

    private void showPriceList() {
        reporter.printPriceListSortedByCategoryAndPrice();
    }

    private void showRoomDetails() {
        System.out.print("Введите номер комнаты: ");
        String roomNumber = scanner.nextLine();
        reporter.printRoomDetails(roomNumber);
    }

    // ---- Вспомогательные методы ----

    private LocalDate parseDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String dateStr = scanner.nextLine();
                return LocalDate.parse(dateStr, dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Неверный формат даты. Используйте гггг-мм-дд. Попробуйте снова.");
            }
        }
    }
}