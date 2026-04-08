package com.agnezdei.hotelmvc.service;

import com.agnezdei.hotelmvc.csv.CsvExporter;
import com.agnezdei.hotelmvc.csv.GuestCsvImporter;
import com.agnezdei.hotelmvc.model.Booking;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.repository.BookingDAO;
import com.agnezdei.hotelmvc.repository.GuestDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuestServiceTest {

    @Mock private GuestDAO guestDAO;
    @Mock private BookingDAO bookingDAO;
    @Mock private CsvExporter csvExporter;
    @Mock private GuestCsvImporter guestImporter;

    @InjectMocks private GuestService guestService;

    private Guest guest;
    private Booking booking;

    @BeforeEach
    void setUp() {
        guest = new Guest("John Doe", "AB123456");
        guest.setId(1L);
        booking = new Booking();
        booking.setGuest(guest);
        booking.setActive(true);
    }

    @Test
    void exportToCsv_success() throws IOException {
        when(guestDAO.findAll()).thenReturn(Collections.singletonList(guest));
        doNothing().when(csvExporter).exportGuests(anyList(), anyString());

        String result = guestService.exportToCsv("/path/file.csv");

        assertThat(result).startsWith("Успех");
        verify(csvExporter).exportGuests(anyList(), eq("/path/file.csv"));
    }

    @Test
    void exportToCsv_exception() throws Exception {
        when(guestDAO.findAll()).thenThrow(new RuntimeException("DB error"));
        String result = guestService.exportToCsv("/path/file.csv");
        assertThat(result).contains("Ошибка файла");
    }

    @Test
    void importFromCsv_success() {
        when(guestImporter.importGuests(anyString())).thenReturn("Imported");

        String result = guestService.importFromCsv("/path/file.csv");

        assertThat(result).isEqualTo("Imported");
    }

    @Test
    void importFromCsv_exception() {
        when(guestImporter.importGuests(anyString())).thenThrow(new RuntimeException("Import error"));

        String result = guestService.importFromCsv("/path/file.csv");

        assertThat(result).contains("Ошибка импорта");
    }

    @Test
    void getActiveGuests_success() {
        when(bookingDAO.findActiveBookings()).thenReturn(Collections.singletonList(booking));

        List<Guest> activeGuests = guestService.getActiveGuests();

        assertThat(activeGuests).containsExactly(guest);
    }

    @Test
    void getActiveGuests_empty() {
        when(bookingDAO.findActiveBookings()).thenReturn(Collections.emptyList());

        List<Guest> activeGuests = guestService.getActiveGuests();

        assertThat(activeGuests).isEmpty();
    }

    @Test
    void getTotalActiveGuests_success() {
        when(guestDAO.countGuestsWithActiveBookings()).thenReturn(5);

        int count = guestService.getTotalActiveGuests();

        assertThat(count).isEqualTo(5);
    }

    @Test
    void getTotalActiveGuests_dbError() {
        when(guestDAO.countGuestsWithActiveBookings()).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> guestService.getTotalActiveGuests())
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void findByPassportNumber_found() {
        when(guestDAO.findByPassportNumber("AB123456")).thenReturn(Optional.of(guest));

        Optional<Guest> found = guestService.findByPassportNumber("AB123456");

        assertThat(found).isPresent().contains(guest);
    }

    @Test
    void findByPassportNumber_notFound() {
        when(guestDAO.findByPassportNumber("XXX")).thenReturn(Optional.empty());

        Optional<Guest> found = guestService.findByPassportNumber("XXX");

        assertThat(found).isEmpty();
    }

    @Test
    void save_success() {
        when(guestDAO.save(guest)).thenReturn(guest);

        Guest saved = guestService.save(guest);

        assertThat(saved).isSameAs(guest);
    }

    @Test
    void update_success() {
        doAnswer(invocation -> null).when(guestDAO).update(guest);

        guestService.update(guest);

        verify(guestDAO).update(guest);
    }
}