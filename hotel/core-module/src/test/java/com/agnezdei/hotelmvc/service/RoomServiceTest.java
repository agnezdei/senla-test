package com.agnezdei.hotelmvc.service;

import com.agnezdei.hotelmvc.config.AppConfig;
import com.agnezdei.hotelmvc.csv.CsvExporter;
import com.agnezdei.hotelmvc.csv.RoomCsvImporter;
import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.model.*;
import com.agnezdei.hotelmvc.repository.BookingDAO;
import com.agnezdei.hotelmvc.repository.RoomDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock private RoomDAO roomDAO;
    @Mock private BookingDAO bookingDAO;
    @Mock private AppConfig config;
    @Mock private CsvExporter csvExporter;
    @Mock private RoomCsvImporter roomImporter;

    @InjectMocks private RoomService roomService;

    private Room room;

    @BeforeEach
    void setUp() {
        room = new Room("101", RoomType.STANDARD, 100.0, 2, 3);
        room.setId(1L);
        room.setStatus(RoomStatus.AVAILABLE);
    }

    @Test
    void exportToCsv_success() throws Exception {
        when(roomDAO.findAll()).thenReturn(Collections.singletonList(room));
        doNothing().when(csvExporter).exportRooms(anyList(), anyString());

        String result = roomService.exportToCsv("/path/file.csv");

        assertThat(result).startsWith("Успех");
    }

    @Test
    void importFromCsv_success() {
        when(roomImporter.importRooms(anyString())).thenReturn("Imported");

        String result = roomService.importFromCsv("/path/file.csv");

        assertThat(result).isEqualTo("Imported");
    }

    @Test
    void setUnderMaintenance_success() throws Exception {
        when(roomDAO.findByNumber("101")).thenReturn(Optional.of(room));
        when(config.isAllowRoomStatusChange()).thenReturn(true);

        String result = roomService.setUnderMaintenance("101");

        assertThat(result).contains("Успех");
        assertThat(room.getStatus()).isEqualTo(RoomStatus.UNDER_MAINTENANCE);
        verify(roomDAO).update(room);
    }

    @Test
    void setUnderMaintenance_roomNotFound() {
        when(roomDAO.findByNumber("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.setUnderMaintenance("999"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void setUnderMaintenance_notAllowed() {
        when(roomDAO.findByNumber("101")).thenReturn(Optional.of(room));
        when(config.isAllowRoomStatusChange()).thenReturn(false);

        assertThatThrownBy(() -> roomService.setUnderMaintenance("101"))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("запрещено");
    }

    @Test
    void setUnderMaintenance_roomOccupied() {
        room.setStatus(RoomStatus.OCCUPIED);
        when(roomDAO.findByNumber("101")).thenReturn(Optional.of(room));
        when(config.isAllowRoomStatusChange()).thenReturn(true);

        assertThatThrownBy(() -> roomService.setUnderMaintenance("101"))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("занятый номер");
    }

    @Test
    void setAvailable_success() throws Exception {
        room.setStatus(RoomStatus.UNDER_MAINTENANCE);
        when(roomDAO.findByNumber("101")).thenReturn(Optional.of(room));
        when(config.isAllowRoomStatusChange()).thenReturn(true);

        String result = roomService.setAvailable("101");

        assertThat(result).contains("доступен");
        assertThat(room.getStatus()).isEqualTo(RoomStatus.AVAILABLE);
    }

    @Test
    void setAvailable_roomOccupied() {
        room.setStatus(RoomStatus.OCCUPIED);
        when(roomDAO.findByNumber("101")).thenReturn(Optional.of(room));
        when(config.isAllowRoomStatusChange()).thenReturn(true);

        assertThatThrownBy(() -> roomService.setAvailable("101"))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("занят");
    }

    @Test
    void changePrice_success() throws Exception {
        when(roomDAO.findByNumber("101")).thenReturn(Optional.of(room));

        String result = roomService.changePrice("101", 150.0);

        assertThat(result).contains("изменена");
        assertThat(room.getPrice()).isEqualTo(150.0);
        verify(roomDAO).update(room);
    }

    @Test
    void changePrice_roomNotFound() {
        when(roomDAO.findByNumber("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.changePrice("999", 150.0))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void addRoom_success() throws Exception {
        when(roomDAO.findByNumber("101")).thenReturn(Optional.empty());
        when(roomDAO.save(any(Room.class))).thenAnswer(inv -> inv.getArgument(0));

        String result = roomService.addRoom("101", RoomType.STANDARD, 100.0, 2, 3);

        assertThat(result).contains("Успех");
        verify(roomDAO).save(any(Room.class));
    }

    @Test
    void addRoom_alreadyExists() {
        when(roomDAO.findByNumber("101")).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> roomService.addRoom("101", RoomType.STANDARD, 100.0, 2, 3))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("уже существует");
    }

    @Test
    void getAllRooms_success() {
        when(roomDAO.findAll()).thenReturn(Collections.singletonList(room));

        List<Room> rooms = roomService.getAllRooms();

        assertThat(rooms).containsExactly(room);
    }

    @Test
    void getLastThreeGuestsOfRoom_success() {
        when(roomDAO.findByNumber("101")).thenReturn(Optional.of(room));
        when(bookingDAO.findLastThreeGuestsByRoomId(room.getId())).thenReturn(Collections.emptyList());

        List<Booking> history = roomService.getLastThreeGuestsOfRoom("101");

        assertThat(history).isEmpty();
    }

    @Test
    void getLastThreeGuestsOfRoom_roomNotFound() {
        when(roomDAO.findByNumber("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.getLastThreeGuestsOfRoom("999"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findByNumber_found() {
        when(roomDAO.findByNumber("101")).thenReturn(Optional.of(room));

        Optional<Room> found = roomService.findByNumber("101");

        assertThat(found).isPresent().contains(room);
    }

    @Test
    void update_success() {
        doAnswer(invocation -> null).when(roomDAO).update(room);

        roomService.update(room);

        verify(roomDAO).update(room);
    }
}