package com.agnezdei.hotelmvc.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger logger = LogManager.getLogger(HotelAdmin.class);

    public String exportRoomsToCsv(String filePath) {
        logger.info("Начало экспорта номеров в файл: {}", filePath);
        try {
            List<Room> rooms = roomDAO.findAll();
            csvExporter.exportRooms(rooms, filePath);
            String result = "Успех: Номера экспортированы в " + filePath;
            logger.info(result);
            return result;
        } catch (DAOException e) {
            logger.error("Ошибка БД при экспорте номеров: {}", e.getMessage(), e);
            return "Ошибка БД при экспорте номеров: " + e.getMessage();
        } catch (IOException e) {
            logger.error("Ошибка файла при экспорте номеров: {}", e.getMessage(), e);
            return "Ошибка файла при экспорте номеров: " + e.getMessage();
        }
    }

    public String exportServicesToCsv(String filePath) {
        logger.info("Начало экспорта услуг в файл: {}", filePath);
        try {
            List<Service> services = serviceDAO.findAll();
            csvExporter.exportServices(services, filePath);
            String result = "Успех: Услуги экспортированы в " + filePath;
            logger.info(result);
            return result;
        } catch (DAOException e) {
            logger.error("Ошибка БД при экспорте услуг: {}", e.getMessage(), e);
            return "Ошибка БД при экспорте услуг: " + e.getMessage();
        } catch (IOException e) {
            logger.error("Ошибка файла при экспорте услуг: {}", e.getMessage(), e);
            return "Ошибка файла при экспорте услуг: " + e.getMessage();
        }
    }

    public String exportGuestsToCsv(String filePath) {
        logger.info("Начало экспорта гостей в файл: {}", filePath);
        try {
            List<Guest> guests = guestDAO.findAll();
            csvExporter.exportGuests(guests, filePath);
            String result = "Успех: Гости экспортированы в " + filePath;
            logger.info(result);
            return result;
        } catch (DAOException e) {
            logger.error("Ошибка БД при экспорте гостей: {}", e.getMessage(), e);
            return "Ошибка БД при экспорте гостей: " + e.getMessage();
        } catch (IOException e) {
            logger.error("Ошибка файла при экспорте гостей: {}", e.getMessage(), e);
            return "Ошибка файла при экспорте гостей: " + e.getMessage();
        }
    }

    public String exportBookingsToCsv(String filePath) {
        logger.info("Начало экспорта бронирований в файл: {}", filePath);
        try {
            List<Booking> bookings = bookingDAO.findAll();
            csvExporter.exportBookings(bookings, filePath);
            String result = "Успех: Бронирования экспортированы в " + filePath;
            logger.info(result);
            return result;
        } catch (DAOException e) {
            logger.error("Ошибка БД при экспорте бронирований: {}", e.getMessage(), e);
            return "Ошибка БД при экспорте бронирований: " + e.getMessage();
        } catch (IOException e) {
            logger.error("Ошибка файла при экспорте бронирований: {}", e.getMessage(), e);
            return "Ошибка файла при экспорте бронирований: " + e.getMessage();
        }
    }

    public String exportGuestServicesToCsv(String filePath) {
        logger.info("Начало экспорта услуг гостей в файл: {}", filePath);
        try {
            List<GuestService> guestServices = guestServiceDAO.findAll();
            csvExporter.exportGuestServices(guestServices, filePath);
            String result = "Успех: Гости экспортированы в " + filePath;
            logger.info(result);
            return result;
        } catch (DAOException e) {
            logger.error("Ошибка БД при экспорте услуг гостей: {}", e.getMessage(), e);
            return "Ошибка БД при экспорте услуг гостей: " + e.getMessage();
        } catch (IOException e) {
            logger.error("Ошибка файла при экспорте услуг гостей: {}", e.getMessage(), e);
            return "Ошибка файла при экспорте услуг гостей: " + e.getMessage();
        }
    }

    public String importRoomsFromCsv(String filePath) {
        logger.info("Начало импорта номеров из файла: {}", filePath);
        try {
            String result = roomImporter.importRooms(filePath);
            logger.info("Импорт номеров завершен: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Ошибка импорта номеров: {}", e.getMessage(), e);
            return "Ошибка импорта: " + e.getMessage();
        }
    }

    public String importServicesFromCsv(String filePath) {
        logger.info("Начало импорта услуг из файла: {}", filePath);
        try {
            String result = serviceImporter.importServices(filePath);
            logger.info("Импорт услуг завершен: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Ошибка импорта услуг: {}", e.getMessage(), e);
            return "Ошибка импорта: " + e.getMessage();
        }
    }

    public String importGuestsFromCsv(String filePath) {
        logger.info("Начало импорта гостей из файла: {}", filePath);
        try {
            String result = guestImporter.importGuests(filePath);
            logger.info("Импорт гостей завершен: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Ошибка импорта гостей: {}", e.getMessage(), e);
            return "Ошибка импорта: " + e.getMessage();
        }
    }

    public String importBookingsFromCsv(String filePath) {
        logger.info("Начало импорта бронирований из файла: {}", filePath);
        try {
            String result = bookingImporter.importBookings(filePath);
            logger.info("Импорт бронирований завершен: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Ошибка импорта бронирований: {}", e.getMessage(), e);
            return "Ошибка импорта: " + e.getMessage();
        }
    }

    public String importGuestServicesFromCsv(String filePath) {
        logger.info("Начало импорта услуг гостей из файла: {}", filePath);
        try {
            String result = guestServiceImporter.importGuestServices(filePath);
            logger.info("Импорт услуг гостей завершен: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Ошибка импорта услуг гостей: {}", e.getMessage(), e);
            return "Ошибка импорта: " + e.getMessage();
        }
    }

    public String settleGuest(String roomNumber, Guest guest, LocalDate checkInDate, LocalDate checkOutDate)
            throws EntityNotFoundException, BusinessLogicException {

        logger.info("Начало заселения: номер={}, гость={}, даты={}-{}",
                roomNumber, guest.getName(), checkInDate, checkOutDate);

        try {
            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
            if (roomOpt.isEmpty()) {
                String errorMsg = "Номер " + roomNumber + " не найден";
                logger.error(errorMsg);
                throw new EntityNotFoundException(errorMsg);
            }

            Room room = roomOpt.get();
            if (room.getStatus() != RoomStatus.AVAILABLE) {
                String errorMsg = "Номер " + roomNumber + " недоступен для заселения";
                logger.error(errorMsg);
                throw new BusinessLogicException(errorMsg);
            }

            if (checkInDate.isAfter(checkOutDate)) {
                String errorMsg = "Дата заезда не может быть после даты выезда";
                logger.error(errorMsg);
                throw new BusinessLogicException(errorMsg);
            }

            List<Booking> existingBookings = bookingDAO.findByRoomId(room.getId());
            for (Booking existing : existingBookings) {
                if (existing.isActive() && datesOverlap(existing.getCheckInDate(),
                        existing.getCheckOutDate(), checkInDate, checkOutDate)) {
                    String errorMsg = "Номер уже забронирован на эти даты";
                    logger.error(errorMsg);
                    throw new BusinessLogicException(errorMsg);
                }
            }

            Optional<Guest> existingGuest = guestDAO.findByPassportNumber(guest.getPassportNumber());
            Guest savedGuest;

            if (existingGuest.isPresent()) {
                savedGuest = existingGuest.get();
                if (!savedGuest.getName().equals(guest.getName())) {
                    savedGuest.setName(guest.getName());
                    guestDAO.update(savedGuest);
                    logger.info("Обновлены данные существующего гостя: {}", guest.getPassportNumber());
                }
            } else {
                savedGuest = guestDAO.save(guest);
                logger.info("Создан новый гость: {}", guest.getPassportNumber());
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

            logger.info("Успешное заселение: номер={}, гость={}, ID={}",
                    roomNumber, guest.getName(), savedBooking.getId());

            return "Успех: " + savedGuest.getName() + " заселен в номер " + roomNumber +
                    " (бронирование ID: " + savedBooking.getId() + ")";

        } catch (DAOException e) {
            logger.error("Ошибка базы данных при заселении: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }

    public String evictGuest(String roomNumber)
            throws EntityNotFoundException, BusinessLogicException {

        logger.info("Начало выселения из номера: {}", roomNumber);

        try {
            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
            if (roomOpt.isEmpty()) {
                String errorMsg = "Номер " + roomNumber + " не найден";
                logger.error(errorMsg);
                throw new EntityNotFoundException(errorMsg);
            }

            Room room = roomOpt.get();
            if (room.getStatus() != RoomStatus.OCCUPIED) {
                String errorMsg = "Номер " + roomNumber + " не занят";
                logger.error(errorMsg);
                throw new BusinessLogicException(errorMsg);
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
                String errorMsg = "В номере " + roomNumber + " нет активного бронирования";
                logger.error(errorMsg);
                throw new BusinessLogicException(errorMsg);
            }

            activeBooking.setActive(false);
            bookingDAO.update(activeBooking);

            room.setStatus(RoomStatus.AVAILABLE);
            roomDAO.update(room);

            String result = "Успех: " + activeBooking.getGuest().getName() +
                    " выселен из номера " + roomNumber;
            logger.info(result);

            return result;

        } catch (DAOException e) {
            logger.error("Ошибка базы данных при выселении: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }

    public String addServiceToGuest(String guestPassport, String serviceName, LocalDate serviceDate)
            throws EntityNotFoundException, BusinessLogicException {

        logger.info("Начало добавления услуги гостю: паспорт={}, услуга={}, дата={}",
                guestPassport, serviceName, serviceDate);

        try {
            Optional<Guest> guestOpt = guestDAO.findByPassportNumber(guestPassport);
            if (guestOpt.isEmpty()) {
                String errorMsg = "Гость с паспортом " + guestPassport + " не найден";
                logger.error(errorMsg);
                throw new EntityNotFoundException(errorMsg);
            }

            Optional<Service> serviceOpt = serviceDAO.findByName(serviceName);
            if (serviceOpt.isEmpty()) {
                String errorMsg = "Услуга '" + serviceName + "' не найдена";
                logger.error(errorMsg);
                throw new EntityNotFoundException(errorMsg);
            }

            Guest guest = guestOpt.get();
            Service service = serviceOpt.get();

            GuestService guestService = new GuestService();
            guestService.setGuest(guest);
            guestService.setService(service);
            guestService.setServiceDate(serviceDate);

            guestServiceDAO.save(guestService);

            String result = "Успех: Услуга '" + serviceName + "' добавлена гостю " + guest.getName();
            logger.info(result);

            return result;
        } catch (DAOException e) {
            logger.error("Ошибка базы данных при добавлении услуги гостю: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }

    public String removeServiceFromGuest(Long guestServiceId)
            throws EntityNotFoundException, BusinessLogicException {

        logger.info("Начало удаления услуги гостя: ID={}", guestServiceId);

        try {
            Optional<GuestService> guestServiceOpt = guestServiceDAO.findById(guestServiceId);
            if (guestServiceOpt.isEmpty()) {
                String errorMsg = "Заказ услуги не найден: ID=" + guestServiceId;
                logger.error(errorMsg);
                throw new EntityNotFoundException(errorMsg);
            }

            guestServiceDAO.delete(guestServiceId);

            String result = "Успех: Услуга удалена из заказов гостя";
            logger.info(result);

            return result;
        } catch (DAOException e) {
            logger.error("Ошибка базы данных при удалении услуги гостя: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }

    public String setRoomUnderMaintenance(String roomNumber)
            throws EntityNotFoundException, BusinessLogicException {

        logger.info("Начало перевода номера на ремонт: номер={}", roomNumber);

        try {
            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
            if (roomOpt.isEmpty()) {
                String errorMsg = "Номер " + roomNumber + " не найден";
                logger.error(errorMsg);
                throw new EntityNotFoundException(errorMsg);
            }

            if (!config.isAllowRoomStatusChange()) {
                String errorMsg = "Изменение статуса номеров запрещено в настройках";
                logger.error(errorMsg);
                throw new BusinessLogicException(errorMsg);
            }

            Room room = roomOpt.get();

            if (room.getStatus() == RoomStatus.OCCUPIED) {
                String errorMsg = "Нельзя перевести занятый номер на ремонт";
                logger.error(errorMsg);
                throw new BusinessLogicException(errorMsg);
            }

            room.setStatus(RoomStatus.UNDER_MAINTENANCE);
            roomDAO.update(room);

            String result = "Успех: Номер " + roomNumber + " переведен на ремонт";
            logger.info(result);

            return result;

        } catch (DAOException e) {
            logger.error("Ошибка базы данных при переводе номера на ремонт: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }

    public String setRoomAvailable(String roomNumber)
            throws EntityNotFoundException, BusinessLogicException {

        logger.info("Начало перевода номера в доступные: номер={}", roomNumber);

        try {
            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
            if (roomOpt.isEmpty()) {
                String errorMsg = "Номер " + roomNumber + " не найден";
                logger.error(errorMsg);
                throw new EntityNotFoundException(errorMsg);
            }

            if (!config.isAllowRoomStatusChange()) {
                String errorMsg = "Изменение статуса номеров запрещено в настройках";
                logger.error(errorMsg);
                throw new BusinessLogicException(errorMsg);
            }

            Room room = roomOpt.get();

            if (room.getStatus() == RoomStatus.OCCUPIED) {
                String errorMsg = "Номер занят. Сначала выселите гостя";
                logger.error(errorMsg);
                throw new BusinessLogicException(errorMsg);
            }

            room.setStatus(RoomStatus.AVAILABLE);
            roomDAO.update(room);

            String result = "Успех: Номер " + roomNumber + " доступен для бронирования";
            logger.info(result);

            return result;

        } catch (DAOException e) {
            logger.error("Ошибка базы данных при переводе номера в доступные: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }

    public String changeRoomPrice(String roomNumber, double newPrice)
            throws EntityNotFoundException, BusinessLogicException {

        logger.info("Начало изменения цены номера: номер={}, новая цена={}", roomNumber, newPrice);

        try {
            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
            if (roomOpt.isEmpty()) {
                String errorMsg = "Номер " + roomNumber + " не найден";
                logger.error(errorMsg);
                throw new EntityNotFoundException(errorMsg);
            }

            Room room = roomOpt.get();
            room.setPrice(newPrice);
            roomDAO.update(room);

            String result = "Успех: Цена номера " + roomNumber + " изменена на " + newPrice + " руб.";
            logger.info(result);

            return result;

        } catch (DAOException e) {
            logger.error("Ошибка базы данных при изменении цены номера: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }

    public String changeServicePrice(String serviceName, double newPrice)
            throws BusinessLogicException, EntityNotFoundException {

        logger.info("Начало изменения цены услуги: услуга={}, новая цена={}", serviceName, newPrice);

        try {
            Optional<Service> serviceOpt = serviceDAO.findByName(serviceName);
            if (serviceOpt.isEmpty()) {
                String errorMsg = "Услуга '" + serviceName + "' не найдена";
                logger.error(errorMsg);
                throw new EntityNotFoundException(errorMsg);
            }

            Service service = serviceOpt.get();
            service.setPrice(newPrice);
            serviceDAO.update(service);

            String result = "Успех: Цена услуги '" + serviceName + "' изменена на " + newPrice + " руб.";
            logger.info(result);

            return result;

        } catch (DAOException e) {
            logger.error("Ошибка базы данных при изменении цены услуги: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }

    public String addRoom(String number, RoomType type, double price,
            int capacity, int stars) throws BusinessLogicException {

        logger.info("Начало добавления номера: номер={}, тип={}, цена={}, вместимость={}, звезды={}",
                number, type, price, capacity, stars);

        try {
            Optional<Room> existingRoom = roomDAO.findByNumber(number);
            if (existingRoom.isPresent()) {
                String errorMsg = "Номер " + number + " уже существует";
                logger.error(errorMsg);
                throw new BusinessLogicException(errorMsg);
            }

            Room room = new Room();
            room.setNumber(number);
            room.setType(type);
            room.setPrice(price);
            room.setCapacity(capacity);
            room.setStars(stars);
            room.setStatus(RoomStatus.AVAILABLE);

            roomDAO.save(room);

            String result = "Успех: Добавлен номер " + number;
            logger.info(result);

            return result;

        } catch (DAOException e) {
            logger.error("Ошибка базы данных при добавлении номера: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }

    public String addService(String name, double price, ServiceCategory category)
            throws BusinessLogicException {

        logger.info("Начало добавления услуги: название={}, цена={}, категория={}",
                name, price, category);

        try {
            Optional<Service> existingService = serviceDAO.findByName(name);
            if (existingService.isPresent()) {
                String errorMsg = "Услуга '" + name + "' уже существует";
                logger.error(errorMsg);
                throw new BusinessLogicException(errorMsg);
            }

            Service service = new Service();
            service.setName(name);
            service.setPrice(price);
            service.setCategory(category);

            serviceDAO.save(service);

            String result = "Успех: Добавлена услуга '" + name + "'";
            logger.info(result);

            return result;

        } catch (DAOException e) {
            logger.error("Ошибка базы данных при добавлении услуги: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }

    public List<GuestService> getGuestServices(Long guestId) throws BusinessLogicException {
        logger.info("Начало получения услуг гостя: ID={}", guestId);

        try {
            List<GuestService> services = guestServiceDAO.findByGuestId(guestId);
            logger.info("Получено услуг для гостя ID={}: {}", guestId, services.size());
            return services;
        } catch (DAOException e) {
            logger.error("Ошибка базы данных при получении услуг гостя: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }

    private boolean datesOverlap(LocalDate start1, LocalDate end1,
            LocalDate start2, LocalDate end2) {
        return !end1.isBefore(start2) && !start1.isAfter(end2);
    }
}