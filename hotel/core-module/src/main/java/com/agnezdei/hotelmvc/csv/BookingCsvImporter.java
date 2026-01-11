package com.agnezdei.hotelmvc.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.agnezdei.hotelmvc.annotations.Inject;
import com.agnezdei.hotelmvc.dao.implementations.BookingDAO;
import com.agnezdei.hotelmvc.dao.implementations.GuestDAO;
import com.agnezdei.hotelmvc.dao.implementations.RoomDAO;
import com.agnezdei.hotelmvc.dao.implementations.ServiceDAO;
import com.agnezdei.hotelmvc.model.Booking;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.model.Room;
import com.agnezdei.hotelmvc.model.RoomStatus;

public class BookingCsvImporter {
    @Inject
    private BookingDAO bookingDAO;
    @Inject
    private GuestDAO guestDAO;
    @Inject
    private RoomDAO roomDAO;
    @Inject
    private ServiceDAO serviceDAO;

    public BookingCsvImporter() {
    }

public String importBookings(String filePath) {
    List<String> errors = new ArrayList<>();
    int imported = 0;
    int updated = 0;
    
    System.out.println("\n=== Начало импорта бронирований ===");
    
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line = reader.readLine();
        System.out.println("Заголовок: " + line);
        
        int lineNum = 1;
        while ((line = reader.readLine()) != null) {
            lineNum++;
            System.out.println("\nСтрока " + lineNum + ": " + line);
            
            try {
                String[] data = line.split(",");
                System.out.println("Количество полей: " + data.length);
                
                if (data.length < 5) {
                    String error = "Строка " + lineNum + ": Недостаточно данных (" + data.length + " из 5)";
                    errors.add(error);
                    System.out.println("ОШИБКА: " + error);
                    continue;
                }
                
                String guestPassport = data[0].trim();
                String roomNumber = data[1].trim();
                LocalDate checkInDate = LocalDate.parse(data[2].trim());
                LocalDate checkOutDate = LocalDate.parse(data[3].trim());
                boolean isActive = Boolean.parseBoolean(data[4].trim());
                
                System.out.println("Парсинг:");
                System.out.println("  Паспорт гостя: " + guestPassport);
                System.out.println("  Номер комнаты: " + roomNumber);
                System.out.println("  Дата заезда: " + checkInDate);
                System.out.println("  Дата выезда: " + checkOutDate);
                System.out.println("  Активно: " + isActive);
                
                // Ищем гостя по паспорту
                Optional<Guest> guestOpt = guestDAO.findByPassportNumber(guestPassport);
                if (guestOpt.isEmpty()) {
                    String error = "Строка " + lineNum + ": Гость с паспортом " + guestPassport + " не найден";
                    errors.add(error);
                    System.out.println("ОШИБКА: " + error);
                    
                    // Выводим всех гостей в БД для отладки
                    System.out.println("Доступные гости в БД:");
                    List<Guest> allGuests = guestDAO.findAll();
                    for (Guest g : allGuests) {
                        System.out.println("  - " + g.getName() + " (паспорт: " + g.getPassportNumber() + ")");
                    }
                    
                    continue;
                }
                
                // Ищем комнату по номеру
                Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
                if (roomOpt.isEmpty()) {
                    String error = "Строка " + lineNum + ": Комната с номером " + roomNumber + " не найдена";
                    errors.add(error);
                    System.out.println("ОШИБКА: " + error);
                    
                    // Выводим все комнаты в БД для отладки
                    System.out.println("Доступные комнаты в БД:");
                    List<Room> allRooms = roomDAO.findAll();
                    for (Room r : allRooms) {
                        System.out.println("  - " + r.getNumber() + " (" + r.getType() + ")");
                    }
                    
                    continue;
                }
                
                Guest guest = guestOpt.get();
                Room room = roomOpt.get();
                
                // Проверяем, существует ли уже такое бронирование
                List<Booking> existingBookings = bookingDAO.findBookingsByRoom(room.getId());
                Booking existingBooking = null;
                
                for (Booking booking : existingBookings) {
                    if (booking.getGuest().getId().equals(guest.getId()) &&
                        booking.getCheckInDate().equals(checkInDate) &&
                        booking.getCheckOutDate().equals(checkOutDate)) {
                        existingBooking = booking;
                        break;
                    }
                }
                
                if (existingBooking != null) {
                    System.out.println("Обновляем существующее бронирование: ID=" + existingBooking.getId());
                    existingBooking.setActive(isActive);
                    bookingDAO.update(existingBooking);
                    updated++;
                } else {
                    System.out.println("Создаем новое бронирование");
                    Booking booking = new Booking();
                    booking.setGuest(guest);
                    booking.setRoom(room);
                    booking.setCheckInDate(checkInDate);
                    booking.setCheckOutDate(checkOutDate);
                    booking.setActive(isActive);
                    
                    bookingDAO.save(booking);
                    
                    // Если бронирование активно, меняем статус комнаты
                    if (isActive) {
                        room.setStatus(RoomStatus.OCCUPIED);
                        roomDAO.update(room);
                    }
                    
                    imported++;
                }
                
            } catch (Exception e) {
                String error = "Строка " + lineNum + ": " + e.getMessage() + " - " + line;
                errors.add(error);
                System.out.println("ОШИБКА: " + error);
                e.printStackTrace();
            }
        }
        
    } catch (IOException e) {
        return "Ошибка чтения файла: " + e.getMessage();
    }
    
    System.out.println("\n=== Импорт бронирований завершен ===");
    return String.format("Импорт бронирований завершен: %d добавлено, %d обновлено. Ошибок: %d", 
                        imported, updated, errors.size());
}
}