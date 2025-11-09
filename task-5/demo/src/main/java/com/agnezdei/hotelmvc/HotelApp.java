package com.agnezdei.hotelmvc;

import java.time.LocalDate;

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
        
        admin.addRoom("101", RoomType.STANDARD, 2500, 2, 3);
        admin.addRoom("102", RoomType.LUXURY, 5000, 3, 5);
        admin.addRoom("103", RoomType.STANDARD, 2000, 2, 3);
        
        admin.addService("Завтрак", 500, ServiceCategory.FOOD);
        admin.addService("Ужин", 800, ServiceCategory.FOOD);
        admin.addService("Уборка", 300, ServiceCategory.CLEANING);
        
        ConsoleUI ui = new ConsoleUI(admin, reporter);
        ui.start();
    }
}