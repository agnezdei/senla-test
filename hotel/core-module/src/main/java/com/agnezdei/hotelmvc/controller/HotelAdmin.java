package com.agnezdei.hotelmvc.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.agnezdei.hotelmvc.annotations.Inject;
import com.agnezdei.hotelmvc.config.AppConfig;
import com.agnezdei.hotelmvc.csv.BookingCsvImporter;
import com.agnezdei.hotelmvc.csv.CsvExporter;
import com.agnezdei.hotelmvc.csv.GuestCsvImporter;
import com.agnezdei.hotelmvc.csv.GuestServiceCsvImporter;
import com.agnezdei.hotelmvc.csv.RoomCsvImporter;
import com.agnezdei.hotelmvc.csv.ServiceCsvImporter;
import com.agnezdei.hotelmvc.dto.BookingDTO;
import com.agnezdei.hotelmvc.dto.GuestDTO;
import com.agnezdei.hotelmvc.dto.GuestServiceDTO;
import com.agnezdei.hotelmvc.dto.RoomDTO;
import com.agnezdei.hotelmvc.dto.ServiceDTO;
import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.DAOException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.mapper.BookingMapper;
import com.agnezdei.hotelmvc.mapper.GuestMapper;
import com.agnezdei.hotelmvc.mapper.GuestServiceMapper;
import com.agnezdei.hotelmvc.mapper.RoomMapper;
import com.agnezdei.hotelmvc.mapper.ServiceMapper;
import com.agnezdei.hotelmvc.model.Booking;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.model.GuestService;
import com.agnezdei.hotelmvc.model.Room;
import com.agnezdei.hotelmvc.model.RoomStatus;
import com.agnezdei.hotelmvc.model.RoomType;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import com.agnezdei.hotelmvc.repository.BookingDAO;
import com.agnezdei.hotelmvc.repository.GuestDAO;
import com.agnezdei.hotelmvc.repository.GuestServiceDAO;
import com.agnezdei.hotelmvc.repository.RoomDAO;
import com.agnezdei.hotelmvc.repository.ServiceDAO;
import com.agnezdei.hotelmvc.util.HibernateUtil;

public class HotelAdmin {
    @Inject
    private RoomDAO roomDAO;

    @Inject
    private GuestDAO guestDAO;

    @Inject
    private ServiceDAO serviceDAO;

    @Inject
    private BookingDAO bookingDAO;

    @Inject
    private GuestServiceDAO guestServiceDAO;

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

    private static final Logger logger = LoggerFactory.getLogger(HotelAdmin.class);

