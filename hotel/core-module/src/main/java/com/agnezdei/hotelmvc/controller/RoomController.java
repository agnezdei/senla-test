package com.agnezdei.hotelmvc.controller;

import com.agnezdei.hotelmvc.dto.BookingDTO;
import com.agnezdei.hotelmvc.dto.RoomDTO;
import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.mapper.BookingMapper;
import com.agnezdei.hotelmvc.mapper.RoomMapper;
import com.agnezdei.hotelmvc.model.Booking;
import com.agnezdei.hotelmvc.model.Room;
import com.agnezdei.hotelmvc.model.RoomType;
import com.agnezdei.hotelmvc.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @PostMapping
    public ResponseEntity<String> addRoom(
            @RequestParam(name = "number") String number,
            @RequestParam(name = "type") RoomType type,
            @RequestParam(name = "price") double price,
            @RequestParam(name = "capacity") int capacity,
            @RequestParam(name = "stars") int stars
    ) throws BusinessLogicException {
        String result = roomService.addRoom(number, type, price, capacity, stars);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{roomNumber}/price")
    public ResponseEntity<String> changeRoomPrice(
            @PathVariable(name = "roomNumber") String roomNumber,
            @RequestParam(name = "newPrice") double newPrice
    ) throws EntityNotFoundException, BusinessLogicException {
        return ResponseEntity.ok(roomService.changePrice(roomNumber, newPrice));
    }

    @PutMapping("/{roomNumber}/maintenance")
    public ResponseEntity<String> setRoomUnderMaintenance(
            @PathVariable(name = "roomNumber") String roomNumber
    ) throws EntityNotFoundException, BusinessLogicException {
        return ResponseEntity.ok(roomService.setUnderMaintenance(roomNumber));
    }

    @PutMapping("/{roomNumber}/available")
    public ResponseEntity<String> setRoomAvailable(
            @PathVariable(name = "roomNumber") String roomNumber
    ) throws EntityNotFoundException, BusinessLogicException {
        return ResponseEntity.ok(roomService.setAvailable(roomNumber));
    }

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms(
            @RequestParam(name = "sort", required = false) String sort
    ) {
        List<Room> rooms;
        if (sort == null) {
            rooms = roomService.getAllRooms();
        } else {
            switch (sort) {
                case "price":
                    rooms = roomService.getAllRoomsSortedByPrice();
                    break;
                case "capacity":
                    rooms = roomService.getAllRoomsSortedByCapacity();
                    break;
                case "stars":
                    rooms = roomService.getAllRoomsSortedByStars();
                    break;
                default:
                    rooms = roomService.getAllRooms();
            }
        }
        return ResponseEntity.ok(RoomMapper.toDTOList(rooms));
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomDTO>> getAvailableRooms(
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<Room> rooms;
        if (date != null) {
            rooms = roomService.getRoomsAvailableByDate(date);
        } else {
            if (sort == null) {
                rooms = roomService.getAvailableRooms();
            } else {
                switch (sort) {
                    case "price":
                        rooms = roomService.getAvailableRoomsSortedByPrice();
                        break;
                    case "capacity":
                        rooms = roomService.getAvailableRoomsSortedByCapacity();
                        break;
                    case "stars":
                        rooms = roomService.getAvailableRoomsSortedByStars();
                        break;
                    default:
                        rooms = roomService.getAvailableRooms();
                }
            }
        }
        return ResponseEntity.ok(RoomMapper.toDTOList(rooms));
    }

    @GetMapping("/available/count")
    public ResponseEntity<Integer> getTotalAvailableRooms() {
        return ResponseEntity.ok(roomService.getTotalAvailableRooms());
    }

    @GetMapping("/{roomNumber}/last-guests")
    public ResponseEntity<List<BookingDTO>> getLastThreeGuests(
            @PathVariable(name = "roomNumber") String roomNumber
    ) {
        List<Booking> bookings = roomService.getLastThreeGuestsOfRoom(roomNumber);
        return ResponseEntity.ok(BookingMapper.toDTOList(bookings));
    }

    @GetMapping("/{roomNumber}")
    public ResponseEntity<RoomDTO> getRoomByNumber(
            @PathVariable(name = "roomNumber") String roomNumber
    ) {
        Room room = roomService.findByNumber(roomNumber)
                .orElseThrow(() -> new EntityNotFoundException("Комната с номером " + roomNumber + " не найдена"));
        return ResponseEntity.ok(RoomMapper.toDTO(room));
    }

    @PostMapping("/export")
    public ResponseEntity<String> exportRooms(
            @RequestParam(name = "filePath") String filePath
    ) {
        return ResponseEntity.ok(roomService.exportToCsv(filePath));
    }

    @PostMapping("/import")
    public ResponseEntity<String> importRooms(
            @RequestParam(name = "filePath") String filePath
    ) {
        return ResponseEntity.ok(roomService.importFromCsv(filePath));
    }
}