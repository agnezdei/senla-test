package com.agnezdei.library.controller;

import com.agnezdei.library.dto.PasswordChangeDTO;
import com.agnezdei.library.dto.UsernameUpdateDTO;
import com.agnezdei.library.dto.UserResponseDTO;
import com.agnezdei.library.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        Long userId = extractUserId(authentication);
        log.info("GET /api/users/me - запрос профиля userId={}", userId);
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/me/username")
    public ResponseEntity<Void> updateUsername(Authentication authentication,
                                               @Valid @RequestBody UsernameUpdateDTO dto) {
        Long userId = extractUserId(authentication);
        log.info("PUT /api/users/me/username - userId={}", userId);
        userService.updateUsername(userId, dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> updatePassword(Authentication authentication,
                                               @Valid @RequestBody PasswordChangeDTO dto) {
        Long userId = extractUserId(authentication);
        log.info("PUT /api/users/me/password - userId={}", userId);
        userService.updatePassword(userId, dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("GET /api/users/all - администратор запросил всех пользователей");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        log.info("DELETE /api/users/{} - администратор удаляет пользователя", userId);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    private Long extractUserId(Authentication auth) {
        String username = auth.getName();
        return userService.findIdByUsername(username);
    }
}