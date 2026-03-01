package com.agnezdei.hotelmvc.controller;

import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.service.GuestServiceService;
import com.agnezdei.hotelmvc.dto.GuestServiceDTO;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guest-services")
public class GuestServiceController {
    @Autowired private GuestServiceService guestServiceService;

    @PostMapping
    public ResponseEntity<String> addServiceToGuest(@RequestParam String guestPassport,
                                                    @RequestParam String serviceName,
                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate serviceDate) throws EntityNotFoundException, BusinessLogicException {
        return ResponseEntity.ok(guestServiceService.addServiceToGuest(guestPassport, serviceName, serviceDate));
    }

    @DeleteMapping("/{guestServiceId}")
    public ResponseEntity<String> removeServiceFromGuest(@PathVariable("guestServiceId") Long guestServiceId) throws EntityNotFoundException, BusinessLogicException {
        return ResponseEntity.ok(guestServiceService.removeServiceFromGuest(guestServiceId));
    }

    @GetMapping("/guests/{guestId}")
    public ResponseEntity<List<GuestServiceDTO>> getGuestServices(@PathVariable("guestId") Long guestId) throws BusinessLogicException {
        return ResponseEntity.ok(guestServiceService.getGuestServices(guestId));
    }

    @GetMapping("/guests/by-name/{guestName}")
    public ResponseEntity<List<GuestServiceDTO>> getGuestServicesByName(@PathVariable String guestName) throws BusinessLogicException {
        return ResponseEntity.ok(guestServiceService.getGuestServicesByName(guestName));
    }

    @PostMapping("/export")
    public ResponseEntity<String> exportGuestServices(@RequestParam String filePath) {
        return ResponseEntity.ok(guestServiceService.exportToCsv(filePath));
    }

    @PostMapping("/import")
    public ResponseEntity<String> importGuestServices(@RequestParam String filePath) {
        return ResponseEntity.ok(guestServiceService.importFromCsv(filePath));
    }
}