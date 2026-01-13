package com.agnezdei.hotelmvc.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.agnezdei.hotelmvc.annotations.Inject;
import com.agnezdei.hotelmvc.config.AppConfig;
import com.agnezdei.hotelmvc.config.DatabaseConfig;
import com.agnezdei.hotelmvc.csv.BookingCsvImporter;
import com.agnezdei.hotelmvc.csv.BookingServiceCsvImporter;
import com.agnezdei.hotelmvc.csv.CsvExporter;
import com.agnezdei.hotelmvc.csv.GuestCsvImporter;
import com.agnezdei.hotelmvc.csv.RoomCsvImporter;
import com.agnezdei.hotelmvc.csv.ServiceCsvImporter;
import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.DAOException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.model.Booking;
import com.agnezdei.hotelmvc.model.BookingService;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.model.Room;
import com.agnezdei.hotelmvc.model.RoomStatus;
import com.agnezdei.hotelmvc.model.RoomType;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import com.agnezdei.hotelmvc.repository.impl.BookingRepository;
import com.agnezdei.hotelmvc.repository.impl.BookingServiceRepository;
import com.agnezdei.hotelmvc.repository.impl.GuestRepository;
import com.agnezdei.hotelmvc.repository.impl.RoomRepository;
import com.agnezdei.hotelmvc.repository.impl.ServiceRepository;
import com.agnezdei.hotelmvc.util.TransactionManager;

public class HotelAdmin {
    @Inject
    private RoomRepository roomDAO;
    
    @Inject
    private GuestRepository guestDAO;
    
    @Inject
    private ServiceRepository serviceDAO;
    
    @Inject
    private BookingRepository bookingDAO;
     
    @Inject
    private BookingServiceRepository bookingServiceDAO;

    @Inject
    private AppConfig config;
    
    @Inject
    private CsvExporter csvExporter;
    
    @Inject
    private RoomCsvImporter roomImporter;
    
    @Inject
    private GuestCsvImporter guestImporter;
    
    @Inject
    private ServiceCsvImporter serviceImporter;
    
    @Inject
    private BookingCsvImporter bookingImporter;

    @Inject
    private BookingServiceCsvImporter bookingServiceImporter;

    @Inject
    private DatabaseConfig databaseConfig;
    
    public HotelAdmin() {
    }
    
    public String exportRoomsToCsv(String filePath) {
        try {
            List<Room> rooms = roomDAO.findAll();
            csvExporter.exportRooms(rooms, filePath);
            return "Успех: Номера экспортированы в " + filePath;
        } catch (DAOException e) {
            return "Ошибка БД при экспорте номеров: " + e.getMessage();
        } catch (IOException e) {
            return "Ошибка файла при экспорте номеров: " + e.getMessage();
        }
    }
    
    public String exportServicesToCsv(String filePath) {
        try {
            List<Service> services = serviceDAO.findAll();
            csvExporter.exportServices(services, filePath);
            return "Успех: Услуги экспортированы в " + filePath;
        } catch (DAOException e) {
            return "Ошибка БД при экспорте услуг: " + e.getMessage();
        } catch (IOException e) {
            return "Ошибка файла при экспорте услуг: " + e.getMessage();
        }
    }

    public String exportGuestsToCsv(String filePath) {
        try {
            List<Guest> guests = guestDAO.findAll();
            csvExporter.exportGuests(guests, filePath);
            return "Успех: Гости экспортированы в " + filePath;
        } catch (DAOException e) {
            return "Ошибка БД при экспорте гостей: " + e.getMessage();
        } catch (IOException e) {
            return "Ошибка файла при экспорте гостей: " + e.getMessage();
        }
    }

