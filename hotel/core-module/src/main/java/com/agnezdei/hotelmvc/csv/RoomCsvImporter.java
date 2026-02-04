package com.agnezdei.hotelmvc.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.agnezdei.hotelmvc.annotations.Inject;
import com.agnezdei.hotelmvc.model.Room;
import com.agnezdei.hotelmvc.model.RoomStatus;
import com.agnezdei.hotelmvc.model.RoomType;
import com.agnezdei.hotelmvc.repository.RoomDAO;

public class RoomCsvImporter {
    @Inject
    private RoomDAO roomDAO;

    public RoomCsvImporter() {
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
                    if (data.length < 6) {
                        errors.add("Недостаточно данных в строке: " + line);
                        continue;
                    }

                    String number = data[0];
                    RoomType type = parseRoomType(data[1]);
                    double price = Double.parseDouble(data[2]);
                    int capacity = Integer.parseInt(data[3]);
                    int stars = Integer.parseInt(data[4]);
                    RoomStatus status = parseRoomStatus(data[5]);

                    Optional<Room> existingRoomOpt = roomDAO.findByNumber(number);

                    if (existingRoomOpt.isPresent()) {
                        Room room = existingRoomOpt.get();
                        room.setPrice(price);
                        room.setType(type);
                        room.setCapacity(capacity);
                        room.setStars(stars);
                        room.setStatus(status);

                        roomDAO.update(room);
                        updated++;
                    } else {
                        Room room = new Room();
                        room.setNumber(number);
                        room.setType(type);
                        room.setPrice(price);
                        room.setCapacity(capacity);
                        room.setStars(stars);
                        room.setStatus(status);

                        roomDAO.save(room);
                        imported++;
                    }

                } catch (Exception e) {
                    errors.add("Ошибка в строке: " + line + " - " + e.getMessage());
                }
            }

        } catch (IOException e) {
            return "Ошибка чтения файла: " + e.getMessage();
        }

        return String.format("Импорт комнат завершен: %d добавлено, %d обновлено. Ошибок: %d",
                imported, updated, errors.size());
    }

    private RoomType parseRoomType(String typeStr) {
        if (typeStr == null)
            return RoomType.STANDARD;

        switch (typeStr.toLowerCase().trim()) {
            case "стандарт":
            case "standard":
                return RoomType.STANDARD;
            case "бизнес":
            case "business":
                return RoomType.BUSINESS;
            case "люкс":
            case "luxury":
                return RoomType.LUXURY;
            default:
                try {
                    return RoomType.valueOf(typeStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.err.println("Неизвестный тип комнаты: " + typeStr + ", используем STANDARD");
                    return RoomType.STANDARD;
                }
        }
    }

    private RoomStatus parseRoomStatus(String statusStr) {
        if (statusStr == null)
            return RoomStatus.AVAILABLE;

        switch (statusStr.toLowerCase().trim()) {
            case "доступен":
            case "available":
                return RoomStatus.AVAILABLE;
            case "занят":
            case "occupied":
                return RoomStatus.OCCUPIED;
            case "на ремонте":
            case "under_maintenance":
                return RoomStatus.UNDER_MAINTENANCE;
            default:
                try {
                    return RoomStatus.valueOf(statusStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.err.println("Неизвестный статус комнаты: " + statusStr + ", используем AVAILABLE");
                    return RoomStatus.AVAILABLE;
                }
        }
    }
}