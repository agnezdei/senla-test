package com.agnezdei.hotelmvc.controller;

import java.io.IOException;
import java.time.LocalDate;

import com.agnezdei.hotelmvc.model.*;
import com.agnezdei.hotelmvc.exceptions.*;
import com.agnezdei.hotelmvc.csv.*;
import com.agnezdei.hotelmvc.config.*;

public class HotelAdmin {
    private Hotel hotel;
    private AppConfig config;
    private CsvExporter csvExporter;
    private RoomCsvImporter roomImporter;
    private GuestCsvImporter guestImporter;
    private ServiceCsvImporter serviceImporter;
    private BookingCsvImporter bookingImporter;
    
    public HotelAdmin(Hotel hotel, AppConfig config) {
        this.hotel = hotel;
        this.config = config;
        this.csvExporter = new CsvExporter();
        this.roomImporter = new RoomCsvImporter(hotel);
        this.guestImporter = new GuestCsvImporter(hotel);
        this.serviceImporter = new ServiceCsvImporter(hotel);
        this.bookingImporter = new BookingCsvImporter(hotel, config);
    }
    
    public String exportRoomsToCsv(String filePath) {
        try {
            csvExporter.exportRooms(hotel.getRooms(), filePath);
            return "Успех: Номера экспортированы в " + filePath;
        } catch (IOException e) {
            return "Ошибка: Не удалось экспортировать номера - " + e.getMessage();
        }
    }
    
    public String exportServicesToCsv(String filePath) {
        try {
            csvExporter.exportServices(hotel.getServices(), filePath);
            return "Успех: Услуги экспортированы в " + filePath;
        } catch (IOException e) {
            return "Ошибка: Не удалось экспортировать услуги - " + e.getMessage();
        }
    }

    public String exportGuestsToCsv(String filePath) {
        try {
            csvExporter.exportGuests(hotel.getGuests(), filePath);
            return "Успех: Гости экспортированы в " + filePath;
        } catch (IOException e) {
            return "Ошибка: Не удалось экспортировать гостей - " + e.getMessage();
        }
    }

    public String exportBookingsToCsv(String filePath) {
        try {
            csvExporter.exportBookings(hotel.getBookings(), filePath);
            return "Успех: Бронирования экспортированы в " + filePath;
        } catch (IOException e) {
            return "Ошибка: Не удалось экспортировать бронирования - " + e.getMessage();
        }
    }

    public String importRoomsFromCsv(String filePath) {
        try {
            return roomImporter.importRooms(filePath);
        } catch (Exception e) {
            return "Ошибка импорта: " + e.getMessage();
        }
    }

    public String importServicesFromCsv(String filePath) {
        try {
            return serviceImporter.importServices(filePath);
        } catch (Exception e) {
            return "Ошибка импорта: " + e.getMessage();
        }
    }

    public String importGuestsFromCsv(String filePath) {
        try {
            return guestImporter.importGuests(filePath);
        } catch (Exception e) {
            return "Ошибка импорта: " + e.getMessage();
        }
    }

    public String importBookingsFromCsv(String filePath) {
        try {
            return bookingImporter.importBookings(filePath);
        } catch (Exception e) {
            return "Ошибка импорта: " + e.getMessage();
        }
    }
    
