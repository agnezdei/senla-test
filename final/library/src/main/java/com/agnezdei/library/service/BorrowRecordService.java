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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BorrowRecordService {

    private static final Logger log = LoggerFactory.getLogger(BorrowRecordService.class);

    private final BorrowRecordDAO borrowRecordDAO;
    private final BookCopyDAO bookCopyDAO;
    private final UserDAO userDAO;
    private final BorrowRecordMapper borrowRecordMapper;

    public BorrowRecordService(BorrowRecordDAO borrowRecordDAO,
                               BookCopyDAO bookCopyDAO,
                               UserDAO userDAO,
                               BorrowRecordMapper borrowRecordMapper) {
        this.borrowRecordDAO = borrowRecordDAO;
        this.bookCopyDAO = bookCopyDAO;
        this.userDAO = userDAO;
        this.borrowRecordMapper = borrowRecordMapper;
    }

    public BorrowRecordDTO borrowBook(Long userId, Long copyId, LocalDate dueDate) {
        log.info("Попытка выдачи: userId={}, copyId={}, dueDate={}", userId, copyId, dueDate);

        if (dueDate == null) {
            dueDate = LocalDate.now().plusDays(14);
        }
        if (dueDate.isBefore(LocalDate.now())) {
            throw new InvalidDateException("Дата возврата не может быть в прошлом");
        }

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден с id: " + userId));
        BookCopy copy = bookCopyDAO.findById(copyId)
                .orElseThrow(() -> new EntityNotFoundException("Экземпляр книги не найден с id: " + copyId));

        if (copy.getStatus() != BookCopy.Status.AVAILABLE) {
            throw new BusinessLogicException("Статус экземпляра книги: " + copy.getStatus() + ", невозможно выдать");
        }

        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setCopy(copy);
        record.setBorrowedAt(LocalDateTime.now());
        record.setDueDate(dueDate);
        record.setReturnedAt(null);

        borrowRecordDAO.save(record);
        copy.setStatus(BookCopy.Status.BORROWED);
        bookCopyDAO.update(copy);

        log.info("Книга успешно выдана: recordId={}", record.getId());
        return borrowRecordMapper.toDto(record);
    }

    public BorrowRecordDTO returnBook(Long recordId) {
        log.info("Попытка возврата: recordId={}", recordId);
        BorrowRecord record = borrowRecordDAO.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("Запись о выдаче не найдена: " + recordId));
        if (record.getReturnedAt() != null) {
            throw new BusinessLogicException("Книга уже возвращена");
        }
        record.setReturnedAt(LocalDateTime.now());
        borrowRecordDAO.update(record);

        BookCopy copy = record.getCopy();
        copy.setStatus(BookCopy.Status.AVAILABLE);
        bookCopyDAO.update(copy);

        log.info("Книга успешно возвращена: recordId={}", recordId);
        return borrowRecordMapper.toDto(record);
    }

    public BorrowRecordDTO returnBookByCopy(Long copyId) {
        log.info("Возврат по экземпляру: copyId={}", copyId);
        List<BorrowRecord> activeRecords = borrowRecordDAO.findActiveByCopy(copyId);
        if (activeRecords.isEmpty()) {
            throw new EntityNotFoundException("Нет активной выдачи для экземпляра с id: " + copyId);
        }
        return returnBook(activeRecords.get(0).getId());
    }

    public BorrowRecordDTO extendDueDate(Long recordId, int daysToAdd) {
        log.info("Продление срока возврата: recordId={}, daysToAdd={}", recordId, daysToAdd);
        if (daysToAdd <= 0) {
            throw new InvalidDateException("Количество добавляемых дней должно быть положительным");
        }
        BorrowRecord record = borrowRecordDAO.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("Запись о выдаче не найдена: " + recordId));
        if (record.getReturnedAt() != null) {
            throw new BusinessLogicException("Нельзя продлить срок возврата для уже возвращённой книги");
        }
        LocalDate newDueDate = record.getDueDate().plusDays(daysToAdd);
        record.setDueDate(newDueDate);
        borrowRecordDAO.update(record);
        log.info("Срок возврата продлён для recordId={}, newDueDate={}", recordId, newDueDate);
        return borrowRecordMapper.toDto(record);
    }

    @Transactional(readOnly = true)
    public List<BorrowRecordDTO> getActiveBorrowsByUser(Long userId) {
        log.info("Получение активных выдач для userId={}", userId);
        return borrowRecordDAO.findActiveByUser(userId).stream()
                .map(borrowRecordMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BorrowRecordDTO> getOverdueRecords() {
        log.info("Получение просроченных записей");
        return borrowRecordDAO.findOverdue().stream()
                .map(borrowRecordMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BorrowRecordDTO> getHistoryByUser(Long userId) {
        log.info("Получение истории выдач для userId={}", userId);
        return borrowRecordDAO.findByUser(userId).stream()
                .map(borrowRecordMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BorrowRecordDTO> getHistoryByCopy(Long copyId) {
        log.info("Получение истории выдач для copyId={}", copyId);
        return borrowRecordDAO.findByCopy(copyId).stream()
                .map(borrowRecordMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BorrowRecordDTO> getAllActiveBorrows() {
        log.info("Получение всех активных выдач");
        return borrowRecordDAO.findAllActive().stream()
                .map(borrowRecordMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BorrowRecordDTO> getOverdueByUser(Long userId) {
        log.info("Получение просроченных выдач для userId={}", userId);
        return borrowRecordDAO.findActiveByUser(userId).stream()
                .filter(record -> record.getDueDate().isBefore(LocalDate.now()))
                .map(borrowRecordMapper::toDto)
                .collect(Collectors.toList());
    }
}