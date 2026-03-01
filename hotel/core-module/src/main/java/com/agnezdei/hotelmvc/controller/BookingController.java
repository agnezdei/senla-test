package com.agnezdei.hotelmvc.controller;

import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.service.BookingService;
import com.agnezdei.hotelmvc.dto.GuestDTO;
import com.agnezdei.hotelmvc.mapper.GuestMapper;
import com.agnezdei.hotelmvc.model.Guest;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired private BookingService bookingService;

    @PostMapping("/settle")
    public ResponseEntity<String> settleGuest(@RequestParam String roomNumber,
                                              @RequestBody GuestDTO guestDto,
                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) throws EntityNotFoundException, BusinessLogicException {
        Guest guest = GuestMapper.toEntity(guestDto);
        return ResponseEntity.ok(bookingService.settleGuest(roomNumber, guest, checkInDate, checkOutDate));
    }

    @PostMapping("/{roomNumber}/evict")
    public ResponseEntity<String> evictGuest(@PathVariable String roomNumber) throws EntityNotFoundException, BusinessLogicException {
        return ResponseEntity.ok(bookingService.evictGuest(roomNumber));
    }

    @PostMapping("/export")
    public ResponseEntity<String> exportBookings(@RequestParam String filePath) {
        return ResponseEntity.ok(bookingService.exportToCsv(filePath));
    }

    @PostMapping("/import")
    public ResponseEntity<String> importBookings(@RequestParam String filePath) {
        return ResponseEntity.ok(bookingService.importFromCsv(filePath));
    }
}