    public String settleGuest(String roomNumber, Guest guest, LocalDate checkInDate, LocalDate checkOutDate) 
            throws EntityNotFoundException, BusinessLogicException, InvalidDateException {
        
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            throw new EntityNotFoundException("Номер " + roomNumber + " не найден");
        }
        
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new BusinessLogicException("Номер " + roomNumber + " недоступен для заселения");
        }
        
        if (checkInDate.isAfter(checkOutDate)) {
            throw new InvalidDateException("Дата заезда не может быть после даты выезда");
        }

        Booking booking = new Booking(hotel.getNextBookingId(), guest, room, checkInDate, checkOutDate);
        Guest guestWithId = new Guest(hotel.getNextGuestId(), guest.getName(), guest.getPassportNumber());
    
        hotel.getBookings().add(booking);
        hotel.addGuest(guestWithId);
        room.setCurrentBooking(booking);
        room.setStatus(RoomStatus.OCCUPIED);
        
        return "Успех: " + guestWithId.getName() + " заселен в номер " + roomNumber;
    }
    
    public String evictGuest(String roomNumber) throws EntityNotFoundException, BusinessLogicException {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            throw new EntityNotFoundException("Номер " + roomNumber + " не найден");
        }
        
        if (room.getStatus() != RoomStatus.OCCUPIED) {
            throw new BusinessLogicException("Номер " + roomNumber + " не занят");
        }

        Booking currentBooking = room.getCurrentBooking();
        if (currentBooking != null) {
            room.addToHistory(currentBooking, config.getMaxBookingHistoryEntries());
            String guestName = currentBooking.getGuest().getName();
            room.setCurrentBooking(null);
            room.setStatus(RoomStatus.AVAILABLE);
            return "Успех: " + guestName + " выселен из номера " + roomNumber;
        }
        throw new BusinessLogicException("В номере " + roomNumber + " нет активного бронирования");
    }
    
    public String setRoomUnderMaintenance(String roomNumber) throws EntityNotFoundException, BusinessLogicException {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            throw new EntityNotFoundException("Номер " + roomNumber + " не найден");
        }

        if (!config.isAllowRoomStatusChange()) {
            throw new BusinessLogicException("Изменение статуса номеров запрещено в настройках");
        }
        
        if (room.getStatus() == RoomStatus.OCCUPIED) {
            throw new BusinessLogicException("Нельзя перевести занятый номер на ремонт");
        }
        
        room.setStatus(RoomStatus.UNDER_MAINTENANCE);
        return "Успех: Номер " + roomNumber + " переведен на ремонт";
    }
    
    public String setRoomAvailable(String roomNumber) throws EntityNotFoundException, BusinessLogicException {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            throw new EntityNotFoundException("Номер " + roomNumber + " не найден");
        }

        if (!config.isAllowRoomStatusChange()) {
            throw new BusinessLogicException("Изменение статуса номеров запрещено в настройках");
        }
        
        room.setStatus(RoomStatus.AVAILABLE);
        return "Успех: Номер " + roomNumber + " доступен для бронирования";
    }
    
    public String changeRoomPrice(String roomNumber, double newPrice) throws EntityNotFoundException {
        Room room = hotel.findRoom(roomNumber);
        if (room == null) {
            throw new EntityNotFoundException("Номер " + roomNumber + " не найден");
        }
        
        room.setPrice(newPrice);
        return "Успех: Цена номера " + roomNumber + " изменена на " + newPrice + " руб.";
    }
    
    public String changeServicePrice(String serviceName, double newPrice) throws EntityNotFoundException {
        Service service = hotel.findService(serviceName);
        if (service == null) {
            throw new EntityNotFoundException("Услуга '" + serviceName + "' не найдена");
        }
        
        service.setPrice(newPrice);
        return "Успех: Цена услуги '" + serviceName + "' изменена на " + newPrice + " руб.";
    }
    
    public String addRoom(String number, RoomType type, double price, int capacity, int stars) 
            throws BusinessLogicException {
        
        if (hotel.findRoom(number) != null) {
            throw new BusinessLogicException("Номер " + number + " уже существует");
        }
        
        Room room = new Room(hotel.getNextRoomId(), number, type, price, capacity, stars, hotel);
        hotel.addRoom(room);
        return "Успех: Добавлен номер " + number;
    }
    
    public String addService(String name, double price, ServiceCategory category) throws BusinessLogicException {
        if (hotel.findService(name) != null) {
            throw new BusinessLogicException("Услуга '" + name + "' уже существует");
        }
        
        Service service = new Service(hotel.getNextServiceId(), name, price, category, hotel);
        hotel.addService(service);
        return "Успех: Добавлена услуга '" + name + "'";
    }

    public String addServiceToBooking(String roomNumber, String serviceName, LocalDate serviceDate) 
            throws EntityNotFoundException, BusinessLogicException, InvalidDateException {
        
        Room room = hotel.findRoom(roomNumber);
        if (room == null || room.getCurrentBooking() == null) {
            throw new BusinessLogicException("Номер не найден или не занят");
        }

        Service service = hotel.findService(serviceName);
        if (service == null) {
            throw new EntityNotFoundException("Услуга '" + serviceName + "' не найдена");
        }

        LocalDate checkIn = room.getCurrentBooking().getCheckInDate();
        LocalDate checkOut = room.getCurrentBooking().getCheckOutDate();
        if (serviceDate.isBefore(checkIn) || serviceDate.isAfter(checkOut)) {
            throw new InvalidDateException("Дата услуги должна быть в периоде проживания");
        }

        room.getCurrentBooking().addService(service, serviceDate);
        return "Успех: Услуга '" + serviceName + "' добавлена к бронированию";
    }
}