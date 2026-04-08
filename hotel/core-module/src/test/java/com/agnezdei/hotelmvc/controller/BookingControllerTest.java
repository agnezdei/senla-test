package com.agnezdei.hotelmvc.controller;

import com.agnezdei.hotelmvc.dto.GuestDTO;
import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.mapper.GuestMapper;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void settleGuest_success() throws Exception {
        GuestDTO guestDto = new GuestDTO();
        guestDto.setName("John Doe");
        guestDto.setPassportNumber("AB123456");
        Guest guest = GuestMapper.toEntity(guestDto);

        when(bookingService.settleGuest(eq("101"), any(Guest.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn("Успех: John Doe заселен");

        ResponseEntity<String> response = bookingController.settleGuest("101", guestDto,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 5));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Успех: John Doe заселен");
    }

    @Test
    void settleGuest_roomNotFound() throws Exception {
        GuestDTO guestDto = new GuestDTO();
        guestDto.setName("John Doe");
        guestDto.setPassportNumber("AB123456");
        Guest guest = GuestMapper.toEntity(guestDto);

        when(bookingService.settleGuest(anyString(), any(Guest.class), any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new EntityNotFoundException("Номер не найден"));

        assertThatThrownBy(() -> bookingController.settleGuest("999", guestDto,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 5)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void settleGuest_businessLogicException() throws Exception {
        GuestDTO guestDto = new GuestDTO();
        guestDto.setName("John Doe");
        guestDto.setPassportNumber("AB123456");
        Guest guest = GuestMapper.toEntity(guestDto);

        when(bookingService.settleGuest(anyString(), any(Guest.class), any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new BusinessLogicException("Номер занят"));

        assertThatThrownBy(() -> bookingController.settleGuest("101", guestDto,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 5)))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void evictGuest_success() throws Exception {
        when(bookingService.evictGuest("101")).thenReturn("Успех: John Doe выселен");

        ResponseEntity<String> response = bookingController.evictGuest("101");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Успех: John Doe выселен");
    }

    @Test
    void evictGuest_notFound() throws Exception {
        when(bookingService.evictGuest("999")).thenThrow(new EntityNotFoundException("Номер не найден"));

        assertThatThrownBy(() -> bookingController.evictGuest("999"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getPaymentForRoom_success() {
        Map<String, Object> paymentDetails = new HashMap<>();
        paymentDetails.put("totalCost", 500.0);
        when(bookingService.getPaymentDetails("101")).thenReturn(paymentDetails);

        ResponseEntity<Map<String, Object>> response = bookingController.getPaymentForRoom("101");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("totalCost", 500.0);
    }

    @Test
    void getPaymentForRoom_notFound() {
        when(bookingService.getPaymentDetails("999")).thenThrow(new EntityNotFoundException("Комната не найдена"));

        assertThatThrownBy(() -> bookingController.getPaymentForRoom("999"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void exportBookings_success() {
        when(bookingService.exportToCsv("/path/file.csv")).thenReturn("Успех: экспорт выполнен");

        ResponseEntity<String> response = bookingController.exportBookings("/path/file.csv");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Успех: экспорт выполнен");
    }

    @Test
    void importBookings_success() {
        when(bookingService.importFromCsv("/path/file.csv")).thenReturn("Успех: импорт выполнен");

        ResponseEntity<String> response = bookingController.importBookings("/path/file.csv");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Успех: импорт выполнен");
    }
}