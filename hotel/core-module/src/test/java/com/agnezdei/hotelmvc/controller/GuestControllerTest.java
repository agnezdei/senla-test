package com.agnezdei.hotelmvc.controller;

import com.agnezdei.hotelmvc.dto.GuestDTO;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.service.GuestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GuestControllerTest {

    @Mock
    private GuestService guestService;

    @InjectMocks
    private GuestController guestController;

    private Guest testGuest;

    @BeforeEach
    void setUp() {
        testGuest = new Guest("John Doe", "AB123456");
        testGuest.setId(1L);
    }

    @Test
    void testEndpoint() {
        ResponseEntity<String> response = guestController.test();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("GuestController is working");
    }

    @Test
    void getActiveGuests_success() {
        when(guestService.getActiveGuests()).thenReturn(List.of(testGuest));

        ResponseEntity<List<GuestDTO>> response = guestController.getActiveGuests(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getName()).isEqualTo("John Doe");
    }

    @Test
    void getActiveGuests_sortedByName() {
        // Если метод отсутствует в сервисе, тест упадёт. Добавьте заглушку или закомментируйте.
        when(guestService.getActiveGuestsSortedByName()).thenReturn(List.of(testGuest));

        ResponseEntity<List<GuestDTO>> response = guestController.getActiveGuests("name");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getActiveGuests_sortedByCheckout() {
        when(guestService.getActiveGuestsSortedByCheckout()).thenReturn(List.of(testGuest));

        ResponseEntity<List<GuestDTO>> response = guestController.getActiveGuests("checkout");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getTotalActiveGuests_success() {
        when(guestService.getTotalActiveGuests()).thenReturn(5);

        ResponseEntity<Integer> response = guestController.getTotalActiveGuests();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(5);
    }

    @Test
    void exportGuests_success() {
        when(guestService.exportToCsv("/path/file.csv")).thenReturn("Успех");

        ResponseEntity<String> response = guestController.exportGuests("/path/file.csv");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Успех");
    }

    @Test
    void importGuests_success() {
        when(guestService.importFromCsv("/path/file.csv")).thenReturn("Успех");

        ResponseEntity<String> response = guestController.importGuests("/path/file.csv");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Успех");
    }
}