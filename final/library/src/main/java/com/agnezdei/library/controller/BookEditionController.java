package com.agnezdei.library.controller;

import com.agnezdei.library.dto.BookEditionDTO;
import com.agnezdei.library.service.BookEditionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/editions")
public class BookEditionController {

    private static final Logger log = LoggerFactory.getLogger(BookEditionController.class);
    private final BookEditionService editionService;

    public BookEditionController(BookEditionService editionService) {
        this.editionService = editionService;
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<BookEditionDTO>> searchByTitle(@RequestParam String title) {
        log.info("GET /api/editions/search/title?title={}", title);
        return ResponseEntity.ok(editionService.searchByTitle(title));
    }

    @GetMapping("/search/author")
    public ResponseEntity<List<BookEditionDTO>> searchByAuthor(@RequestParam String author) {
        log.info("GET /api/editions/search/author?author={}", author);
        return ResponseEntity.ok(editionService.searchByAuthor(author));
    }

    @GetMapping
    public ResponseEntity<List<BookEditionDTO>> getAllEditions() {
        log.info("GET /api/editions");
        return ResponseEntity.ok(editionService.getAllEditions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookEditionDTO> getEdition(@PathVariable Long id) {
        log.info("GET /api/editions/{}", id);
        return ResponseEntity.ok(editionService.getEditionById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookEditionDTO> createEdition(@Valid @RequestBody BookEditionDTO dto) {
        log.info("POST /api/editions - создание издания {}", dto.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(editionService.createEdition(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookEditionDTO> updateEdition(@PathVariable Long id,
                                                        @Valid @RequestBody BookEditionDTO dto) {
        log.info("PUT /api/editions/{}", id);
        return ResponseEntity.ok(editionService.updateEdition(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEdition(@PathVariable Long id) {
        log.info("DELETE /api/editions/{}", id);
        editionService.deleteEdition(id);
        return ResponseEntity.noContent().build();
    }
}