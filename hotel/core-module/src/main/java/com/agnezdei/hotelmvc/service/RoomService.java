package com.agnezdei.hotelmvc.service;

import com.agnezdei.hotelmvc.config.AppConfig;
import com.agnezdei.hotelmvc.csv.CsvExporter;
import com.agnezdei.hotelmvc.csv.RoomCsvImporter;
import com.agnezdei.hotelmvc.dto.RoomDTO;
import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.mapper.RoomMapper;
import com.agnezdei.hotelmvc.model.Room;
import com.agnezdei.hotelmvc.model.RoomStatus;
import com.agnezdei.hotelmvc.model.RoomType;
import com.agnezdei.hotelmvc.model.Booking;
import com.agnezdei.hotelmvc.repository.RoomDAO;
import com.agnezdei.hotelmvc.repository.BookingDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Service
public class RoomService {
    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    @Autowired
    private RoomDAO roomDAO;
    @Autowired
    private BookingDAO bookingDAO;
    @Autowired
    private AppConfig config;
    @Autowired
    private CsvExporter csvExporter;
    @Autowired
    private RoomCsvImporter roomImporter;

    @Transactional(readOnly = true)
    public String exportToCsv(String filePath) {
        logger.info("Начало экспорта номеров в файл: {}", filePath);
        try {
            List<Room> rooms = roomDAO.findAll();
            List<RoomDTO> roomDTOs = RoomMapper.toDTOList(rooms);
            csvExporter.exportRooms(roomDTOs, filePath);
            String result = "Успех: Номера экспортированы в " + filePath;
            logger.info(result);
            return result;
        } catch (IOException e) {
            logger.error("Ошибка файла при экспорте номеров: {}", e.getMessage(), e);
            return "Ошибка файла при экспорте номеров: " + e.getMessage();
        } catch (Exception e) {
            logger.error("Ошибка при экспорте номеров: {}", e.getMessage(), e);
            return "Ошибка при экспорте номеров: " + e.getMessage();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String importFromCsv(String filePath) {
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

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(rollbackFor = {EntityNotFoundException.class, BusinessLogicException.class})
    public String setUnderMaintenance(String roomNumber) throws EntityNotFoundException, BusinessLogicException {
        logger.info("Начало перевода номера на ремонт: номер={}", roomNumber);

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

        String result = "Успех: Номер " + roomNumber + " переведен на ремонт";
        logger.info(result);
        return result;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(rollbackFor = {EntityNotFoundException.class, BusinessLogicException.class})
    public String setAvailable(String roomNumber) throws EntityNotFoundException, BusinessLogicException {
        logger.info("Начало перевода номера в доступные: номер={}", roomNumber);

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

        String result = "Успех: Номер " + roomNumber + " доступен для бронирования";
        logger.info(result);
        return result;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(rollbackFor = {EntityNotFoundException.class, BusinessLogicException.class})
    public String changePrice(String roomNumber, double newPrice) throws EntityNotFoundException, BusinessLogicException {
        logger.info("Начало изменения цены номера: номер={}, новая цена={}", roomNumber, newPrice);

        Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber);
        if (roomOpt.isEmpty()) {
            throw new EntityNotFoundException("Номер " + roomNumber + " не найден");
        }

        Room room = roomOpt.get();
        room.setPrice(newPrice);
        roomDAO.update(room);

        String result = "Успех: Цена номера " + roomNumber + " изменена на " + newPrice + " руб.";
        logger.info(result);
        return result;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(rollbackFor = {BusinessLogicException.class})
    public String addRoom(String number, RoomType type, double price, int capacity, int stars) throws BusinessLogicException {
        logger.info("Начало добавления номера: номер={}, тип={}, цена={}, вместимость={}, звезды={}",
                number, type, price, capacity, stars);

        Optional<Room> existingRoom = roomDAO.findByNumber(number);
        if (existingRoom.isPresent()) {
            throw new BusinessLogicException("Номер " + number + " уже существует");
        }

        Room room = new Room(number, type, price, capacity, stars);
        room.setStatus(RoomStatus.AVAILABLE);
        roomDAO.save(room);

        String result = "Успех: Добавлен номер " + number;
        logger.info(result);
        return result;
    }

    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        return roomDAO.findAll();
    }

    @Transactional(readOnly = true)
    public List<Room> getAllRoomsSortedByPrice() {
        return roomDAO.findAllOrderedByPrice();
    }

    @Transactional(readOnly = true)
    public List<Room> getAllRoomsSortedByCapacity() {
        return roomDAO.findAllOrderedByCapacity();
    }

    @Transactional(readOnly = true)
    public List<Room> getAllRoomsSortedByStars() {
        return roomDAO.findAllOrderedByStars();
    }

    @Transactional(readOnly = true)
    public List<Room> getAvailableRooms() {
        return roomDAO.findAvailableRooms();
    }

    @Transactional(readOnly = true)
    public List<Room> getAvailableRoomsSortedByPrice() {
        return roomDAO.findAvailableRoomsOrderedByPrice();
    }

    @Transactional(readOnly = true)
    public List<Room> getAvailableRoomsSortedByCapacity() {
        return roomDAO.findAvailableRoomsOrderedByCapacity();
    }

    @Transactional(readOnly = true)
    public List<Room> getAvailableRoomsSortedByStars() {
        return roomDAO.findAvailableRoomsOrderedByStars();
    }

    @Transactional(readOnly = true)
    public int getTotalAvailableRooms() {
        return roomDAO.countAvailableRooms();
    }

    @Transactional(readOnly = true)
    public List<Room> getRoomsAvailableByDate(LocalDate date) {
        return roomDAO.findRoomsAvailableOnDate(date);
    }

    public List<Booking> getLastThreeGuestsOfRoom(String roomNumber) {
        Room room = roomDAO.findByNumber(roomNumber)
                .orElseThrow(() -> new EntityNotFoundException("Комната не найдена"));
        return bookingDAO.findLastThreeGuestsByRoomId(room.getId());
    }

    @Transactional(readOnly = true)
    public Optional<Room> findByNumber(String number) {
        return roomDAO.findByNumber(number);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void update(Room room) {
        roomDAO.update(room);
    }
}