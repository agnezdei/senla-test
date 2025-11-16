package com.agnezdei.hotelmvc.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.agnezdei.hotelmvc.model.*;

public class CsvImporter {
    private Hotel hotel;

    public CsvImporter(Hotel hotel) {
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

    public String importServices(String filePath) {
        List<String> errors = new ArrayList<>();
        int imported = 0;
        int updated = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                try {
                    String[] data = line.split(",");
                    if (data.length < 4) {
                        errors.add("Недостаточно данных в строке: " + line);
                        continue;
                    }

                    Long id = Long.parseLong(data[0]);
                    String name = data[1];
                    double price = Double.parseDouble(data[2]);
                    ServiceCategory category = parseServiceCategory(data[3]);

                    Service existingService = hotel.findServiceById(id);
                    
                    if (existingService != null) {
                        existingService.setPrice(price);
                        updated++;
                    } else {
                        Service service = new Service(id, name, price, category, hotel);
                        hotel.addService(service);
                        imported++;
                    }
                    
                } catch (Exception e) {
                    errors.add("Ошибка в строке: " + line + " - " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            return "Ошибка чтения файла: " + e.getMessage();
        }

        return String.format("Импорт услуг завершен: %d добавлено, %d обновлено. Ошибок: %d", 
                            imported, updated, errors.size());
    }

    public String importGuests(String filePath) {
        List<String> errors = new ArrayList<>();
        int imported = 0;
        int updated = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                try {
                    String[] data = line.split(",");
                    if (data.length < 3) {
                        errors.add("Недостаточно данных в строке: " + line);
                        continue;
                    }

                    Long id = Long.parseLong(data[0]);
                    String name = data[1];
                    String passportNumber = data[2];

                    Guest existingGuest = hotel.findGuestById(id);
                    
                    if (existingGuest != null) {
                        updated++;
                    } else {
                        Guest guest = new Guest(id, name, passportNumber);
                        hotel.addGuest(guest);
                        imported++;
                    }
                    
                } catch (Exception e) {
                    errors.add("Ошибка в строке: " + line + " - " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            return "Ошибка чтения файла: " + e.getMessage();
        }

        return String.format("Импорт гостей завершен: %d добавлено, %d обновлено. Ошибок: %d", 
                            imported, updated, errors.size());
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

    private ServiceCategory parseServiceCategory(String categoryStr) {
        switch (categoryStr.toLowerCase()) {
            case "питание": return ServiceCategory.FOOD;
            case "обслуживание": return ServiceCategory.CLEANING;
            case "комфорт": return ServiceCategory.COMFORT;
            default: 
                try {
                    return ServiceCategory.valueOf(categoryStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Неизвестная категория услуги: " + categoryStr);
                }
        }
    }

    private double parsePrice(String priceStr) {
        String normalized = priceStr.replace(',', '.').trim();
        return Double.parseDouble(normalized);
    }
}