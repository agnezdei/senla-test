package com.agnezdei.hotelmvc.controller;

import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import com.agnezdei.hotelmvc.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    @Autowired private ServiceService serviceService;

    @PostMapping
    public ResponseEntity<String> addService(@RequestParam String name,
                                             @RequestParam double price,
                                             @RequestParam ServiceCategory category) throws BusinessLogicException {
        String result = serviceService.addService(name, price, category);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{serviceName}/price")
    public ResponseEntity<String> changeServicePrice(@PathVariable String serviceName,
                                                     @RequestParam double newPrice) throws EntityNotFoundException, BusinessLogicException {
        return ResponseEntity.ok(serviceService.changePrice(serviceName, newPrice));
    }

    @PostMapping("/export")
    public ResponseEntity<String> exportServices(@RequestParam String filePath) {
        return ResponseEntity.ok(serviceService.exportToCsv(filePath));
    }

    @PostMapping("/import")
    public ResponseEntity<String> importServices(@RequestParam String filePath) {
        return ResponseEntity.ok(serviceService.importFromCsv(filePath));
    }
}