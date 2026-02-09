package com.agnezdei.hotelmvc.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.agnezdei.hotelmvc.annotations.Inject;
import com.agnezdei.hotelmvc.model.Booking;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.model.Room;
import com.agnezdei.hotelmvc.model.RoomStatus;
import com.agnezdei.hotelmvc.repository.BookingDAO;
import com.agnezdei.hotelmvc.repository.GuestDAO;
import com.agnezdei.hotelmvc.repository.RoomDAO;
import com.agnezdei.hotelmvc.util.HibernateUtil;

public class BookingCsvImporter {
    @Inject
    private BookingDAO bookingDAO;
    @Inject
    private GuestDAO guestDAO;
    @Inject
    private RoomDAO roomDAO;

    public BookingCsvImporter() {
    }

    public String importBookings(String filePath) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();
            
            String result = importBookings(filePath, session);
            
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            return "Ошибка при импорте бронирований: " + e.getMessage();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public String importBookings(String filePath, Session session) {
        List<String> errors = new ArrayList<>();
        int imported = 0;
        int updated = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();

            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                try {
                    String[] data = line.split(",");
                    if (data.length < 5) {
                        errors.add("Строка " + lineNum + ": Недостаточно данных (" + data.length + " из 5)");
                        continue;
                    }

                    String guestPassport = data[0].trim();
                    String roomNumber = data[1].trim();
                    LocalDate checkInDate = LocalDate.parse(data[2].trim());
                    LocalDate checkOutDate = LocalDate.parse(data[3].trim());
                    boolean isActive = Boolean.parseBoolean(data[4].trim());

                    Optional<Guest> guestOpt = guestDAO.findByPassportNumber(guestPassport, session);
                    if (guestOpt.isEmpty()) {
                        errors.add("Строка " + lineNum + ": Гость с паспортом " + guestPassport + " не найден");
                        continue;
                    }

                    Optional<Room> roomOpt = roomDAO.findByNumber(roomNumber, session);
                    if (roomOpt.isEmpty()) {
                        errors.add("Строка " + lineNum + ": Комната с номером " + roomNumber + " не найдена");
                        continue;
                    }

                    Guest guest = guestOpt.get();
                    Room room = roomOpt.get();

                    List<Booking> existingBookings = bookingDAO.findByRoomId(room.getId(), session);
                    Booking existingBooking = null;

                    for (Booking booking : existingBookings) {
                        if (booking.getGuest().getId().equals(guest.getId()) &&
                                booking.getCheckInDate().equals(checkInDate) &&
                                booking.getCheckOutDate().equals(checkOutDate)) {
                            existingBooking = booking;
                            break;
                        }
                    }

                    if (existingBooking != null) {
                        existingBooking.setActive(isActive);
                        bookingDAO.update(existingBooking, session);
                        updated++;
                    } else {
                        Booking booking = new Booking();
                        booking.setGuest(guest);
                        booking.setRoom(room);
                        booking.setCheckInDate(checkInDate);
                        booking.setCheckOutDate(checkOutDate);
                        booking.setActive(isActive);

                        bookingDAO.save(booking, session);

                        if (isActive) {
                            room.setStatus(RoomStatus.OCCUPIED);
                            roomDAO.update(room, session);
                        }

                        imported++;
                    }

                } catch (Exception e) {
                    errors.add("Строка " + lineNum + ": " + e.getMessage() + " - " + line);
                }
            }

        } catch (IOException e) {
            return "Ошибка чтения файла: " + e.getMessage();
        }

        return String.format("Импорт бронирований завершен: %d добавлено, %d обновлено. Ошибок: %d",
                imported, updated, errors.size());
    }
}