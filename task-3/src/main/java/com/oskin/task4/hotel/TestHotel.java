package com.oskin.task4.hotel;

public class TestHotel {
    public static void main(String[] args) {
        System.out.println("=== СИСТЕМА УПРАВЛЕНИЯ ГОСТИНИЦЕЙ ===\n");
        
        Hotel hotel = new Hotel("Гранд Отель");
        HotelAdmin admin = new HotelAdmin(hotel);
        
        admin.addRoom("101", "Стандарт", 2500);
        admin.addRoom("102", "Люкс", 5000);
        admin.addRoom("201", "Бизнес", 3500);
        
        admin.addService("Завтрак", 500);
        admin.addService("Уборка", 300);
        admin.addService("СПА", 1500);
        
        Guest guest1 = new Guest("Иван Иванов", "1234 567890");
        Guest guest2 = new Guest("Петр Петров", "9876 543210");
        
        System.out.println("\n=== ТЕСТИРОВАНИЕ ОПЕРАЦИЙ ===");
        
        admin.settleGuest("101", guest1);
        admin.settleGuest("102", guest2);
        
        admin.settleGuest("101", new Guest("Сергей Сергеев", "1111 222222"));
        
        admin.changeRoomPrice("201", 4000);
        admin.changeServicePrice("Завтрак", 600);
        
        admin.setRoomUnderMaintenance("201");
        
        admin.setRoomUnderMaintenance("101");
        
        admin.evictGuest("101");
        
        admin.setRoomUnderMaintenance("101");
        
        admin.setRoomAvailable("101");
        
        admin.showHotelInfo();
    }
}