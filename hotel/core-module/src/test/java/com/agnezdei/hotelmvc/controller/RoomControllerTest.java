package com.agnezdei.hotelmvc.controller;

import com.agnezdei.hotelmvc.dto.BookingDTO;
import com.agnezdei.hotelmvc.dto.RoomDTO;
import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.model.Booking;
import com.agnezdei.hotelmvc.model.Room;
import com.agnezdei.hotelmvc.model.RoomType;
import com.agnezdei.hotelmvc.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomController roomController;

    private Room testRoom;

    @BeforeEach
    void setUp() {
        testRoom = new Room("101", RoomType.STANDARD, 100.0, 2, 3);
        testRoom.setId(1L);
    }

    @Test
    void addRoom_success() throws BusinessLogicException {
        String expected = "Успех: Добавлен номер";
        when(roomService.addRoom("101", RoomType.STANDARD, 100.0, 2, 3)).thenReturn(expected);

        ResponseEntity<String> response = roomController.addRoom("101", RoomType.STANDARD, 100.0, 2, 3);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    void addRoom_alreadyExists() throws BusinessLogicException {
        when(roomService.addRoom(anyString(), any(), anyDouble(), anyInt(), anyInt()))
                .thenThrow(new BusinessLogicException("Номер уже существует"));

        assertThatThrownBy(() -> roomController.addRoom("101", RoomType.STANDARD, 100.0, 2, 3))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void changeRoomPrice_success() throws Exception {
        String expected = "Успех: Цена изменена";
        when(roomService.changePrice("101", 150.0)).thenReturn(expected);

        ResponseEntity<String> response = roomController.changeRoomPrice("101", 150.0);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    void changeRoomPrice_roomNotFound() throws Exception {
        when(roomService.changePrice("101", 150.0))
                .thenThrow(new EntityNotFoundException("Комната не найдена"));

        assertThatThrownBy(() -> roomController.changeRoomPrice("101", 150.0))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void setRoomUnderMaintenance_success() throws Exception {
        String expected = "Успех: Номер на ремонте";
        when(roomService.setUnderMaintenance("101")).thenReturn(expected);

        ResponseEntity<String> response = roomController.setRoomUnderMaintenance("101");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    void setRoomUnderMaintenance_businessError() throws Exception {
        when(roomService.setUnderMaintenance("101"))
                .thenThrow(new BusinessLogicException("Номер занят"));

        assertThatThrownBy(() -> roomController.setRoomUnderMaintenance("101"))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void setRoomAvailable_success() throws Exception {
        String expected = "Успех: Номер доступен";
        when(roomService.setAvailable("101")).thenReturn(expected);

        ResponseEntity<String> response = roomController.setRoomAvailable("101");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    void getAllRooms_success() {
        when(roomService.getAllRooms()).thenReturn(List.of(testRoom));

        ResponseEntity<List<RoomDTO>> response = roomController.getAllRooms(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getNumber()).isEqualTo("101");
    }

    @Test
    void getAllRooms_sortedByPrice() {
        when(roomService.getAllRoomsSortedByPrice()).thenReturn(List.of(testRoom));

        ResponseEntity<List<RoomDTO>> response = roomController.getAllRooms("price");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getAvailableRooms_success() {
        when(roomService.getAvailableRooms()).thenReturn(List.of(testRoom));

        ResponseEntity<List<RoomDTO>> response = roomController.getAvailableRooms(null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getAvailableRoomsByDate() {
        when(roomService.getRoomsAvailableByDate(any(LocalDate.class))).thenReturn(List.of(testRoom));

        ResponseEntity<List<RoomDTO>> response = roomController.getAvailableRooms(null, LocalDate.of(2025, 1, 1));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getTotalAvailableRooms_success() {
        when(roomService.getTotalAvailableRooms()).thenReturn(5);

        ResponseEntity<Integer> response = roomController.getTotalAvailableRooms();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(5);
    }

    @Test
    void getLastThreeGuests_success() {
        when(roomService.getLastThreeGuestsOfRoom("101")).thenReturn(List.of());

        ResponseEntity<List<BookingDTO>> response = roomController.getLastThreeGuests("101");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getRoomByNumber_success() {
        when(roomService.findByNumber("101")).thenReturn(Optional.of(testRoom));

        ResponseEntity<RoomDTO> response = roomController.getRoomByNumber("101");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNumber()).isEqualTo("101");
    }

    @Test
    void getRoomByNumber_notFound() {
        when(roomService.findByNumber("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomController.getRoomByNumber("999"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void exportRooms_success() {
        when(roomService.exportToCsv("/path/file.csv")).thenReturn("Успех");

        ResponseEntity<String> response = roomController.exportRooms("/path/file.csv");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Успех");
    }

    @Test
    void importRooms_success() {
        when(roomService.importFromCsv("/path/file.csv")).thenReturn("Успех");

        ResponseEntity<String> response = roomController.importRooms("/path/file.csv");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Успех");
    }
}