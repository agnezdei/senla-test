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

import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired private BookingService bookingService;

    @PostMapping("/settle")
    public ResponseEntity<String> settleGuest(
            @RequestParam(name = "roomNumber") String roomNumber,
            @RequestBody GuestDTO guestDto,
            @RequestParam(name = "checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam(name = "checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate
    ) throws EntityNotFoundException, BusinessLogicException {
        Guest guest = GuestMapper.toEntity(guestDto);
        return ResponseEntity.ok(bookingService.settleGuest(roomNumber, guest, checkInDate, checkOutDate));
    }

    @PostMapping("/{roomNumber}/evict")
    public ResponseEntity<String> evictGuest(
            @PathVariable(name = "roomNumber") String roomNumber
    ) throws EntityNotFoundException, BusinessLogicException {
        return ResponseEntity.ok(bookingService.evictGuest(roomNumber));
    }

    @GetMapping("/{roomNumber}/payment")
    public ResponseEntity<Map<String, Object>> getPaymentForRoom(
            @PathVariable(name = "roomNumber") String roomNumber
    ) {
        Map<String, Object> paymentDetails = bookingService.getPaymentDetails(roomNumber);
        return ResponseEntity.ok(paymentDetails);
    }

    @PostMapping("/export")
    public ResponseEntity<String> exportBookings(
            @RequestParam(name = "filePath") String filePath
    ) {
        return ResponseEntity.ok(bookingService.exportToCsv(filePath));
    }

    @PostMapping("/import")
    public ResponseEntity<String> importBookings(
            @RequestParam(name = "filePath") String filePath
    ) {
        return ResponseEntity.ok(bookingService.importFromCsv(filePath));
    }
}