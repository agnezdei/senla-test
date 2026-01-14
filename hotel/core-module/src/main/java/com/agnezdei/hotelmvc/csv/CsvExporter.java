package com.agnezdei.hotelmvc.csv;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.agnezdei.hotelmvc.annotations.Inject;
import com.agnezdei.hotelmvc.exceptions.DAOException;
import com.agnezdei.hotelmvc.model.Booking;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.model.GuestService;
import com.agnezdei.hotelmvc.model.Room;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.repository.impl.BookingRepository;
import com.agnezdei.hotelmvc.repository.impl.GuestRepository;
import com.agnezdei.hotelmvc.repository.impl.GuestServiceRepository;
import com.agnezdei.hotelmvc.repository.impl.RoomRepository;
import com.agnezdei.hotelmvc.repository.impl.ServiceRepository;

public class CsvExporter {
    @Inject
    private RoomRepository roomDAO;
    
    @Inject
    private GuestRepository guestDAO;
    
    @Inject
    private ServiceRepository serviceDAO;
    
    @Inject
    private BookingRepository bookingDAO;

    @Inject
    private GuestServiceRepository guestServiceDAO;
    
    public CsvExporter() {
    }
    
    public void exportRooms(List<Room> rooms, String filePath) throws IOException {
        try {
            if (rooms == null) {
                rooms = roomDAO.findAll();
            }
        
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write("Номер,Тип,Цена,Вместимость,Звезды,Статус\n");
                
                for (Room room : rooms) {
                    writer.write(String.format("%s,%s,%.2f,%d,%d,%s\n",
                        room.getNumber(),
                        room.getType().name(),
                        room.getPrice(),
                        room.getCapacity(),
                        room.getStars(),
                        room.getStatus().name()
                    ));
                }
            }
        } catch (DAOException e) {
                throw new IOException("Ошибка при получении комнат из базы данных: " + e.getMessage(), e);
            }
    }
    
    public void exportGuests(List<Guest> guests, String filePath) throws IOException {
        try {
            if (guests == null) {
                guests = guestDAO.findAll();
            }
            
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write("Имя,Номер паспорта\n");
                
                for (Guest guest : guests) {
                    writer.write(String.format("%s,%s\n",
                        guest.getName(),
                        guest.getPassportNumber()
                    ));
                }
            }
        } catch (DAOException e) {
                throw new IOException("Ошибка при получении гостей из базы данных: " + e.getMessage(), e);
            }
    }
    
    public void exportServices(List<Service> services, String filePath) throws IOException {
        try {
            if (services == null) {
                services = serviceDAO.findAll();
            }
            
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write("Название,Цена,Категория\n");
                
                for (Service service : services) {
                    writer.write(String.format("%s,%.2f,%s\n",
                        service.getName(),
                        service.getPrice(),
                        service.getCategory().name()
                    ));
                }
            }
        } catch (DAOException e) {
                throw new IOException("Ошибка при получении услуг из базы данных: " + e.getMessage(), e);
            }
    }
    
    public void exportBookings(List<Booking> bookings, String filePath) throws IOException {
        try {
            if (bookings == null) {
                bookings = bookingDAO.findAll();
            }
            
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write("Паспорт гостя,Номер комнаты,Дата заезда,Дата выезда,Активно\n");
                
                for (Booking booking : bookings) {
                    writer.write(String.format("%s,%s,%s,%s,%s\n",
                        booking.getGuest().getPassportNumber(),
                        booking.getRoom().getNumber(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.isActive()
                    ));
                }
            }
        } catch (DAOException e) {
                throw new IOException("Ошибка при получении бронирований из базы данных: " + e.getMessage(), e);
            }
    }

    public void exportGuestServices(List<GuestService> guestServices, String filePath) throws IOException {
        try {
            if (guestServices == null) {
                guestServices = guestServiceDAO.findAll();
            }
            
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write("Гость,Услуга\n");
                
                for (GuestService guestService : guestServices) {
                    writer.write(String.format("%s,%s\n",
                        guestService.getGuest(),
                        guestService.getService()
                    ));
                }
            }
        } catch (DAOException e) {
                throw new IOException("Ошибка при получении услуг гостей из базы данных: " + e.getMessage(), e);
            }
    }
}