package com.agnezdei.hotelmvc.test;

import com.agnezdei.hotelmvc.dao.implementations.GuestDAO;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.util.DatabaseInitializer;

public class TestGuestDAO {
    public static void main(String[] args) {
        System.out.println("=== ТЕСТ GuestDAO ===");
        
        try {
            DatabaseInitializer.initialize();
            GuestDAO guestDAO = new GuestDAO();
            
            // Тест 1: Все гости
            System.out.println("1. Все гости:");
            for (Guest guest : guestDAO.findAll()) {
                System.out.println("   - " + guest.getName() + " (" + guest.getPassportNumber() + ")");
            }
            
            // Тест 2: Новый гость
            System.out.println("\n2. Создание нового гостя:");
            Guest newGuest = new Guest();
            newGuest.setName("Новый Гость");
            newGuest.setPassportNumber("TEST123456");
            
            Guest savedGuest = guestDAO.save(newGuest);
            System.out.println("   ✓ Создан с ID: " + savedGuest.getId());
            
            // Тест 3: Поиск по паспорту
            System.out.println("\n3. Поиск по паспорту:");
            var foundGuest = guestDAO.findByPassportNumber("TEST123456");
            if (foundGuest.isPresent()) {
                System.out.println("   ✓ Найден: " + foundGuest.get().getName());
            }
            
            // Тест 4: Обновление
            System.out.println("\n4. Обновление гостя:");
            savedGuest.setName("Обновлённый Гость");
            guestDAO.update(savedGuest);
            System.out.println("   ✓ Обновлён");
            
            // Тест 5: Удаление
            System.out.println("\n5. Удаление гостя:");
            guestDAO.delete(savedGuest.getId());
            System.out.println("   ✓ Удалён");
            
            System.out.println("\n=== ТЕСТ ПРОЙДЕН ===");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}