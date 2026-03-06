package com.agnezdei.hotelmvc.controller;

import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.service.ServiceService;
import com.agnezdei.hotelmvc.mapper.ServiceMapper;
import com.agnezdei.hotelmvc.dto.ServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    @Autowired private ServiceService serviceService;

    @PostMapping
    public ResponseEntity<String> addService(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "price") double price,
            @RequestParam(name = "category") ServiceCategory category
    ) throws BusinessLogicException {
        String result = serviceService.addService(name, price, category);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{serviceName}/price")
    public ResponseEntity<String> changeServicePrice(
            @PathVariable(name = "serviceName") String serviceName,
            @RequestParam(name = "newPrice") double newPrice
    ) throws EntityNotFoundException, BusinessLogicException {
        return ResponseEntity.ok(serviceService.changePrice(serviceName, newPrice));
    }

    @GetMapping
    public ResponseEntity<List<ServiceDTO>> getAllServices(
            @RequestParam(name = "sort", required = false) String sort
    ) {
        List<Service> services;
        if ("category,price".equals(sort)) {
            services = serviceService.getAllServicesSortedByCategoryAndPrice();
        } else {
            services = serviceService.getAllServices();
        }
        return ResponseEntity.ok(ServiceMapper.toDTOList(services));
    }

    @PostMapping("/export")
    public ResponseEntity<String> exportServices(
            @RequestParam(name = "filePath") String filePath
    ) {
        return ResponseEntity.ok(serviceService.exportToCsv(filePath));
    }

    @PostMapping("/import")
    public ResponseEntity<String> importServices(
            @RequestParam(name = "filePath") String filePath
    ) {
        return ResponseEntity.ok(serviceService.importFromCsv(filePath));
    }
}