    public String exportBookingsToCsv(String filePath) {
        try {
            List<Booking> bookings = bookingDAO.findAll();
            csvExporter.exportBookings(bookings, filePath);
            return "Успех: Бронирования экспортированы в " + filePath;
        } catch (DAOException e) {
            return "Ошибка БД при экспорте бронирований: " + e.getMessage();
        } catch (IOException e) {
            return "Ошибка файла при экспорте бронирований: " + e.getMessage();
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
    
    public String importBookingServicesFromCsv(String filePath) {
        try {
            return bookingServiceImporter.importBookingServices(filePath);
        } catch (Exception e) {
            return "Ошибка импорта: " + e.getMessage();
        }
    }
    
    public String settleGuest(String roomNumber, Guest guest, LocalDate checkInDate, LocalDate checkOutDate) 
        throws EntityNotFoundException, BusinessLogicException {
    
    try {
        TransactionManager.beginTransaction(databaseConfig);
        
        Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
        if (roomOpt.isEmpty()) {
            TransactionManager.rollback();
            throw new EntityNotFoundException("Номер " + roomNumber + " не найден");
        }
        
        Room room = roomOpt.get();
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            TransactionManager.rollback();
            throw new BusinessLogicException("Номер " + roomNumber + " недоступен для заселения");
        }
        
        if (checkInDate.isAfter(checkOutDate)) {
            TransactionManager.rollback();
            throw new BusinessLogicException("Дата заезда не может быть после даты выезда");
        }
        
        List<Booking> existingBookings = bookingDAO.findByRoomId(room.getId());
        for (Booking existing : existingBookings) {
            if (existing.isActive() && datesOverlap(existing.getCheckInDate(), 
                    existing.getCheckOutDate(), checkInDate, checkOutDate)) {
                TransactionManager.rollback();
                throw new BusinessLogicException("Номер уже забронирован на эти даты");
            }
        }
        
        Optional<Guest> existingGuest = guestDAO.findByPassportNumber(guest.getPassportNumber());
        Guest savedGuest;
        
        if (existingGuest.isPresent()) {
            savedGuest = existingGuest.get();
            if (!savedGuest.getName().equals(guest.getName())) {
                savedGuest.setName(guest.getName());
                guestDAO.update(savedGuest);
            }
        } else {
            savedGuest = guestDAO.save(guest);
        }
        
        Booking booking = new Booking();
        booking.setGuest(savedGuest);
        booking.setRoom(room);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setActive(true);
        
        Booking savedBooking = bookingDAO.save(booking);
        
        room.setStatus(RoomStatus.OCCUPIED);
        roomDAO.update(room);
        
        TransactionManager.commit();
        
        return "Успех: " + savedGuest.getName() + " заселен в номер " + roomNumber + 
               " (бронирование ID: " + savedBooking.getId() + ")";
        
    } catch (SQLException e) {
        TransactionManager.rollback();
        throw new BusinessLogicException("Ошибка транзакции: " + e.getMessage());
    } catch (DAOException e) {
        TransactionManager.rollback();
        throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
    }
}
    
    private boolean datesOverlap(LocalDate start1, LocalDate end1, 
                                LocalDate start2, LocalDate end2) {
        return !end1.isBefore(start2) && !start1.isAfter(end2);
    }
    
    public String evictGuest(String roomNumber) 
            throws EntityNotFoundException, BusinessLogicException {
        
        try {
            TransactionManager.beginTransaction(databaseConfig);
            
            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
            if (roomOpt.isEmpty()) {
                TransactionManager.rollback();
                throw new EntityNotFoundException("Номер " + roomNumber + " не найден");
            }
            
            Room room = roomOpt.get();
            if (room.getStatus() != RoomStatus.OCCUPIED) {
                TransactionManager.rollback();
                throw new BusinessLogicException("Номер " + roomNumber + " не занят");
            }

            List<Booking> bookings = bookingDAO.findByRoomId(room.getId());
            Booking activeBooking = null;
            
            for (Booking booking : bookings) {
                if (booking.isActive()) {
                    activeBooking = booking;
                    break;
                }
            }
            
            if (activeBooking == null) {
                TransactionManager.rollback();
                throw new BusinessLogicException("В номере " + roomNumber + " нет активного бронирования");
            }
            
            activeBooking.setActive(false);
            bookingDAO.update(activeBooking);
            
            room.setStatus(RoomStatus.AVAILABLE);
            roomDAO.update(room);
            
            TransactionManager.commit();
            
            return "Успех: " + activeBooking.getGuest().getName() + 
                   " выселен из номера " + roomNumber;
        } catch (SQLException e) {
        TransactionManager.rollback();
        throw new BusinessLogicException("Ошибка транзакции: " + e.getMessage());
        } catch (DAOException e) {
        TransactionManager.rollback();
        throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }
    
    public String addServiceToBooking(String roomNumber, String serviceName, 
                                     LocalDate serviceDate) 
            throws EntityNotFoundException, BusinessLogicException {
        try {
            TransactionManager.beginTransaction(databaseConfig);
            
            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
            if (roomOpt.isEmpty()) {
                TransactionManager.rollback();
                throw new EntityNotFoundException("Номер " + roomNumber + " не найден");
            }
            
            Room room = roomOpt.get();
            List<Booking> bookings = bookingDAO.findByRoomId(room.getId());
            Booking activeBooking = null;
            
            for (Booking booking : bookings) {
                if (booking.isActive()) {
                    activeBooking = booking;
                    break;
                }
            }
            
            if (activeBooking == null) {
                TransactionManager.rollback();
                throw new BusinessLogicException("В номере нет активного бронирования");
            }
            
            Optional<Service> serviceOpt = serviceDAO.findByName(serviceName);
            if (serviceOpt.isEmpty()) {
                TransactionManager.rollback();
                throw new EntityNotFoundException("Услуга '" + serviceName + "' не найдена");
            }
            
            Service service = serviceOpt.get();
            LocalDate checkIn = activeBooking.getCheckInDate();
            LocalDate checkOut = activeBooking.getCheckOutDate();
            
            if (serviceDate.isBefore(checkIn) || serviceDate.isAfter(checkOut)) {
                TransactionManager.rollback();
                throw new BusinessLogicException("Дата услуги должна быть в периоде проживания: " + 
                                               checkIn + " - " + checkOut);
            }
            
            BookingService bookingService = new BookingService();
            bookingService.setBooking(activeBooking);
            bookingService.setService(service);
            bookingService.setServiceDate(serviceDate);
            
            bookingServiceDAO.save(bookingService);
            
            TransactionManager.commit();
            
            return "Успех: Услуга '" + serviceName + "' добавлена к бронированию на " + serviceDate;
        } catch (SQLException e) {
        TransactionManager.rollback();
        throw new BusinessLogicException("Ошибка транзакции: " + e.getMessage());
        } catch (DAOException e) {
        TransactionManager.rollback();
        throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
    }
    }
    
    public String removeServiceFromBooking(Long bookingServiceId) 
            throws EntityNotFoundException, BusinessLogicException {
        try {
            TransactionManager.beginTransaction(databaseConfig);
            
            Optional<BookingService> bookingServiceOpt = bookingServiceDAO.findById(bookingServiceId);
            if (bookingServiceOpt.isEmpty()) {
                TransactionManager.rollback();
                throw new EntityNotFoundException("Связь услуги с бронированием не найдена: ID=" + bookingServiceId);
            }
            
            bookingServiceDAO.delete(bookingServiceId);
            
            TransactionManager.commit();
            return "Успех: Услуга удалена из бронирования";
        } catch (SQLException e) {
        TransactionManager.rollback();
        throw new BusinessLogicException("Ошибка транзакции: " + e.getMessage());
        } catch (DAOException e) {
        TransactionManager.rollback();
        throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
    }
    }
    
    public List<BookingService> getBookingServices(Long bookingId) throws BusinessLogicException {
        try {
            return bookingServiceDAO.findByBookingId(bookingId);
        } catch (DAOException e) {
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }
    
    public String setRoomUnderMaintenance(String roomNumber) 
            throws EntityNotFoundException, BusinessLogicException {
        try {
            TransactionManager.beginTransaction(databaseConfig);
            
            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
            if (roomOpt.isEmpty()) {
                TransactionManager.rollback();
                throw new EntityNotFoundException("Номер " + roomNumber + " не найден");
            }
            
            if (!config.isAllowRoomStatusChange()) {
                TransactionManager.rollback();
                throw new BusinessLogicException("Изменение статуса номеров запрещено в настройках");
            }
            
            Room room = roomOpt.get();
            
            if (room.getStatus() == RoomStatus.OCCUPIED) {
                TransactionManager.rollback();
                throw new BusinessLogicException("Нельзя перевести занятый номер на ремонт");
            }
            
            room.setStatus(RoomStatus.UNDER_MAINTENANCE);
            roomDAO.update(room);
            
            TransactionManager.commit();
            
            return "Успех: Номер " + roomNumber + " переведен на ремонт";
        } catch (SQLException e) {
        TransactionManager.rollback();
        throw new BusinessLogicException("Ошибка транзакции: " + e.getMessage());
        } catch (DAOException e) {
        TransactionManager.rollback();
        throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
    }
    }
    
    public String setRoomAvailable(String roomNumber) 
            throws EntityNotFoundException, BusinessLogicException {
        try {
            TransactionManager.beginTransaction(databaseConfig);
            
            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
            if (roomOpt.isEmpty()) {
                TransactionManager.rollback();
                throw new EntityNotFoundException("Номер " + roomNumber + " не найден");
            }
            
            if (!config.isAllowRoomStatusChange()) {
                TransactionManager.rollback();
                throw new BusinessLogicException("Изменение статуса номеров запрещено в настройках");
            }
            
            Room room = roomOpt.get();
            
            if (room.getStatus() == RoomStatus.OCCUPIED) {
                TransactionManager.rollback();
                throw new BusinessLogicException("Номер занят. Сначала выселите гостя");
            }
            
            room.setStatus(RoomStatus.AVAILABLE);
            roomDAO.update(room);
            
            TransactionManager.commit();
            
            return "Успех: Номер " + roomNumber + " доступен для бронирования";
        } catch (SQLException e) {
        TransactionManager.rollback();
        throw new BusinessLogicException("Ошибка транзакции: " + e.getMessage());
        } catch (DAOException e) {
        TransactionManager.rollback();
        throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
    }
    }
    
    public String changeRoomPrice(String roomNumber, double newPrice) 
            throws EntityNotFoundException, BusinessLogicException {
        
        try {
            TransactionManager.beginTransaction(databaseConfig);
            
            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
            if (roomOpt.isEmpty()) {
                TransactionManager.rollback();
                throw new EntityNotFoundException("Номер " + roomNumber + " не найден");
            }
            
            Room room = roomOpt.get();
            room.setPrice(newPrice);
            roomDAO.update(room);
            
            TransactionManager.commit();
            
            return "Успех: Цена номера " + roomNumber + " изменена на " + newPrice + " руб.";
        } catch (SQLException e) {
        TransactionManager.rollback();
        throw new BusinessLogicException("Ошибка транзакции: " + e.getMessage());
        } catch (DAOException e) {
        TransactionManager.rollback();
        throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
    }
    }
    
    public String changeServicePrice(String serviceName, double newPrice) 
            throws BusinessLogicException, EntityNotFoundException {
        try {
            TransactionManager.beginTransaction(databaseConfig);
            
            Optional<Service> serviceOpt = serviceDAO.findByName(serviceName);
            if (serviceOpt.isEmpty()) {
                TransactionManager.rollback();
                throw new EntityNotFoundException("Услуга '" + serviceName + "' не найдена");
            }
            
            Service service = serviceOpt.get();
            service.setPrice(newPrice);
            serviceDAO.update(service);
            
            TransactionManager.commit();
            
            return "Успех: Цена услуги '" + serviceName + "' изменена на " + newPrice + " руб.";
        } catch (SQLException e) {
        TransactionManager.rollback();
        throw new BusinessLogicException("Ошибка транзакции: " + e.getMessage());
        } catch (DAOException e) {
        TransactionManager.rollback();
        throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
    }
    }
    
    public String addRoom(String number, RoomType type, double price, 
                         int capacity, int stars) throws BusinessLogicException {
        try {
            TransactionManager.beginTransaction(databaseConfig);
            
            Optional<Room> existingRoom = roomDAO.findByNumber(number);
            if (existingRoom.isPresent()) {
                TransactionManager.rollback();
                throw new BusinessLogicException("Номер " + number + " уже существует");
            }
            
            Room room = new Room();
            room.setNumber(number);
            room.setType(type);
            room.setPrice(price);
            room.setCapacity(capacity);
            room.setStars(stars);
            room.setStatus(RoomStatus.AVAILABLE);
            
            roomDAO.save(room);
            
            TransactionManager.commit();
            return "Успех: Добавлен номер " + number;
        } catch (SQLException e) {
        TransactionManager.rollback();
        throw new BusinessLogicException("Ошибка транзакции: " + e.getMessage());
        } catch (DAOException e) {
        TransactionManager.rollback();
        throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
    }
    }
    
    public String addService(String name, double price, ServiceCategory category) 
            throws BusinessLogicException {
        try {
            TransactionManager.beginTransaction(databaseConfig);
            
            Optional<Service> existingService = serviceDAO.findByName(name);
            if (existingService.isPresent()) {
                TransactionManager.rollback();
                throw new BusinessLogicException("Услуга '" + name + "' уже существует");
            }
            
            Service service = new Service();
            service.setName(name);
            service.setPrice(price);
            service.setCategory(category);
            
            serviceDAO.save(service);
            
            TransactionManager.commit();
            return "Успех: Добавлена услуга '" + name + "'";
        } catch (SQLException e) {
        TransactionManager.rollback();
        throw new BusinessLogicException("Ошибка транзакции: " + e.getMessage());
        } catch (DAOException e) {
        TransactionManager.rollback();
        throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
    }
    }
}