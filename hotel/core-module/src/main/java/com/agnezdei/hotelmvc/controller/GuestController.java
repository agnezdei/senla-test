package com.agnezdei.hotelmvc.controller;

import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.service.GuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guests")
public class GuestController {
    @Autowired private GuestService guestService;

    @GetMapping
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("GuestController is working");
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