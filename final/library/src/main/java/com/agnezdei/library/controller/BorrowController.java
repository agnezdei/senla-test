package com.agnezdei.library.controller;

import com.agnezdei.library.dto.BorrowRecordDTO;
import com.agnezdei.library.service.BorrowRecordService;
import com.agnezdei.library.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {

    private static final Logger log = LoggerFactory.getLogger(BorrowController.class);
    private final BorrowRecordService borrowService;
    private final UserService userService;

    public BorrowController(BorrowRecordService borrowService, UserService userService) {
        this.borrowService = borrowService;
        this.userService = userService;
    }

    @PostMapping("/borrow")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BorrowRecordDTO> borrowBook(Authentication authentication,
                                                      @RequestParam Long copyId,
                                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        Long userId = extractUserId(authentication);
        log.info("POST /api/borrows/borrow - userId={}, copyId={}", userId, copyId);
        BorrowRecordDTO record = borrowService.borrowBook(userId, copyId, dueDate);
        return ResponseEntity.ok(record);
    }

    @PostMapping("/return/{recordId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BorrowRecordDTO> returnBook(@PathVariable Long recordId) {
        log.info("POST /api/borrows/return/{}", recordId);
        return ResponseEntity.ok(borrowService.returnBook(recordId));
    }

    @PostMapping("/return-by-copy/{copyId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BorrowRecordDTO> returnBookByCopy(@PathVariable Long copyId) {
        log.info("POST /api/borrows/return-by-copy/{}", copyId);
        return ResponseEntity.ok(borrowService.returnBookByCopy(copyId));
    }

    @PostMapping("/extend/{recordId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BorrowRecordDTO> extendDueDate(@PathVariable Long recordId,
                                                         @RequestParam int daysToAdd) {
        log.info("POST /api/borrows/extend/{}?days={}", recordId, daysToAdd);
        return ResponseEntity.ok(borrowService.extendDueDate(recordId, daysToAdd));
    }

    @GetMapping("/me/active")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BorrowRecordDTO>> getMyActiveBorrows(Authentication authentication) {
        Long userId = extractUserId(authentication);
        log.info("GET /api/borrows/me/active - userId={}", userId);
        return ResponseEntity.ok(borrowService.getActiveBorrowsByUser(userId));
    }

    @GetMapping("/me/history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BorrowRecordDTO>> getMyHistory(Authentication authentication) {
        Long userId = extractUserId(authentication);
        log.info("GET /api/borrows/me/history - userId={}", userId);
        return ResponseEntity.ok(borrowService.getHistoryByUser(userId));
    }

    @GetMapping("/me/overdue")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BorrowRecordDTO>> getMyOverdue(Authentication authentication) {
        Long userId = extractUserId(authentication);
        log.info("GET /api/borrows/me/overdue - userId={}", userId);
        return ResponseEntity.ok(borrowService.getOverdueByUser(userId));
    }

    @GetMapping("/admin/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BorrowRecordDTO>> getAllActiveBorrows() {
        log.info("GET /api/borrows/admin/active");
        return ResponseEntity.ok(borrowService.getAllActiveBorrows());
    }

    @GetMapping("/admin/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BorrowRecordDTO>> getAllOverdue() {
        log.info("GET /api/borrows/admin/overdue");
        return ResponseEntity.ok(borrowService.getOverdueRecords());
    }

    @GetMapping("/admin/user/{userId}/history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BorrowRecordDTO>> getUserHistory(@PathVariable Long userId) {
        log.info("GET /api/borrows/admin/user/{}/history", userId);
        return ResponseEntity.ok(borrowService.getHistoryByUser(userId));
    }

    @PostMapping("/admin/force-return/{recordId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BorrowRecordDTO> forceReturn(@PathVariable Long recordId) {
        log.info("POST /api/borrows/admin/force-return/{}", recordId);
        return ResponseEntity.ok(borrowService.returnBook(recordId));
    }

    private Long extractUserId(Authentication auth) {
        String username = auth.getName();
        return userService.findIdByUsername(username);
    }
}