package com.agnezdei.hotelmvc.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.agnezdei.hotelmvc.annotations.Inject;
import com.agnezdei.hotelmvc.config.AppConfig;
import com.agnezdei.hotelmvc.csv.BookingCsvImporter;
import com.agnezdei.hotelmvc.csv.CsvExporter;
import com.agnezdei.hotelmvc.csv.GuestCsvImporter;
import com.agnezdei.hotelmvc.csv.GuestServiceCsvImporter;
import com.agnezdei.hotelmvc.csv.RoomCsvImporter;
import com.agnezdei.hotelmvc.csv.ServiceCsvImporter;
import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.DAOException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.model.Booking;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.model.GuestService;
import com.agnezdei.hotelmvc.model.Room;
import com.agnezdei.hotelmvc.model.RoomStatus;
import com.agnezdei.hotelmvc.model.RoomType;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import com.agnezdei.hotelmvc.repository.impl.BookingRepository;
import com.agnezdei.hotelmvc.repository.impl.GuestRepository;
import com.agnezdei.hotelmvc.repository.impl.GuestServiceRepository;
import com.agnezdei.hotelmvc.repository.impl.RoomRepository;
import com.agnezdei.hotelmvc.repository.impl.ServiceRepository;

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
    private GuestServiceRepository guestServiceDAO;

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
    private GuestServiceCsvImporter guestServiceImporter;
    
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

    public String exportGuestServicesToCsv(String filePath) {
        try {
            List<GuestService> guestServices = guestServiceDAO.findAll();
            csvExporter.exportGuestServices(guestServices, filePath);
            return "Успех: Гости экспортированы в " + filePath;
        } catch (DAOException e) {
            return "Ошибка БД при экспорте услуг гостей: " + e.getMessage();
        } catch (IOException e) {
            return "Ошибка файла при экспорте услуг гостей: " + e.getMessage();
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
    
    public String importGuestServicesFromCsv(String filePath) {
        try {
            return guestServiceImporter.importGuestServices(filePath);
        } catch (Exception e) {
            return "Ошибка импорта: " + e.getMessage();
        }
    }
    
    public String settleGuest(String roomNumber, Guest guest, LocalDate checkInDate, LocalDate checkOutDate) 
        throws EntityNotFoundException, BusinessLogicException {
    
        try {
            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
            if (roomOpt.isEmpty()) {
                throw new EntityNotFoundException("Номер " + roomNumber + " не найден");
            }
            
            Room room = roomOpt.get();
            if (room.getStatus() != RoomStatus.AVAILABLE) {
                throw new BusinessLogicException("Номер " + roomNumber + " недоступен для заселения");
            }
            
            if (checkInDate.isAfter(checkOutDate)) {
                throw new BusinessLogicException("Дата заезда не может быть после даты выезда");
            }
            
            List<Booking> existingBookings = bookingDAO.findByRoomId(room.getId());
            for (Booking existing : existingBookings) {
                if (existing.isActive() && datesOverlap(existing.getCheckInDate(), 
                        existing.getCheckOutDate(), checkInDate, checkOutDate)) {
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
            
            return "Успех: " + savedGuest.getName() + " заселен в номер " + roomNumber + 
                   " (бронирование ID: " + savedBooking.getId() + ")";
            
        } catch (DAOException e) {
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }

    public String evictGuest(String roomNumber) 
            throws EntityNotFoundException, BusinessLogicException {
        
        try {
            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
            if (roomOpt.isEmpty()) {
                throw new EntityNotFoundException("Номер " + roomNumber + " не найден");
            }
            
            Room room = roomOpt.get();
            if (room.getStatus() != RoomStatus.OCCUPIED) {
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
                throw new BusinessLogicException("В номере " + roomNumber + " нет активного бронирования");
            }
            
            activeBooking.setActive(false);
            bookingDAO.update(activeBooking);
            
            room.setStatus(RoomStatus.AVAILABLE);
            roomDAO.update(room);
            
            return "Успех: " + activeBooking.getGuest().getName() + 
                   " выселен из номера " + roomNumber;
            
        } catch (DAOException e) {
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }
    
    public String addServiceToGuest(String guestPassport, String serviceName, LocalDate serviceDate) 
        throws EntityNotFoundException, BusinessLogicException {
        
        try {
            Optional<Guest> guestOpt = guestDAO.findByPassportNumber(guestPassport);
            if (guestOpt.isEmpty()) {
                throw new EntityNotFoundException("Гость с паспортом " + guestPassport + " не найден");
            }
            
            Optional<Service> serviceOpt = serviceDAO.findByName(serviceName);
            if (serviceOpt.isEmpty()) {
                throw new EntityNotFoundException("Услуга '" + serviceName + "' не найдена");
            }
            
            Guest guest = guestOpt.get();
            Service service = serviceOpt.get();
            
            GuestService guestService = new GuestService();
            guestService.setGuest(guest);
            guestService.setService(service);
            guestService.setServiceDate(serviceDate);
            
            guestServiceDAO.save(guestService);
            
            return "Успех: Услуга '" + serviceName + "' добавлена гостю " + guest.getName();
        } catch (DAOException e) {
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }
    
    public String removeServiceFromGuest(Long guestServiceId) 
        throws EntityNotFoundException, BusinessLogicException {
        
        try {
            Optional<GuestService> guestServiceOpt = guestServiceDAO.findById(guestServiceId);
            if (guestServiceOpt.isEmpty()) {
                throw new EntityNotFoundException("Заказ услуги не найден: ID=" + guestServiceId);
            }
            
            guestServiceDAO.delete(guestServiceId);
            
            return "Успех: Услуга удалена из заказов гостя";
        } catch (DAOException e) {
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }
    
    public String setRoomUnderMaintenance(String roomNumber) 
            throws EntityNotFoundException, BusinessLogicException {
        
        try {
            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
            if (roomOpt.isEmpty()) {
                throw new EntityNotFoundException("Номер " + roomNumber + " не найден");
            }
            
            if (!config.isAllowRoomStatusChange()) {
                throw new BusinessLogicException("Изменение статуса номеров запрещено в настройках");
            }
            
            Room room = roomOpt.get();
            
            if (room.getStatus() == RoomStatus.OCCUPIED) {
                throw new BusinessLogicException("Нельзя перевести занятый номер на ремонт");
            }
            
            room.setStatus(RoomStatus.UNDER_MAINTENANCE);
            roomDAO.update(room);
            
            return "Успех: Номер " + roomNumber + " переведен на ремонт";
            
        } catch (DAOException e) {
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }
    
    public String setRoomAvailable(String roomNumber) 
            throws EntityNotFoundException, BusinessLogicException {
        
        try {
            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
            if (roomOpt.isEmpty()) {
                throw new EntityNotFoundException("Номер " + roomNumber + " не найден");
            }
            
            if (!config.isAllowRoomStatusChange()) {
                throw new BusinessLogicException("Изменение статуса номеров запрещено в настройках");
            }
            
            Room room = roomOpt.get();
            
            if (room.getStatus() == RoomStatus.OCCUPIED) {
                throw new BusinessLogicException("Номер занят. Сначала выселите гостя");
            }
            
            room.setStatus(RoomStatus.AVAILABLE);
            roomDAO.update(room);
            
            return "Успех: Номер " + roomNumber + " доступен для бронирования";
            
        } catch (DAOException e) {
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }

    public String changeRoomPrice(String roomNumber, double newPrice) 
            throws EntityNotFoundException, BusinessLogicException {
        
        try {
            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
            if (roomOpt.isEmpty()) {
                throw new EntityNotFoundException("Номер " + roomNumber + " не найден");
            }

            Room room = roomOpt.get();
            room.setPrice(newPrice);
            roomDAO.update(room);
            
            return "Успех: Цена номера " + roomNumber + " изменена на " + newPrice + " руб.";
            
        } catch (DAOException e) {
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }
    

    public String changeServicePrice(String serviceName, double newPrice) 
            throws BusinessLogicException, EntityNotFoundException {
        
        try {
            Optional<Service> serviceOpt = serviceDAO.findByName(serviceName);
            if (serviceOpt.isEmpty()) {
                throw new EntityNotFoundException("Услуга '" + serviceName + "' не найдена");
            }
            
            Service service = serviceOpt.get();
            service.setPrice(newPrice);
            serviceDAO.update(service);
            
            return "Успех: Цена услуги '" + serviceName + "' изменена на " + newPrice + " руб.";
            
        } catch (DAOException e) {
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }

    public String addRoom(String number, RoomType type, double price, 
                         int capacity, int stars) throws BusinessLogicException {
        
        try {
            Optional<Room> existingRoom = roomDAO.findByNumber(number);
            if (existingRoom.isPresent()) {
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
            
            return "Успех: Добавлен номер " + number;
            
        } catch (DAOException e) {
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }
    
    public String addService(String name, double price, ServiceCategory category) 
            throws BusinessLogicException {
        
        try {
            Optional<Service> existingService = serviceDAO.findByName(name);
            if (existingService.isPresent()) {
                throw new BusinessLogicException("Услуга '" + name + "' уже существует");
            }
            
            Service service = new Service();
            service.setName(name);
            service.setPrice(price);
            service.setCategory(category);
            
            serviceDAO.save(service);
            
            return "Успех: Добавлена услуга '" + name + "'";
            
        } catch (DAOException e) {
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }
    
    public List<GuestService> getGuestServices(Long guestId) throws BusinessLogicException {
        try {
            return guestServiceDAO.findByGuestId(guestId);
        } catch (DAOException e) {
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }

    private boolean datesOverlap(LocalDate start1, LocalDate end1, 
                                LocalDate start2, LocalDate end2) {
        return !end1.isBefore(start2) && !start1.isAfter(end2);
    }
}