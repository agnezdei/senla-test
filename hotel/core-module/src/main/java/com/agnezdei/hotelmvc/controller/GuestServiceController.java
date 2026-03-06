package com.agnezdei.hotelmvc.controller;

import com.agnezdei.hotelmvc.dto.GuestServiceDTO;
import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.mapper.GuestServiceMapper;
import com.agnezdei.hotelmvc.model.GuestService;
import com.agnezdei.hotelmvc.service.GuestServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/guest-services")
public class GuestServiceController {

    private final GuestServiceService guestServiceService;

    @Autowired
    public GuestServiceController(GuestServiceService guestServiceService) {
        this.guestServiceService = guestServiceService;
    }

    @PostMapping
    public ResponseEntity<String> addServiceToGuest(
            @RequestParam(name = "guestPassport") String guestPassport,
            @RequestParam(name = "serviceName") String serviceName,
            @RequestParam(name = "serviceDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate serviceDate
    ) throws EntityNotFoundException, BusinessLogicException {
        return ResponseEntity.ok(guestServiceService.addServiceToGuest(guestPassport, serviceName, serviceDate));
    }

    @DeleteMapping("/{guestServiceId}")
    public ResponseEntity<String> removeServiceFromGuest(
            @PathVariable(name = "guestServiceId") Long guestServiceId
    ) throws EntityNotFoundException, BusinessLogicException {
        return ResponseEntity.ok(guestServiceService.removeServiceFromGuest(guestServiceId));
    }

    @GetMapping("/guests/{guestId}")
    public ResponseEntity<List<GuestServiceDTO>> getGuestServices(
            @PathVariable(name = "guestId") Long guestId
    ) throws BusinessLogicException {
        return ResponseEntity.ok(guestServiceService.getGuestServices(guestId));
    }

    @GetMapping("/guests/by-name/{guestName}")
    public ResponseEntity<List<GuestServiceDTO>> getGuestServicesByName(
            @PathVariable(name = "guestName") String guestName,
            @RequestParam(name = "sort", required = false) String sort
    ) throws BusinessLogicException {
        List<GuestService> services;
        if ("price".equals(sort)) {
            services = guestServiceService.getGuestServicesByNameSortedByPrice(guestName);
        } else if ("date".equals(sort)) {
            services = guestServiceService.getGuestServicesByNameSortedByDate(guestName);
        } else {
            services = guestServiceService.getGuestServicesByNameSortedByDate(guestName);
        }
        return ResponseEntity.ok(GuestServiceMapper.toDTOList(services));
    }

    @PostMapping("/export")
    public ResponseEntity<String> exportGuestServices(
            @RequestParam(name = "filePath") String filePath
    ) {
        return ResponseEntity.ok(guestServiceService.exportToCsv(filePath));
    }

    @PostMapping("/import")
    public ResponseEntity<String> importGuestServices(
            @RequestParam(name = "filePath") String filePath
    ) {
        return ResponseEntity.ok(guestServiceService.importFromCsv(filePath));
    }
}