    public String exportRoomsToCsv(String filePath) {
        logger.info("Начало экспорта номеров в файл: {}", filePath);
        try {
            List<Room> rooms = roomDAO.findAll();
            List<RoomDTO> roomDTOs = RoomMapper.toDTOList(rooms);
            csvExporter.exportRooms(roomDTOs, filePath);
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
            List<ServiceDTO> serviceDTOs = ServiceMapper.toDTOList(services);
            csvExporter.exportServices(serviceDTOs, filePath);
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
            List<GuestDTO> guestDTOs = GuestMapper.toDTOList(guests);
            csvExporter.exportGuests(guestDTOs, filePath);
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
            List<BookingDTO> bookingDTOs = BookingMapper.toDTOList(bookings);
            csvExporter.exportBookings(bookingDTOs, filePath);
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
            List<GuestServiceDTO> guestServiceDTOs = GuestServiceMapper.toDTOList(guestServices);
            csvExporter.exportGuestServices(guestServiceDTOs, filePath);
            String result = "Успех: Услуги гостей экспортированы в " + filePath;
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

        Session session = null;
        Transaction transaction = null;
        
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();

            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber, session);
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

            List<Booking> existingBookings = bookingDAO.findByRoomId(room.getId(), session);
            for (Booking existing : existingBookings) {
                if (existing.isActive() && datesOverlap(existing.getCheckInDate(),
                        existing.getCheckOutDate(), checkInDate, checkOutDate)) {
                    throw new BusinessLogicException("Номер уже забронирован на эти даты");
                }
            }

            Optional<Guest> existingGuest = guestDAO.findByPassportNumber(guest.getPassportNumber(), session);
            Guest savedGuest;

            if (existingGuest.isPresent()) {
                savedGuest = existingGuest.get();
                if (!savedGuest.getName().equals(guest.getName())) {
                    savedGuest.setName(guest.getName());
                    guestDAO.update(savedGuest, session);
                }
            } else {
                savedGuest = guestDAO.save(guest, session);
            }

            Booking booking = new Booking(savedGuest, room, checkInDate, checkOutDate);
            booking.setActive(true);
            Booking savedBooking = bookingDAO.save(booking, session);

            room.setStatus(RoomStatus.OCCUPIED);
            roomDAO.update(room, session);

            transaction.commit();

            String result = "Успех: " + savedGuest.getName() + " заселен в номер " + roomNumber +
                    " (бронирование ID: " + savedBooking.getId() + ")";
            logger.info(result);
            return result;

        } catch (EntityNotFoundException | BusinessLogicException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при заселении: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка базы данных при заселении: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public String evictGuest(String roomNumber)
            throws EntityNotFoundException, BusinessLogicException {

        logger.info("Начало выселения из номера: {}", roomNumber);

        Session session = null;
        Transaction transaction = null;
        
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();

            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber, session);
            if (roomOpt.isEmpty()) {
                throw new EntityNotFoundException("Номер " + roomNumber + " не найден");
            }

            Room room = roomOpt.get();
            if (room.getStatus() != RoomStatus.OCCUPIED) {
                throw new BusinessLogicException("Номер " + roomNumber + " не занят");
            }

            List<Booking> bookings = bookingDAO.findByRoomId(room.getId(), session);
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
            bookingDAO.update(activeBooking, session);

            room.setStatus(RoomStatus.AVAILABLE);
            roomDAO.update(room, session);

            transaction.commit();

            String result = "Успех: " + activeBooking.getGuest().getName() +
                    " выселен из номера " + roomNumber;
            logger.info(result);
            return result;

        } catch (EntityNotFoundException | BusinessLogicException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при выселении: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка базы данных при выселении: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public String addServiceToGuest(String guestPassport, String serviceName, LocalDate serviceDate)
            throws EntityNotFoundException, BusinessLogicException {

        logger.info("Начало добавления услуги гостю: паспорт={}, услуга={}, дата={}",
                guestPassport, serviceName, serviceDate);

        Session session = null;
        Transaction transaction = null;
        
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();

            Optional<Guest> guestOpt = guestDAO.findByPassportNumber(guestPassport, session);
            if (guestOpt.isEmpty()) {
                throw new EntityNotFoundException("Гость с паспортом " + guestPassport + " не найден");
            }

            Optional<Service> serviceOpt = serviceDAO.findByName(serviceName, session);
            if (serviceOpt.isEmpty()) {
                throw new EntityNotFoundException("Услуга '" + serviceName + "' не найдена");
            }

            Guest guest = guestOpt.get();
            Service service = serviceOpt.get();

            GuestService guestService = new GuestService(guest, service, serviceDate);
            guestServiceDAO.save(guestService, session);

            transaction.commit();

            String result = "Успех: Услуга '" + serviceName + "' добавлена гостю " + guest.getName();
            logger.info(result);
            return result;
            
        } catch (EntityNotFoundException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при добавлении услуги гостю: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка базы данных при добавлении услуги гостю: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public String removeServiceFromGuest(Long guestServiceId)
            throws EntityNotFoundException, BusinessLogicException {

        logger.info("Начало удаления услуги гостя: ID={}", guestServiceId);

        Session session = null;
        Transaction transaction = null;
        
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();

            Optional<GuestService> guestServiceOpt = guestServiceDAO.findById(guestServiceId, session);
            if (guestServiceOpt.isEmpty()) {
                throw new EntityNotFoundException("Заказ услуги не найден: ID=" + guestServiceId);
            }

            guestServiceDAO.delete(guestServiceId, session);

            transaction.commit();

            String result = "Успех: Услуга удалена из заказов гостя";
            logger.info(result);
            return result;
            
        } catch (EntityNotFoundException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при удалении услуги гостя: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка базы данных при удалении услуги гостя: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public String setRoomUnderMaintenance(String roomNumber)
            throws EntityNotFoundException, BusinessLogicException {

        logger.info("Начало перевода номера на ремонт: номер={}", roomNumber);

        Session session = null;
        Transaction transaction = null;
        
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();

            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber, session);
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
            roomDAO.update(room, session);

            transaction.commit();

            String result = "Успех: Номер " + roomNumber + " переведен на ремонт";
            logger.info(result);
            return result;

        } catch (EntityNotFoundException | BusinessLogicException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при переводе номера на ремонт: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка базы данных при переводе номера на ремонт: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public String setRoomAvailable(String roomNumber)
            throws EntityNotFoundException, BusinessLogicException {

        logger.info("Начало перевода номера в доступные: номер={}", roomNumber);

        Session session = null;
        Transaction transaction = null;
        
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();

            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber, session);
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
            roomDAO.update(room, session);

            transaction.commit();

            String result = "Успех: Номер " + roomNumber + " доступен для бронирования";
            logger.info(result);
            return result;

        } catch (EntityNotFoundException | BusinessLogicException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при переводе номера в доступные: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка базы данных при переводе номера в доступные: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public String changeRoomPrice(String roomNumber, double newPrice)
            throws EntityNotFoundException, BusinessLogicException {

        logger.info("Начало изменения цены номера: номер={}, новая цена={}", roomNumber, newPrice);

        Session session = null;
        Transaction transaction = null;
        
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();

            Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber, session);
            if (roomOpt.isEmpty()) {
                throw new EntityNotFoundException("Номер " + roomNumber + " не найден");
            }

            Room room = roomOpt.get();
            room.setPrice(newPrice);
            roomDAO.update(room, session);

            transaction.commit();

            String result = "Успех: Цена номера " + roomNumber + " изменена на " + newPrice + " руб.";
            logger.info(result);
            return result;

        } catch (EntityNotFoundException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при изменении цены номера: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка базы данных при изменении цены номера: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public String changeServicePrice(String serviceName, double newPrice)
            throws BusinessLogicException, EntityNotFoundException {

        logger.info("Начало изменения цены услуги: услуга={}, новая цена={}", serviceName, newPrice);

        Session session = null;
        Transaction transaction = null;
        
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();

            Optional<Service> serviceOpt = serviceDAO.findByName(serviceName, session);
            if (serviceOpt.isEmpty()) {
                throw new EntityNotFoundException("Услуга '" + serviceName + "' не найдена");
            }

            Service service = serviceOpt.get();
            service.setPrice(newPrice);
            serviceDAO.update(service, session);

            transaction.commit();

            String result = "Успех: Цена услуги '" + serviceName + "' изменена на " + newPrice + " руб.";
            logger.info(result);
            return result;

        } catch (EntityNotFoundException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при изменении цены услуги: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка базы данных при изменении цены услуги: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public String addRoom(String number, RoomType type, double price,
            int capacity, int stars) throws BusinessLogicException {

        logger.info("Начало добавления номера: номер={}, тип={}, цена={}, вместимость={}, звезды={}",
                number, type, price, capacity, stars);

        Session session = null;
        Transaction transaction = null;
        
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();

            Optional<Room> existingRoom = roomDAO.findByNumber(number, session);
            if (existingRoom.isPresent()) {
                throw new BusinessLogicException("Номер " + number + " уже существует");
            }

            Room room = new Room(number, type, price, capacity, stars);
            room.setStatus(RoomStatus.AVAILABLE);
            roomDAO.save(room, session);

            transaction.commit();

            String result = "Успех: Добавлен номер " + number;
            logger.info(result);
            return result;

        } catch (BusinessLogicException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при добавлении номера: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка базы данных при добавлении номера: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public String addService(String name, double price, ServiceCategory category)
            throws BusinessLogicException {

        logger.info("Начало добавления услуги: название={}, цена={}, категория={}",
                name, price, category);

        Session session = null;
        Transaction transaction = null;
        
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();

            Optional<Service> existingService = serviceDAO.findByName(name, session);
            if (existingService.isPresent()) {
                throw new BusinessLogicException("Услуга '" + name + "' уже существует");
            }

            Service service = new Service(name, price, category);
            serviceDAO.save(service, session);

            transaction.commit();

            String result = "Успех: Добавлена услуга '" + name + "'";
            logger.info(result);
            return result;

        } catch (BusinessLogicException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка при добавлении услуги: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Ошибка базы данных при добавлении услуги: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public List<GuestServiceDTO> getGuestServices(Long guestId) throws BusinessLogicException {
        logger.info("Начало получения услуг гостя: ID={}", guestId);

        try {
            List<GuestService> services = guestServiceDAO.findByGuestId(guestId);
            logger.info("Получено услуг для гостя ID={}: {}", guestId, services.size());
            return GuestServiceMapper.toDTOList(services);
        } catch (DAOException e) {
            logger.error("Ошибка базы данных при получении услуг гостя: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }

    public List<GuestServiceDTO> getGuestServicesByName(String guestName) throws BusinessLogicException {
        logger.info("Начало получения услуг гостя по имени: {}", guestName);

        try {
            List<GuestService> services = guestServiceDAO.findByGuestNameOrderedByDate(guestName);
            logger.info("Получено услуг для гостя {}: {}", guestName, services.size());
            return GuestServiceMapper.toDTOList(services);
        } catch (DAOException e) {
            logger.error("Ошибка базы данных при получении услуг гостя по имени: {}", e.getMessage(), e);
            throw new BusinessLogicException("Ошибка базы данных: " + e.getMessage());
        }
    }

    private boolean datesOverlap(LocalDate start1, LocalDate end1,
            LocalDate start2, LocalDate end2) {
        return !end1.isBefore(start2) && !start1.isAfter(end2);
    }
}