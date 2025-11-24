package com.agnezdei.hotelmvc.csv;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.agnezdei.hotelmvc.model.*;

public class CsvExporter {

    public void exportRooms(List<Room> rooms, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("id,number,type,price,capacity,stars,status\n");
            
            for (Room room : rooms) {
                String roomType = convertRoomTypeToRussian(room.getType());
                String roomStatus = convertRoomStatusToRussian(room.getStatus());
                
                writer.write(String.format(Locale.US, "%d,%s,%s,%.2f,%d,%d,%s\n",
                    room.getId(),
                    escapeCsv(room.getNumber()),
                    escapeCsv(roomType),
                    room.getPrice(),
                    room.getCapacity(),
                    room.getStars(),
                    escapeCsv(roomStatus)
                ));
            }
        }
    }

    public void exportServices(List<Service> services, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("id,name,price,category\n");
            
            for (Service service : services) {
                String serviceCategory = convertServiceCategoryToRussian(service.getCategory());
                
                writer.write(String.format(Locale.US, "%d,%s,%.2f,%s\n",
                    service.getId(),
                    escapeCsv(service.getName()),
                    service.getPrice(),
                    escapeCsv(serviceCategory)
                ));
            }
        }
    }

    public void exportGuests(List<Guest> guests, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("id,name,passportNumber\n");
            
            for (Guest guest : guests) {
                writer.write(String.format("%d,%s,%s\n",
                    guest.getId(),
                    escapeCsv(guest.getName()),
                    escapeCsv(guest.getPassportNumber())
                ));
            }
        }
    }

    public void exportBookings(List<Booking> bookings, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("id,guestId,roomId,checkInDate,checkOutDate,isActive\n");
            
            for (Booking booking : bookings) {
                writer.write(String.format("%d,%d,%d,%s,%s,%b\n",
                    booking.getId(),
                    booking.getGuest().getId(),
                    booking.getRoom().getId(),
                    booking.getCheckInDate().toString(),
                    booking.getCheckOutDate().toString(),
                    booking.isActive()
                ));
            }
        }
    }

    private String convertRoomTypeToRussian(RoomType type) {
        switch (type) {
            case STANDARD: return "Стандарт";
            case BUSINESS: return "Бизнес";
            case LUXURY: return "Люкс";
            default: return type.toString();
        }
    }
    
    private String convertRoomStatusToRussian(RoomStatus status) {
        switch (status) {
            case AVAILABLE: return "Доступен";
            case OCCUPIED: return "Занят";
            case UNDER_MAINTENANCE: return "На ремонте";
            default: return status.toString();
        }
    }
    
    private String convertServiceCategoryToRussian(ServiceCategory category) {
        switch (category) {
            case FOOD: return "Питание";
            case CLEANING: return "Обслуживание";
            case COMFORT: return "Комфорт";
            default: return category.toString();
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}