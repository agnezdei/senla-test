package com.agnezdei.hotelmvc.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.agnezdei.hotelmvc.model.*;

public class RoomCsvImporter {
    private Hotel hotel;

    public RoomCsvImporter(Hotel hotel) {
        this.hotel = hotel;
    }

    public String importRooms(String filePath) {
        List<String> errors = new ArrayList<>();
        int imported = 0;
        int updated = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                try {
                    String[] data = line.split(",");
                    if (data.length < 7) {
                        errors.add("Недостаточно данных в строке: " + line);
                        continue;
                    }

                    Long id = Long.parseLong(data[0]);
                    String number = data[1];
                    RoomType type = parseRoomType(data[2]);
                    double price = parsePrice(data[3]);
                    int capacity = Integer.parseInt(data[4]);
                    int stars = Integer.parseInt(data[5]);
                    RoomStatus status = parseRoomStatus(data[6]);

                    Room existingRoom = hotel.findRoomById(id);
                    
                    if (existingRoom != null) {
                        existingRoom.setPrice(price);
                        existingRoom.setStatus(status);
                        updated++;
                    } else {
                        Room room = new Room(id, number, type, price, capacity, stars, hotel);
                        room.setStatus(status);
                        hotel.addRoom(room);
                        imported++;
                    }
                    
                } catch (Exception e) {
                    errors.add("Ошибка в строке: " + line + " - " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            return "Ошибка чтения файла: " + e.getMessage();
        }

        StringBuilder result = new StringBuilder();
        result.append("Импорт комнат завершен: ")
              .append(imported).append(" добавлено, ")
              .append(updated).append(" обновлено");
        
        if (!errors.isEmpty()) {
            result.append("\nОшибки:\n");
            for (String error : errors) {
                result.append("- ").append(error).append("\n");
            }
        }
        
        return result.toString();
    }

    private RoomType parseRoomType(String typeStr) {
        switch (typeStr.toLowerCase()) {
            case "стандарт": return RoomType.STANDARD;
            case "бизнес": return RoomType.BUSINESS;
            case "люкс": return RoomType.LUXURY;
            default: 
                try {
                    return RoomType.valueOf(typeStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Неизвестный тип комнаты: " + typeStr);
                }
        }
    }

    private RoomStatus parseRoomStatus(String statusStr) {
        switch (statusStr.toLowerCase()) {
            case "доступен": return RoomStatus.AVAILABLE;
            case "занят": return RoomStatus.OCCUPIED;
            case "на ремонте": return RoomStatus.UNDER_MAINTENANCE;
            default: 
                try {
                    return RoomStatus.valueOf(statusStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Неизвестный статус комнаты: " + statusStr);
                }
        }
    }

    private double parsePrice(String priceStr) {
        String normalized = priceStr.replace(',', '.').trim();
        return Double.parseDouble(normalized);
    }
}