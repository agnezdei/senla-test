package com.agnezdei.hotelmvc.service;

import com.agnezdei.hotelmvc.csv.BookingCsvImporter;
import com.agnezdei.hotelmvc.csv.CsvExporter;
import com.agnezdei.hotelmvc.dto.BookingDTO;
import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.mapper.BookingMapper;
import com.agnezdei.hotelmvc.model.Booking;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.model.Room;
import com.agnezdei.hotelmvc.model.RoomStatus;
import com.agnezdei.hotelmvc.repository.BookingDAO;
import com.agnezdei.hotelmvc.repository.GuestServiceDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Service
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingDAO bookingDAO;
    @Autowired
    private GuestServiceDAO guestServiceDAO;
    @Autowired
    private CsvExporter csvExporter;
    @Autowired
    private BookingCsvImporter bookingImporter;
    @Autowired
    private RoomService roomService;
    @Autowired
    private GuestService guestService;

    @Transactional(readOnly = true)
    public String exportToCsv(String filePath) {
        logger.info("Начало экспорта бронирований в файл: {}", filePath);
        try {
            List<Booking> bookings = bookingDAO.findAll();
            List<BookingDTO> bookingDTOs = BookingMapper.toDTOList(bookings);
            csvExporter.exportBookings(bookingDTOs, filePath);
            String result = "Успех: Бронирования экспортированы в " + filePath;
            logger.info(result);
            return result;
        } catch (IOException e) {
            logger.error("Ошибка файла при экспорте бронирований: {}", e.getMessage(), e);
            return "Ошибка файла при экспорте бронирований: " + e.getMessage();
        } catch (Exception e) {
            logger.error("Ошибка БД при экспорте бронирований: {}", e.getMessage(), e);
            return "Ошибка БД при экспорте бронирований: " + e.getMessage();
        }
    }

    public String importFromCsv(String filePath) {
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

    @Transactional(rollbackFor = {EntityNotFoundException.class, BusinessLogicException.class})
    public String settleGuest(String roomNumber, Guest guest, LocalDate checkInDate, LocalDate checkOutDate)
            throws EntityNotFoundException, BusinessLogicException {
        logger.info("Начало заселения: номер={}, гость={}, даты={}-{}",
                roomNumber, guest.getName(), checkInDate, checkOutDate);

        Optional<Room> roomOpt = roomService.findByNumber(roomNumber);
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

        Optional<Guest> existingGuest = guestService.findByPassportNumber(guest.getPassportNumber());
        Guest savedGuest;
        if (existingGuest.isPresent()) {
            savedGuest = existingGuest.get();
            if (!savedGuest.getName().equals(guest.getName())) {
                savedGuest.setName(guest.getName());
                guestService.update(savedGuest);
            }
        } else {
            savedGuest = guestService.save(guest);
        }

        Booking booking = new Booking(savedGuest, room, checkInDate, checkOutDate);
        booking.setActive(true);
        Booking savedBooking = bookingDAO.save(booking);

        room.setStatus(RoomStatus.OCCUPIED);
        roomService.update(room);

        String result = "Успех: " + savedGuest.getName() + " заселен в номер " + roomNumber +
                " (бронирование ID: " + savedBooking.getId() + ")";
        logger.info(result);
        return result;
    }

    @Transactional(rollbackFor = {EntityNotFoundException.class, BusinessLogicException.class})
    public String evictGuest(String roomNumber)
            throws EntityNotFoundException, BusinessLogicException {
        logger.info("Начало выселения из номера: {}", roomNumber);

        Optional<Room> roomOpt = roomService.findByNumber(roomNumber);
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
        roomService.update(room);

        String result = "Успех: " + activeBooking.getGuest().getName() +
                " выселен из номера " + roomNumber;
        logger.info(result);
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPaymentDetails(String roomNumber) throws EntityNotFoundException, BusinessLogicException {
        Room room = roomService.findByNumber(roomNumber)
                .orElseThrow(() -> new EntityNotFoundException("Комната " + roomNumber + " не найдена"));
        List<Booking> bookings = bookingDAO.findByRoomId(room.getId());
        Booking activeBooking = bookings.stream()
                .filter(Booking::isActive)
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException("В комнате " + roomNumber + " нет активного бронирования"));

        long days = activeBooking.getCheckOutDate().toEpochDay() - activeBooking.getCheckInDate().toEpochDay();
        if (days <= 0) days = 1;

        double roomCost = room.getPrice() * days;

        List<com.agnezdei.hotelmvc.model.GuestService> allGuestServices =
                guestServiceDAO.findByGuestId(activeBooking.getGuest().getId());
        List<Map<String, Object>> serviceItems = new ArrayList<>();
        double servicesCost = 0.0;
        for (com.agnezdei.hotelmvc.model.GuestService gs : allGuestServices) {
            if (!gs.getServiceDate().isBefore(activeBooking.getCheckInDate()) &&
                    !gs.getServiceDate().isAfter(activeBooking.getCheckOutDate())) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", gs.getService().getName());
                item.put("date", gs.getServiceDate().toString());
                item.put("price", gs.getService().getPrice());
                serviceItems.add(item);
                servicesCost += gs.getService().getPrice();
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("roomNumber", roomNumber);
        result.put("guestName", activeBooking.getGuest().getName());
        result.put("guestPassport", activeBooking.getGuest().getPassportNumber());
        result.put("checkInDate", activeBooking.getCheckInDate().toString());
        result.put("checkOutDate", activeBooking.getCheckOutDate().toString());
        result.put("days", days);
        result.put("roomPricePerDay", room.getPrice());
        result.put("roomCost", roomCost);
        result.put("services", serviceItems);
        result.put("totalServicesCost", servicesCost);
        result.put("totalCost", roomCost + servicesCost);

        return result;
    }

    private boolean datesOverlap(LocalDate start1, LocalDate end1,
                                 LocalDate start2, LocalDate end2) {
        return !end1.isBefore(start2) && !start1.isAfter(end2);
    }
}