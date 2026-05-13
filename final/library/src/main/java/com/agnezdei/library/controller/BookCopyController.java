package com.agnezdei.library.controller;

import com.agnezdei.library.dto.BookCopyDTO;
import com.agnezdei.library.dto.BorrowRecordDTO;
import com.agnezdei.library.model.BookCopy;
import com.agnezdei.library.service.BookCopyService;
import com.agnezdei.library.service.BorrowRecordService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/copies")
public class BookCopyController {

    private static final Logger log = LoggerFactory.getLogger(BookCopyController.class);
    private final BookCopyService copyService;
    private final BorrowRecordService borrowRecordService;

    public BookCopyController(BookCopyService copyService,
                              BorrowRecordService borrowRecordService) {
        this.copyService = copyService;
        this.borrowRecordService = borrowRecordService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookCopyDTO> getCopy(@PathVariable("id") Long id) {
        log.info("GET /api/copies/{}", id);
        return ResponseEntity.ok(copyService.getCopyById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookCopyDTO>> getCopiesByStatus(@PathVariable("status") BookCopy.Status status) {
        log.info("GET /api/copies/status/{}", status);
        return ResponseEntity.ok(copyService.getCopiesByStatus(status));
    }

    @GetMapping("/by-catalog/{catalogId}")
    public ResponseEntity<List<BookCopyDTO>> getCopiesByCatalog(@PathVariable("catalogId") Long catalogId) {
        log.info("GET /api/copies/by-catalog/{}", catalogId);
        return ResponseEntity.ok(copyService.getCopiesByCatalog(catalogId));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<BorrowRecordDTO>> getCopyHistory(@PathVariable("id") Long id) {
        log.info("GET /api/copies/{}/history", id);
        return ResponseEntity.ok(borrowRecordService.getHistoryByCopy(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookCopyDTO> createCopy(@Valid @RequestBody BookCopyDTO dto) {
        log.info("POST /api/copies - создание экземпляра {}", dto.getInventoryNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(copyService.createCopy(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookCopyDTO> updateCopy(@PathVariable("id") Long id,
                                                  @Valid @RequestBody BookCopyDTO dto) {
        log.info("PUT /api/copies/{}", id);
        return ResponseEntity.ok(copyService.updateCopy(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCopy(@PathVariable("id") Long id) {
        log.info("DELETE /api/copies/{}", id);
        copyService.deleteCopy(id);
        return ResponseEntity.noContent().build();
    }
}