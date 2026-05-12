package com.agnezdei.library.controller;

import com.agnezdei.library.dto.BorrowRecordDTO;
import com.agnezdei.library.service.BorrowRecordService;
import com.agnezdei.library.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BorrowControllerTest {

    @Mock
    private BorrowRecordService borrowService;

    @Mock
    private UserService userService;

    @InjectMocks
    private BorrowController borrowController;

    private Authentication userAuthentication;
    private static final String TEST_USERNAME = "john";
    private static final Long TEST_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        userAuthentication = new UsernamePasswordAuthenticationToken(TEST_USERNAME, null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    // === USER методы ===

    @Test
    void borrowBook_shouldReturnOk() {
        Long copyId = 10L;
        LocalDate dueDate = null;
        BorrowRecordDTO expected = new BorrowRecordDTO();
        when(userService.findIdByUsername(TEST_USERNAME)).thenReturn(TEST_USER_ID);
        when(borrowService.borrowBook(eq(TEST_USER_ID), eq(copyId), eq(dueDate))).thenReturn(expected);

        ResponseEntity<BorrowRecordDTO> response = borrowController.borrowBook(userAuthentication, copyId, dueDate);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    void borrowBook_withDueDate_shouldReturnOk() {
        Long copyId = 10L;
        LocalDate dueDate = LocalDate.now().plusDays(14);
        BorrowRecordDTO expected = new BorrowRecordDTO();
        when(userService.findIdByUsername(TEST_USERNAME)).thenReturn(TEST_USER_ID);
        when(borrowService.borrowBook(eq(TEST_USER_ID), eq(copyId), eq(dueDate))).thenReturn(expected);

        ResponseEntity<BorrowRecordDTO> response = borrowController.borrowBook(userAuthentication, copyId, dueDate);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void returnBook_shouldReturnOk() {
        Long recordId = 100L;
        BorrowRecordDTO expected = new BorrowRecordDTO();
        when(borrowService.returnBook(recordId)).thenReturn(expected);

        ResponseEntity<BorrowRecordDTO> response = borrowController.returnBook(recordId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    void returnBookByCopy_shouldReturnOk() {
        Long copyId = 10L;
        BorrowRecordDTO expected = new BorrowRecordDTO();
        when(borrowService.returnBookByCopy(copyId)).thenReturn(expected);

        ResponseEntity<BorrowRecordDTO> response = borrowController.returnBookByCopy(copyId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    void extendDueDate_shouldReturnOk() {
        Long recordId = 100L;
        int daysToAdd = 7;
        BorrowRecordDTO expected = new BorrowRecordDTO();
        when(borrowService.extendDueDate(recordId, daysToAdd)).thenReturn(expected);

        ResponseEntity<BorrowRecordDTO> response = borrowController.extendDueDate(recordId, daysToAdd);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    void getMyActiveBorrows_shouldReturnList() {
        List<BorrowRecordDTO> expected = List.of(new BorrowRecordDTO(), new BorrowRecordDTO());
        when(userService.findIdByUsername(TEST_USERNAME)).thenReturn(TEST_USER_ID);
        when(borrowService.getActiveBorrowsByUser(TEST_USER_ID)).thenReturn(expected);

        ResponseEntity<List<BorrowRecordDTO>> response = borrowController.getMyActiveBorrows(userAuthentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void getMyHistory_shouldReturnList() {
        List<BorrowRecordDTO> expected = List.of(new BorrowRecordDTO());
        when(userService.findIdByUsername(TEST_USERNAME)).thenReturn(TEST_USER_ID);
        when(borrowService.getHistoryByUser(TEST_USER_ID)).thenReturn(expected);

        ResponseEntity<List<BorrowRecordDTO>> response = borrowController.getMyHistory(userAuthentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    void getMyOverdue_shouldReturnList() {
        List<BorrowRecordDTO> expected = List.of(new BorrowRecordDTO());
        when(userService.findIdByUsername(TEST_USERNAME)).thenReturn(TEST_USER_ID);
        when(borrowService.getOverdueByUser(TEST_USER_ID)).thenReturn(expected);

        ResponseEntity<List<BorrowRecordDTO>> response = borrowController.getMyOverdue(userAuthentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    // === ADMIN методы (не требуют Authentication) ===

    @Test
    void getAllActiveBorrows_asAdmin_shouldReturnList() {
        List<BorrowRecordDTO> expected = List.of(new BorrowRecordDTO(), new BorrowRecordDTO());
        when(borrowService.getAllActiveBorrows()).thenReturn(expected);

        ResponseEntity<List<BorrowRecordDTO>> response = borrowController.getAllActiveBorrows();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void getAllOverdue_asAdmin_shouldReturnList() {
        List<BorrowRecordDTO> expected = List.of(new BorrowRecordDTO());
        when(borrowService.getOverdueRecords()).thenReturn(expected);

        ResponseEntity<List<BorrowRecordDTO>> response = borrowController.getAllOverdue();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    void getUserHistory_asAdmin_shouldReturnList() {
        Long userId = 5L;
        List<BorrowRecordDTO> expected = List.of(new BorrowRecordDTO(), new BorrowRecordDTO());
        when(borrowService.getHistoryByUser(userId)).thenReturn(expected);

        ResponseEntity<List<BorrowRecordDTO>> response = borrowController.getUserHistory(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void forceReturn_asAdmin_shouldReturnOk() {
        Long recordId = 100L;
        BorrowRecordDTO expected = new BorrowRecordDTO();
        when(borrowService.returnBook(recordId)).thenReturn(expected);

        ResponseEntity<BorrowRecordDTO> response = borrowController.forceReturn(recordId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }
}