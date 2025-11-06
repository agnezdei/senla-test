package com.agnezdei.hotel.ui;

import com.agnezdei.hotel.model.*;
import com.agnezdei.hotel.controller.HotelAdmin;
import com.agnezdei.hotel.controller.HotelReporter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private HotelAdmin admin;
    private HotelReporter reporter;
    private Scanner scanner;
    private DateTimeFormatter dateFormatter;
    
    public ConsoleUI(HotelAdmin admin, HotelReporter reporter) {
        this.admin = admin;
        this.reporter = reporter;
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
                    case ADD_SERVICE_TO_BOOKING:
                        addServiceToBooking();
                        break;
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }
    
    
    private void settleGuest() {
        System.out.print("Введите номер комнаты: ");
        String roomNumber = scanner.nextLine();
        
        System.out.print("Введите имя гостя: ");
        String name = scanner.nextLine();
        
        System.out.print("Введите номер паспорта: ");
        String passport = scanner.nextLine();
        
        System.out.print("Введите дату заселения (гггг-мм-дд): ");
        LocalDate checkIn = parseDate(scanner.nextLine());
        
        System.out.print("Введите дату выселения (гггг-мм-дд): ");
        LocalDate checkOut = parseDate(scanner.nextLine());
        
        Guest guest = new Guest(name, passport);
        String result = admin.settleGuest(roomNumber, guest, checkIn, checkOut);
        System.out.println(result);
    }
    
    private void evictGuest() {
        System.out.print("Введите номер комнаты для выселения: ");
        String roomNumber = scanner.nextLine();
        String result = admin.evictGuest(roomNumber);
        System.out.println(result);
    }
    
    private void setRoomMaintenance() {
        System.out.print("Введите номер комнаты для перевода на ремонт: ");
        String roomNumber = scanner.nextLine();
        String result = admin.setRoomUnderMaintenance(roomNumber);
        System.out.println(result);
    }
    
    private void setRoomAvailable() {
        System.out.print("Введите номер комнаты для перевода в доступные: ");
        String roomNumber = scanner.nextLine();
        String result = admin.setRoomAvailable(roomNumber);
        System.out.println(result);
    }
    
    private void changeRoomPrice() {
        System.out.print("Введите номер комнаты: ");
        String roomNumber = scanner.nextLine();
        
        System.out.print("Введите новую цену: ");
        double newPrice = scanner.nextDouble();
        scanner.nextLine();
        
        String result = admin.changeRoomPrice(roomNumber, newPrice);
        System.out.println(result);
    }
    
    private void changeServicePrice() {
        System.out.print("Введите название услуги: ");
        String serviceName = scanner.nextLine();
        
        System.out.print("Введите новую цену: ");
        double newPrice = scanner.nextDouble();
        scanner.nextLine();
        
        String result = admin.changeServicePrice(serviceName, newPrice);
        System.out.println(result);
    }
    
    private void addRoom() {
        System.out.print("Введите номер комнаты: ");
        String number = scanner.nextLine();
        
        System.out.print("Введите тип (STANDARD/BUSINESS/LUXURY): ");
        RoomType type = RoomType.valueOf(scanner.nextLine().toUpperCase());
        
        System.out.print("Введите цену: ");
        double price = Double.parseDouble(scanner.nextLine());
        
        System.out.print("Введите вместимость: ");
        int capacity = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Введите количество звезд: ");
        int stars = Integer.parseInt(scanner.nextLine());
        
        String result = admin.addRoom(number, type, price, capacity, stars);
        System.out.println(result);
    }
    
    private void addService() {
        System.out.print("Введите название услуги: ");
        String name = scanner.nextLine();
        
        System.out.print("Введите цену услуги: ");
        double price = Double.parseDouble(scanner.nextLine());
        
        System.out.print("Введите категорию (FOOD/CLEANING/COMFORT): ");
        ServiceCategory category = ServiceCategory.valueOf(scanner.nextLine().toUpperCase());
        
        String result = admin.addService(name, price, category);
        System.out.println(result);
    }

    private void addServiceToBooking() {
        System.out.print("Введите номер комнаты: ");
        String roomNumber = scanner.nextLine();
        
        System.out.print("Введите название услуги: ");
        String serviceName = scanner.nextLine();
        
        System.out.print("Введите дату услуги (гггг-мм-дд): ");
        LocalDate serviceDate = parseDate(scanner.nextLine());
        
        String result = admin.addServiceToBooking(roomNumber, serviceName, serviceDate);
        System.out.println(result);
    }
    
    
    private void showAllRoomsByPrice() {
        System.out.println("\n=== ВСЕ НОМЕРА (ПО ЦЕНЕ) ===");
        reporter.getRoomsSortedByPrice().forEach(System.out::println);
    }
    
    private void showAllRoomsByCapacity() {
        System.out.println("\n=== ВСЕ НОМЕРА (ПО ВМЕСТИМОСТИ) ===");
        reporter.getRoomsSortedByCapacity().forEach(System.out::println);
    }
    
    private void showAllRoomsByStars() {
        System.out.println("\n=== ВСЕ НОМЕРА (ПО ЗВЕЗДАМ) ===");
        reporter.getRoomsSortedByStars().forEach(System.out::println);
    }
    
    private void showAvailableRoomsByPrice() {
        System.out.println("\n=== СВОБОДНЫЕ НОМЕРА (ПО ЦЕНЕ) ===");
        reporter.getAvailableRoomsSortedByPrice().forEach(System.out::println);
    }
    
    private void showAvailableRoomsByCapacity() {
        System.out.println("\n=== СВОБОДНЫЕ НОМЕРА (ПО ВМЕСТИМОСТИ) ===");
        reporter.getAvailableRoomsSortedByCapacity().forEach(System.out::println);
    }
    
    private void showAvailableRoomsByStars() {
        System.out.println("\n=== СВОБОДНЫЕ НОМЕРА (ПО ЗВЕЗДАМ) ===");
        reporter.getAvailableRoomsSortedByStars().forEach(System.out::println);
    }
    
    private void showGuestsByName() {
        System.out.println("\n=== ПОСТОЯЛЬЦЫ (ПО ИМЕНИ) ===");
        reporter.getGuestsAndRoomsSortedByName().forEach(booking -> 
            System.out.println(booking.getGuest().getName() + " - номер " + booking.getRoom().getNumber())
        );
    }
    
    private void showGuestsByCheckout() {
        System.out.println("\n=== ПОСТОЯЛЬЦЫ (ПО ДАТЕ ВЫЕЗДА) ===");
        reporter.getGuestsAndRoomsSortedByCheckoutDate().forEach(booking -> 
            System.out.println(booking.getGuest().getName() + " - выезд: " + booking.getCheckOutDate())
        );
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
        LocalDate date = parseDate(scanner.nextLine());
        
        System.out.println("\n=== НОМЕРА СВОБОДНЫЕ НА " + date + " ===");
        reporter.getRoomsAvailableByDate(date).forEach(System.out::println);
    }
    
    private void showPaymentAmount() {
        System.out.print("Введите номер комнаты: ");
        String roomNumber = scanner.nextLine();
        
        double amount = reporter.getPaymentAmountForRoom(roomNumber);
        System.out.println("Сумма к оплате за номер " + roomNumber + ": " + amount + " руб.");
    }
    
    private void showLastThreeGuests() {
        System.out.print("Введите номер комнаты: ");
        String roomNumber = scanner.nextLine();
        
        List<Booking> lastThree = reporter.getLastThreeGuestsOfRoom(roomNumber);
        System.out.println("\n=== 3 ПОСЛЕДНИХ ПОСТОЯЛЬЦА НОМЕРА " + roomNumber + " ===");
        for (Booking booking : lastThree) {
            System.out.println("• " + booking.getGuest().getName() + 
                             " (" + booking.getCheckInDate() + " - " + booking.getCheckOutDate() + ")");
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
    
    
    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, dateFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Неверный формат даты. Используйте гггг-мм-дд");
        }
    }
}