package com.agnezdei.library.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public class BorrowRecordDTO {
    private Long id;
    @NotNull(message = "ID пользователя не может быть null")
    private Long userId;
    @NotNull(message = "ID экземпляра книги не может быть null")
    private Long copyId;
    private LocalDateTime borrowedAt;
    private LocalDate dueDate;
    private LocalDateTime returnedAt;

    public BorrowRecordDTO() {}

    public BorrowRecordDTO(Long id, Long userId, Long copyId, LocalDateTime borrowedAt, LocalDate dueDate, LocalDateTime returnedAt) {
        this.id = id;
        this.userId = userId;
        this.copyId = copyId;
        this.borrowedAt = borrowedAt;
        this.dueDate = dueDate;
        this.returnedAt = returnedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCopyId() { return copyId; }
    public void setCopyId(Long copyId) { this.copyId = copyId; }

    public LocalDateTime getBorrowedAt() { return borrowedAt; }
    public void setBorrowedAt(LocalDateTime borrowedAt) { this.borrowedAt = borrowedAt; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getReturnedAt() { return returnedAt; }
    public void setReturnedAt(LocalDateTime returnedAt) { this.returnedAt = returnedAt; }
}