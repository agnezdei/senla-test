package com.agnezdei.hotelmvc.controller;

import com.agnezdei.hotelmvc.service.GuestService;
import com.agnezdei.hotelmvc.mapper.GuestMapper;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.dto.GuestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guests")
public class GuestController {
    @Autowired private GuestService guestService;

    @GetMapping
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("GuestController is working");
    }

    @GetMapping("/active")
    public ResponseEntity<List<GuestDTO>> getActiveGuests(
            @RequestParam(required = false) String sort) {
        List<Guest> guests;
        if (sort != null) {
            guests = switch (sort) {
                case "name" -> guestService.getActiveGuestsSortedByName();
                case "checkout" -> guestService.getActiveGuestsSortedByCheckout();
                default -> guestService.getActiveGuests();
            };
        } else {
            guests = guestService.getActiveGuests();
        }
        return ResponseEntity.ok(GuestMapper.toDTOList(guests));
    }

    // Количество активных гостей
    @GetMapping("/active/count")
    public ResponseEntity<Integer> getTotalActiveGuests() {
        return ResponseEntity.ok(guestService.getTotalActiveGuests());
    }

    @PostMapping("/export")
    public ResponseEntity<String> exportGuests(@RequestParam String filePath) {
        return ResponseEntity.ok(guestService.exportToCsv(filePath));
    }

    @PostMapping("/import")
    public ResponseEntity<String> importGuests(@RequestParam String filePath) {
        return ResponseEntity.ok(guestService.importFromCsv(filePath));
    }
}