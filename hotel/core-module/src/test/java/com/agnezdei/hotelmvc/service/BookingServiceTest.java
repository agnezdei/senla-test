package com.agnezdei.hotelmvc.service;

import com.agnezdei.hotelmvc.csv.BookingCsvImporter;
import com.agnezdei.hotelmvc.csv.CsvExporter;
import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.model.*;
import com.agnezdei.hotelmvc.repository.BookingDAO;
import com.agnezdei.hotelmvc.repository.GuestServiceDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private BookingDAO bookingDAO;
    @Mock private GuestServiceDAO guestServiceDAO;
    @Mock private CsvExporter csvExporter;
    @Mock private BookingCsvImporter bookingImporter;
    @Mock private RoomService roomService;
    @Mock private GuestService guestService;

    @InjectMocks private BookingService bookingService;

    private Room room;
    private Guest guest;
    private Booking booking;

    @BeforeEach
    void setUp() {
        room = new Room("101", RoomType.STANDARD, 100.0, 2, 3);
        room.setId(1L);
        room.setStatus(RoomStatus.AVAILABLE);
        guest = new Guest("John Doe", "AB123456");
        guest.setId(1L);
        booking = new Booking(guest, room, LocalDate.now(), LocalDate.now().plusDays(3));
        booking.setId(1L);
        booking.setActive(true);
    }

    @Test
    void exportToCsv_success() throws IOException {
        List<Booking> bookings = Collections.singletonList(booking);
        when(bookingDAO.findAll()).thenReturn(bookings);
        doNothing().when(csvExporter).exportBookings(anyList(), anyString());

        String result = bookingService.exportToCsv("/path/file.csv");

        assertThat(result).startsWith("Успех");
        verify(bookingDAO).findAll();
        verify(csvExporter).exportBookings(anyList(), eq("/path/file.csv"));
    }

    @Test
    void exportToCsv_ioException() throws IOException {
        when(bookingDAO.findAll()).thenReturn(Collections.emptyList());
        doThrow(new IOException("IO error")).when(csvExporter).exportBookings(anyList(), anyString());

        String result = bookingService.exportToCsv("/path/file.csv");

        assertThat(result).contains("Ошибка файла");
    }

    @Test
    void importFromCsv_success() {
        when(bookingImporter.importBookings(anyString())).thenReturn("Imported successfully");

        String result = bookingService.importFromCsv("/path/file.csv");

        assertThat(result).isEqualTo("Imported successfully");
        verify(bookingImporter).importBookings("/path/file.csv");
    }

    @Test
    void importFromCsv_exception() {
        when(bookingImporter.importBookings(anyString())).thenThrow(new RuntimeException("Import error"));

        String result = bookingService.importFromCsv("/path/file.csv");

        assertThat(result).contains("Ошибка импорта");
    }

    @Test
    void settleGuest_success() throws EntityNotFoundException, BusinessLogicException {
        when(roomService.findByNumber("101")).thenReturn(Optional.of(room));
        when(bookingDAO.findByRoomId(room.getId())).thenReturn(Collections.emptyList());
        when(guestService.findByPassportNumber(guest.getPassportNumber())).thenReturn(Optional.empty());
        when(guestService.save(any(Guest.class))).thenReturn(guest);
        when(bookingDAO.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(roomService).update(any(Room.class));

        String result = bookingService.settleGuest("101", guest, LocalDate.now(), LocalDate.now().plusDays(3));

        assertThat(result).contains("Успех");
        verify(roomService).update(argThat(r -> r.getStatus() == RoomStatus.OCCUPIED));
        verify(bookingDAO).save(any(Booking.class));
    }

    @Test
    void settleGuest_roomNotFound() {
        when(roomService.findByNumber("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.settleGuest("999", guest, LocalDate.now(), LocalDate.now().plusDays(3)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void settleGuest_roomNotAvailable() {
        room.setStatus(RoomStatus.OCCUPIED);
        when(roomService.findByNumber("101")).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> bookingService.settleGuest("101", guest, LocalDate.now(), LocalDate.now().plusDays(3)))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("недоступен");
    }

    @Test
    void settleGuest_datesOverlap() {
        Booking existingBooking = new Booking(guest, room, LocalDate.now(), LocalDate.now().plusDays(3));
        existingBooking.setActive(true);
        when(roomService.findByNumber("101")).thenReturn(Optional.of(room));
        when(bookingDAO.findByRoomId(room.getId())).thenReturn(Collections.singletonList(existingBooking));

        assertThatThrownBy(() -> bookingService.settleGuest("101", guest, LocalDate.now().plusDays(1), LocalDate.now().plusDays(4)))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("уже забронирован");
    }

    @Test
    void evictGuest_success() throws Exception {
        room.setStatus(RoomStatus.OCCUPIED);
        booking.setActive(true);

        when(roomService.findByNumber("101")).thenReturn(Optional.of(room));
        when(bookingDAO.findByRoomId(room.getId())).thenReturn(Collections.singletonList(booking));
        doAnswer(invocation -> null).when(bookingDAO).update(any(Booking.class));
        doAnswer(invocation -> null).when(roomService).update(any(Room.class));

        String result = bookingService.evictGuest("101");
        assertThat(result).contains("Успех");
        verify(bookingDAO).update(booking);
        verify(roomService).update(argThat(r -> r.getStatus() == RoomStatus.AVAILABLE));
    }

    @Test
    void evictGuest_roomNotFound() {
        when(roomService.findByNumber("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.evictGuest("999"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void evictGuest_roomNotOccupied() {
        room.setStatus(RoomStatus.AVAILABLE);
        when(roomService.findByNumber("101")).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> bookingService.evictGuest("101"))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("не занят");
    }

    @Test
    void evictGuest_noActiveBooking() {
        room.setStatus(RoomStatus.OCCUPIED);

        when(roomService.findByNumber("101")).thenReturn(Optional.of(room));
        when(bookingDAO.findByRoomId(room.getId())).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> bookingService.evictGuest("101"))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("нет активного бронирования");
    }


    @Test
    void getPaymentDetails_success() throws EntityNotFoundException, BusinessLogicException {
        when(roomService.findByNumber("101")).thenReturn(Optional.of(room));
        when(bookingDAO.findByRoomId(room.getId())).thenReturn(Collections.singletonList(booking));
        when(guestServiceDAO.findByGuestId(guest.getId())).thenReturn(Collections.emptyList());

        Map<String, Object> details = bookingService.getPaymentDetails("101");

        assertThat(details).containsEntry("roomNumber", "101");
        assertThat(details).containsEntry("guestName", guest.getName());
        assertThat(details).containsKey("roomCost");
    }

    @Test
    void getPaymentDetails_roomNotFound() {
        when(roomService.findByNumber("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getPaymentDetails("999"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getPaymentDetails_noActiveBooking() {
        when(roomService.findByNumber("101")).thenReturn(Optional.of(room));
        when(bookingDAO.findByRoomId(room.getId())).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> bookingService.getPaymentDetails("101"))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("нет активного бронирования");
    }
}