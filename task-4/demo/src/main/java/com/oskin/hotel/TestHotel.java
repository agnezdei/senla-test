package com.oskin.hotel;

import java.time.LocalDate;

public class TestHotel {
    public static void main(String[] args) {
        System.out.println("=== СИСТЕМА УПРАВЛЕНИЯ ГОСТИНИЦЕЙ ===\n");
        
        Hotel hotel = new Hotel("Гранд Отель");
        HotelAdmin admin = new HotelAdmin(hotel);
        HotelReporter reporter = new HotelReporter(hotel);
        
        admin.addRoom("101", "Стандарт", 2500, 2, 3);
        admin.addRoom("102", "Люкс", 5000, 3, 5);
        admin.addRoom("103", "Стандарт", 2000, 2, 3);
        admin.addRoom("201", "Бизнес", 3500, 1, 4);
        admin.addRoom("202", "Бизнес", 4000, 2, 4);
        
        admin.addService("Завтрак", 500, "Питание");
        admin.addService("Ужин", 800, "Питание");
        admin.addService("Уборка", 300, "Обслуживание");
        admin.addService("СПА", 1500, "Комфорт");
        admin.addService("Массаж", 1200, "Комфорт");
        
        Guest guest1 = new Guest("Иван Иванов", "1234 567890");
        Guest guest2 = new Guest("Петр Петров", "9876 543210");
        Guest guest3 = new Guest("Сергей Сергеев", "1111 222222");

        System.out.println("\n=== ТЕСТИРОВАНИЕ ОПЕРАЦИЙ ===");
        
        admin.settleGuest("101", guest1, LocalDate.now().minusDays(10), LocalDate.now().minusDays(5));
        admin.addServiceToBooking("101", "Завтрак", LocalDate.now().minusDays(9));
        admin.evictGuest("101");
        admin.setRoomAvailable("101");

        admin.settleGuest("101", guest2, LocalDate.now().minusDays(3), LocalDate.now().plusDays(2));
        admin.addServiceToBooking("101", "Завтрак", LocalDate.now().minusDays(2));
        admin.addServiceToBooking("101", "Уборка", LocalDate.now().plusDays(2));

        admin.settleGuest("102", guest3, LocalDate.now(), LocalDate.now().plusDays(7));
        admin.addServiceToBooking("102", "СПА", LocalDate.now());
        
        admin.changeRoomPrice("102", 4000);
        admin.changeServicePrice("Завтрак", 600);

        System.out.println("\n=== ТЕСТИРОВАНИЕ ОТЧЕТОВ ===");
        
        System.out.println("\n1. Все номера (по цене):");
        for (Room room : reporter.getRoomsSortedByPrice()) {
            System.out.println("  " + room);
        }
        
        System.out.println("\n2. Свободные номера (по вместимости):");
        for (Room room : reporter.getAvailableRoomsSortedByCapacity()) {
            System.out.println("  " + room);
        }
        
        System.out.println("\n3. Постояльцы (по имени):");
        for (Booking booking : reporter.getGuestsAndRoomsSortedByName()) {
            System.out.println("  " + booking.getGuest().getName() + " - номер " + booking.getRoom().getNumber());
        }

       System.out.println("\n4. Постояльцы (по дате выезда):");
        for (Booking booking : reporter.getGuestsAndRoomsSortedByCheckoutDate()) {
            System.out.println("  " + booking.getGuest().getName() + " - выезд: " + booking.getCheckOutDate());
        }
        
        System.out.println("\n5. Общее число свободных номеров: " + reporter.getTotalAvailableRooms());
        System.out.println("\n6. Общее число постояльцев: " + reporter.getTotalGuests());
        
        System.out.println("\n7. Номера, свободные к завтрашнему дню:");
        for (Room room : reporter.getRoomsAvailableByDate(LocalDate.now().plusDays(1))) {
            System.out.println("  " + room);
        }

        System.out.println("\n8. Сумма к оплате за номер 101: " + reporter.getPaymentAmountForRoom("101"));

        System.out.println("\n9. УСЛУГИ ПОСТОЯЛЬЦА:");
        reporter.printGuestServicesSortedByPrice("Петр Петров");
        reporter.printGuestServicesSortedByDate("Петр Петров");
        
        System.out.println("\n10. ПРАЙС-ЛИСТ ОТЕЛЯ (услуги и номера):");
        reporter.printPriceListSortedByCategoryAndPrice();

        System.out.println("\n11. Детали номера 101:");
        reporter.printRoomDetails("101");
    }
}