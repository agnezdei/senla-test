package com.agnezdei.library.service;

import com.agnezdei.library.dto.BorrowRecordDTO;
import com.agnezdei.library.exception.BusinessLogicException;
import com.agnezdei.library.exception.EntityNotFoundException;
import com.agnezdei.library.exception.InvalidDateException;
import com.agnezdei.library.mapper.BorrowRecordMapper;
import com.agnezdei.library.model.BookCopy;
import com.agnezdei.library.model.BorrowRecord;
import com.agnezdei.library.model.User;
import com.agnezdei.library.repository.BookCopyDAO;
import com.agnezdei.library.repository.BorrowRecordDAO;
import com.agnezdei.library.repository.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowRecordServiceTest {

    @Mock private BorrowRecordDAO borrowRecordDAO;
    @Mock private BookCopyDAO bookCopyDAO;
    @Mock private UserDAO userDAO;
    @Mock private BorrowRecordMapper borrowRecordMapper;
    @InjectMocks private BorrowRecordService borrowRecordService;

    private User user;
    private BookCopy copy;
    private BorrowRecord record;
    private BorrowRecordDTO dto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        copy = new BookCopy();
        copy.setId(10L);
        copy.setStatus(BookCopy.Status.AVAILABLE);
        record = new BorrowRecord();
        record.setId(100L);
        record.setUser(user);
        record.setCopy(copy);
        record.setBorrowedAt(LocalDateTime.now());
        record.setDueDate(LocalDate.now().plusDays(14));
        record.setReturnedAt(null);
        dto = new BorrowRecordDTO();
        dto.setId(100L);
    }

    @Test
    void borrowBook_success_withDefaultDueDate() {
        when(userDAO.findById(1L)).thenReturn(Optional.of(user));
        when(bookCopyDAO.findById(10L)).thenReturn(Optional.of(copy));
        when(borrowRecordDAO.save(any(BorrowRecord.class))).thenAnswer(inv -> {
            BorrowRecord saved = inv.getArgument(0);
            saved.setId(100L);
            return saved;
        });
        when(borrowRecordMapper.toDto(any(BorrowRecord.class))).thenReturn(dto);

        BorrowRecordDTO result = borrowRecordService.borrowBook(1L, 10L, null);

        assertThat(result).isEqualTo(dto);
        assertThat(copy.getStatus()).isEqualTo(BookCopy.Status.BORROWED);
        verify(bookCopyDAO).update(copy);
        verify(borrowRecordDAO).save(any(BorrowRecord.class));
    }

    @Test
    void borrowBook_shouldThrowIfCopyNotAvailable() {
        copy.setStatus(BookCopy.Status.BORROWED);
        when(userDAO.findById(1L)).thenReturn(Optional.of(user));
        when(bookCopyDAO.findById(10L)).thenReturn(Optional.of(copy));

        assertThatThrownBy(() -> borrowRecordService.borrowBook(1L, 10L, null))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("невозможно выдать");
    }

    @Test
    void returnBook_success() {
        when(borrowRecordDAO.findById(100L)).thenReturn(Optional.of(record));
        when(borrowRecordMapper.toDto(any(BorrowRecord.class))).thenReturn(dto);

        BorrowRecordDTO result = borrowRecordService.returnBook(100L);

        assertThat(result).isEqualTo(dto);
        assertThat(record.getReturnedAt()).isNotNull();
        assertThat(copy.getStatus()).isEqualTo(BookCopy.Status.AVAILABLE);
        verify(bookCopyDAO).update(copy);
        verify(borrowRecordDAO).update(record);
    }

    @Test
    void returnBook_shouldThrowIfAlreadyReturned() {
        record.setReturnedAt(LocalDateTime.now());
        when(borrowRecordDAO.findById(100L)).thenReturn(Optional.of(record));

        assertThatThrownBy(() -> borrowRecordService.returnBook(100L))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("уже возвращена");
    }

    @Test
    void extendDueDate_success() {
        when(borrowRecordDAO.findById(100L)).thenReturn(Optional.of(record));
        when(borrowRecordMapper.toDto(any(BorrowRecord.class))).thenReturn(dto);

        LocalDate oldDue = record.getDueDate();
        BorrowRecordDTO result = borrowRecordService.extendDueDate(100L, 7);

        assertThat(result).isEqualTo(dto);
        assertThat(record.getDueDate()).isEqualTo(oldDue.plusDays(7));
        verify(borrowRecordDAO).update(record);
    }

    @Test
    void getOverdueRecords_shouldReturnList() {
        when(borrowRecordDAO.findOverdue()).thenReturn(List.of(record));
        when(borrowRecordMapper.toDto(any(BorrowRecord.class))).thenReturn(dto);

        List<BorrowRecordDTO> result = borrowRecordService.getOverdueRecords();
        assertThat(result).hasSize(1);
    }
}