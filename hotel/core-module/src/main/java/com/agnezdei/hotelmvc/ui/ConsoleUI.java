package com.agnezdei.hotelmvc.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import com.agnezdei.hotelmvc.annotations.Inject;
import com.agnezdei.hotelmvc.controller.HotelAdmin;
import com.agnezdei.hotelmvc.controller.HotelReporter;
import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.model.RoomType;
import com.agnezdei.hotelmvc.model.ServiceCategory;

public class ConsoleUI {
    @Inject
    private HotelAdmin admin;
    @Inject
    private HotelReporter reporter;
    private Scanner scanner;
    private DateTimeFormatter dateFormatter;
    
    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }
    
    public void start() {
        System.out.println("=== СИСТЕМА УПРАВЛЕНИЯ ГОСТИНИЦЕЙ ===");
        
        while (true) {
            MenuCommand.printMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            MenuCommand command = MenuCommand.fromCode(choice);
            if (command == null) {
                System.out.println("Неверный выбор!");
                continue;
            }
            
            try {
                switch (command) {
                    case EXIT:
                        System.out.println("Выход из системы...");
                        return;
                        
                    case SETTLE_GUEST:
                        settleGuest();
                        break;
                        
                    case EVICT_GUEST:
                        evictGuest();
                        break;
                        
                    case SET_ROOM_MAINTENANCE:
                        setRoomMaintenance();
                        break;
                        
                    case SET_ROOM_AVAILABLE:
                        setRoomAvailable();
                        break;
                        
                    case CHANGE_ROOM_PRICE:
                        changeRoomPrice();
                        break;
                        
                    case CHANGE_SERVICE_PRICE:
                        changeServicePrice();
                        break;
                        
                    case ADD_ROOM:
                        addRoom();
                        break;
                        
                    case ADD_SERVICE:
                        addService();
                        break;
                        
                    case SHOW_ALL_ROOMS_PRICE:
                        showAllRoomsByPrice();
                        break;
                        
                    case SHOW_ALL_ROOMS_CAPACITY:
                        showAllRoomsByCapacity();
                        break;
                        
                    case SHOW_ALL_ROOMS_STARS:
                        showAllRoomsByStars();
                        break;
                        
                    case SHOW_AVAILABLE_ROOMS_PRICE:
                        showAvailableRoomsByPrice();
                        break;
                        
                    case SHOW_AVAILABLE_ROOMS_CAPACITY:
                        showAvailableRoomsByCapacity();
                        break;
                        
                    case SHOW_AVAILABLE_ROOMS_STARS:
                        showAvailableRoomsByStars();
                        break;
                        
                    case SHOW_GUESTS_NAME:
                        showGuestsByName();
                        break;
                        
                    case SHOW_GUESTS_CHECKOUT:
                        showGuestsByCheckout();
                        break;
                        
                    case SHOW_TOTAL_AVAILABLE:
                        showTotalAvailable();
                        break;
                        
                    case SHOW_TOTAL_GUESTS:
                        showTotalGuests();
                        break;
                        
                    case SHOW_ROOMS_BY_DATE:
                        showRoomsByDate();
                        break;
                        
                    case SHOW_PAYMENT_AMOUNT:
                        showPaymentAmount();
                        break;
                        
                    case SHOW_LAST_THREE_GUESTS:
                        showLastThreeGuests();
                        break;
                        
                    case SHOW_GUEST_SERVICES_PRICE:
                        showGuestServicesByPrice();
                        break;
                        
                    case SHOW_GUEST_SERVICES_DATE:
                        showGuestServicesByDate();
                        break;
                        
                    case SHOW_PRICE_LIST:
                        showPriceList();
                        break;
                        
                    case SHOW_ROOM_DETAILS:
                        showRoomDetails();
                        break;
                    case ADD_SERVICE_TO_GUEST:
                        addServiceToGuest();
                        break;
                    case EXPORT_ROOMS:
                        exportRooms();
                        break;
                    case EXPORT_SERVICES:
                        exportServices();
                        break;
                    case EXPORT_GUESTS:
                        exportGuests();
                        break;
                    case EXPORT_BOOKINGS:
                        exportBookings();
                        break;
                    case EXPORT_GUEST_SERVICES:
                        exportGuestServices();
                        break;
                    case IMPORT_ROOMS:
                        importRooms();
                        break;
                    case IMPORT_SERVICES:
                        importServices();
                        break;
                    case IMPORT_GUESTS:
                        importGuests();
                        break;
                    case IMPORT_BOOKINGS:
                        importBookings();
                        break;
                    case IMPORT_GUESTS_SERVICES:
                        importGuestServices();
                        break;
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }
    
    
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
            
            String result = admin.settleGuest(roomNumber, guest, checkIn, checkOut);
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
            String result = admin.evictGuest(roomNumber);
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
            String result = admin.setRoomUnderMaintenance(roomNumber);
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
            String result = admin.setRoomAvailable(roomNumber);
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
            
            String result = admin.changeRoomPrice(roomNumber, newPrice);
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
            
            String result = admin.changeServicePrice(serviceName, newPrice);
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
            
            String result = admin.addRoom(number, type, price, capacity, stars);
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
            
            String result = admin.addService(name, price, category);
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
            
            String result = admin.addServiceToGuest(passportNumber, serviceName, serviceDate);
            System.out.println(result);
        } catch (EntityNotFoundException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (BusinessLogicException e) {
            System.out.println("Невозможно выполнить операцию: " + e.getMessage());
        } catch (DateTimeParseException e) {
            System.out.println("Ошибка формата даты. Используйте гггг-мм-дд");
        }
    }

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

    private void exportRooms() {
        System.out.print("Введите путь для сохранения файла (например: data/rooms.csv): ");
        String filePath = scanner.nextLine();
        String result = admin.exportRoomsToCsv(filePath);
        System.out.println(result);
    }

    private void exportServices() {
        System.out.print("Введите путь для сохранения файла (например: data/services.csv): ");
        String filePath = scanner.nextLine();
        String result = admin.exportServicesToCsv(filePath);
        System.out.println(result);
    }

    private void exportGuests() {
        System.out.print("Введите путь для сохранения файла (например: data/guests.csv): ");
        String filePath = scanner.nextLine();
        String result = admin.exportGuestsToCsv(filePath);
        System.out.println(result);
    }

    private void exportBookings() {
        System.out.print("Введите путь для сохранения файла (например: data/bookings.csv): ");
        String filePath = scanner.nextLine();
        String result = admin.exportBookingsToCsv(filePath);
        System.out.println(result);
    }

    private void exportGuestServices() {
        System.out.print("Введите путь для сохранения файла (например: data/guest_services.csv): ");
        String filePath = scanner.nextLine();
        String result = admin.exportGuestServicesToCsv(filePath);
        System.out.println(result);
    }

    private void importRooms() {
        System.out.print("Введите путь к файлу для импорта комнат (например: data/rooms.csv): ");
        String filePath = scanner.nextLine();
        String result = admin.importRoomsFromCsv(filePath);
        System.out.println(result);
    }

    private void importServices() {
        System.out.print("Введите путь к файлу для импорта услуг (например: data/services.csv): ");
        String filePath = scanner.nextLine();
        String result = admin.importServicesFromCsv(filePath);
        System.out.println(result);
    }

    private void importGuests() {
        System.out.print("Введите путь к файлу для импорта гостей (например: data/guests.csv): ");
        String filePath = scanner.nextLine();
        String result = admin.importGuestsFromCsv(filePath);
        System.out.println(result);
    }

    private void importBookings() {
        System.out.print("Введите путь к файлу для импорта бронирований (например: data/bookings.csv): ");
        String filePath = scanner.nextLine();
        String result = admin.importBookingsFromCsv(filePath);
        System.out.println(result);
    }

    private void importGuestServices() {
        System.out.print("Введите путь к файлу для импорта услуг гостей (например: data/guest_services.csv): ");
        String filePath = scanner.nextLine();
        String result = admin.importGuestServicesFromCsv(filePath);
        System.out.println(result);
    }

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