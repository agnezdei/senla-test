package com.agnezdei.hotelmvc.controller;

import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.model.RoomType;
import com.agnezdei.hotelmvc.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    @Autowired private RoomService roomService;

    @PostMapping
    public ResponseEntity<String> addRoom(@RequestParam String number,
                                          @RequestParam RoomType type,
                                          @RequestParam double price,
                                          @RequestParam int capacity,
                                          @RequestParam int stars) throws BusinessLogicException {
        String result = roomService.addRoom(number, type, price, capacity, stars);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{roomNumber}/price")
    public ResponseEntity<String> changeRoomPrice(@PathVariable String roomNumber,
                                                  @RequestParam double newPrice) throws EntityNotFoundException, BusinessLogicException {
        return ResponseEntity.ok(roomService.changePrice(roomNumber, newPrice));
    }

    @PutMapping("/{roomNumber}/maintenance")
    public ResponseEntity<String> setRoomUnderMaintenance(@PathVariable String roomNumber) throws EntityNotFoundException, BusinessLogicException {
        return ResponseEntity.ok(roomService.setUnderMaintenance(roomNumber));
    }

    @PutMapping("/{roomNumber}/available")
    public ResponseEntity<String> setRoomAvailable(@PathVariable String roomNumber) throws EntityNotFoundException, BusinessLogicException {
        return ResponseEntity.ok(roomService.setAvailable(roomNumber));
    }

    @PostMapping("/export")
    public ResponseEntity<String> exportRooms(@RequestParam String filePath) {
        return ResponseEntity.ok(roomService.exportToCsv(filePath));
    }

    @PostMapping("/import")
    public ResponseEntity<String> importRooms(@RequestParam String filePath) {
        return ResponseEntity.ok(roomService.importFromCsv(filePath));
    }
}