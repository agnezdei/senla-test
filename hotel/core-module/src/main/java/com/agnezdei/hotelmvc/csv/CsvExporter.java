package com.agnezdei.hotelmvc.csv;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.agnezdei.hotelmvc.dto.BookingDTO;
import com.agnezdei.hotelmvc.dto.GuestDTO;
import com.agnezdei.hotelmvc.dto.GuestServiceDTO;
import com.agnezdei.hotelmvc.dto.RoomDTO;
import com.agnezdei.hotelmvc.dto.ServiceDTO;

public class CsvExporter {

    public CsvExporter() {
    }

    public void exportRooms(List<RoomDTO> rooms, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Номер,Тип,Цена,Вместимость,Звезды,Статус\n");

            for (RoomDTO room : rooms) {
                writer.write(String.format("%s,%s,%.2f,%d,%d,%s\n",
                        room.getNumber(),
                        room.getType(),
                        room.getPrice(),
                        room.getCapacity(),
                        room.getStars(),
                        room.getStatus()));
            }
        }
    }

    public void exportGuests(List<GuestDTO> guests, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Имя,Номер паспорта\n");

            for (GuestDTO guest : guests) {
                writer.write(String.format("%s,%s\n",
                        guest.getName(),
                        guest.getPassportNumber()));
            }
        }
    }

    public void exportServices(List<ServiceDTO> services, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Название,Цена,Категория\n");

            for (ServiceDTO service : services) {
                writer.write(String.format("%s,%.2f,%s\n",
                        service.getName(),
                        service.getPrice(),
                        service.getCategory()));
            }
        }
    }

    public void exportBookings(List<BookingDTO> bookings, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("ID,Гость,Номер комнаты,Дата заезда,Дата выезда,Активно\n");

            for (BookingDTO booking : bookings) {
                writer.write(String.format("%d,%s,%s,%s,%s,%s\n",
                        booking.getId(),
                        booking.getGuestName(),
                        booking.getRoomNumber(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.isActive()));
            }
        }
    }

    public void exportGuestServices(List<GuestServiceDTO> guestServices, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("ID,Гость,Услуга,Категория,Дата\n");

            for (GuestServiceDTO guestService : guestServices) {
                writer.write(String.format("%d,%s,%s,%s,%s\n",
                        guestService.getId(),
                        guestService.getGuestName(),
                        guestService.getServiceName(),
                        guestService.getServiceCategory(),
                        guestService.getServiceDate()));
            }
        }
    }
}