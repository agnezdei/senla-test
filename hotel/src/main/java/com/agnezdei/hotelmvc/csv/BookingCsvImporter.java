package com.agnezdei.hotelmvc.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.agnezdei.hotelmvc.model.*;

public class BookingCsvImporter {
    private Hotel hotel;

    public BookingCsvImporter(Hotel hotel) {
        this.hotel = hotel;
    }

    public String importBookings(String filePath) {
        List<String> errors = new ArrayList<>();
        int imported = 0;
        int updated = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                try {
                    String[] data = line.split(",");
                    if (data.length < 6) {
                        errors.add("Недостаточно данных в строке: " + line);
                        continue;
                    }

                    Long id = Long.parseLong(data[0]);
                    Long guestId = Long.parseLong(data[1]);
                    Long roomId = Long.parseLong(data[2]);
                    LocalDate checkInDate = LocalDate.parse(data[3]);
                    LocalDate checkOutDate = LocalDate.parse(data[4]);
                    boolean isActive = Boolean.parseBoolean(data[5]);

                    Guest guest = hotel.findGuestById(guestId);
                    Room room = hotel.findRoomById(roomId);
                    
                    if (guest == null) {
                        errors.add("Гость с ID " + guestId + " не найден в строке: " + line);
                        continue;
                    }
                    
                    if (room == null) {
                        errors.add("Комната с ID " + roomId + " не найдена в строке: " + line);
                        continue;
                    }

                    Booking existingBooking = hotel.findBookingById(id);
                    
                    if (existingBooking != null) {
                        existingBooking.setCheckOutDate(checkOutDate);
                        existingBooking.setActive(isActive);
                        updated++;
                    } else {
                        Booking booking = new Booking(id, guest, room, checkInDate, checkOutDate);
                        booking.setActive(isActive);
                        hotel.addBooking(booking);
                        
                        if (isActive) {
                            room.setCurrentBooking(booking);
                            room.setStatus(RoomStatus.OCCUPIED);
                        } else {
                            room.addToHistory(booking);
                        }
                        imported++;
                    }
                    
                } catch (Exception e) {
                    errors.add("Ошибка в строке: " + line + " - " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            return "Ошибка чтения файла: " + e.getMessage();
        }

        return String.format("Импорт бронирований завершен: %d добавлено, %d обновлено. Ошибок: %d", 
                            imported, updated, errors.size());
    }
}