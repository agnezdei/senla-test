package com.agnezdei.library.controller;

import com.agnezdei.library.dto.CatalogDTO;
import com.agnezdei.library.service.CatalogService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogs")
public class CatalogController {

    private static final Logger log = LoggerFactory.getLogger(CatalogController.class);
    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/roots")
    public ResponseEntity<List<CatalogDTO>> getRootCatalogs() {
        log.info("GET /api/catalogs/roots");
        return ResponseEntity.ok(catalogService.getRootCatalogs());
    }

    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<CatalogDTO>> getChildren(@PathVariable("parentId") Long parentId) {
        log.info("GET /api/catalogs/{}/children", parentId);
        return ResponseEntity.ok(catalogService.getChildren(parentId));
    }

    @GetMapping("/tree")
    public ResponseEntity<List<CatalogDTO>> getFullTree() {
        log.info("GET /api/catalogs/tree");
        return ResponseEntity.ok(catalogService.getFullTree());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CatalogDTO> getCatalog(@PathVariable("id") Long id) {
        log.info("GET /api/catalogs/{}", id);
        return ResponseEntity.ok(catalogService.getCatalogTree(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CatalogDTO> createCatalog(@Valid @RequestBody CatalogDTO dto) {
        log.info("POST /api/catalogs - создание каталога {}", dto.getName());
        CatalogDTO created = catalogService.createCatalog(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CatalogDTO> updateCatalog(@PathVariable("id") Long id,
                                                    @Valid @RequestBody CatalogDTO dto) {
        log.info("PUT /api/catalogs/{} - обновление", id);
        return ResponseEntity.ok(catalogService.updateCatalog(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCatalog(@PathVariable("id") Long id) {
        log.info("DELETE /api/catalogs/{}", id);
        catalogService.deleteCatalog(id);
        return ResponseEntity.noContent().build();
    }
}