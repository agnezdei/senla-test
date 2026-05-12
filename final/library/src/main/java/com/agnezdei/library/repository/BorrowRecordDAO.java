package com.agnezdei.library.repository;

import com.agnezdei.library.model.BorrowRecord;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public class BorrowRecordDAO extends AbstractDAO<BorrowRecord, Long> {

    public BorrowRecordDAO() {
        super(BorrowRecord.class);
    }

    public List<BorrowRecord> findByUser(Long userId) {
        return em.createQuery("SELECT br FROM BorrowRecord br WHERE br.user.id = :userId ORDER BY br.borrowedAt DESC", BorrowRecord.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<BorrowRecord> findByCopy(Long copyId) {
        return em.createQuery("SELECT br FROM BorrowRecord br WHERE br.copy.id = :copyId ORDER BY br.borrowedAt DESC", BorrowRecord.class)
                .setParameter("copyId", copyId)
                .getResultList();
    }

    public List<BorrowRecord> findActiveByUser(Long userId) {
        return em.createQuery("SELECT br FROM BorrowRecord br WHERE br.user.id = :userId AND br.returnedAt IS NULL", BorrowRecord.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<BorrowRecord> findOverdue() {
        LocalDate today = LocalDate.now();
        return em.createQuery("SELECT br FROM BorrowRecord br WHERE br.returnedAt IS NULL AND br.dueDate < :today", BorrowRecord.class)
                .setParameter("today", today)
                .getResultList();
    }

    public List<BorrowRecord> findAllActive() {
        return em.createQuery("SELECT br FROM BorrowRecord br WHERE br.returnedAt IS NULL", BorrowRecord.class)
                .getResultList();
    }

    public List<BorrowRecord> findActiveByCopy(Long copyId) {
        return em.createQuery("SELECT br FROM BorrowRecord br WHERE br.copy.id = :copyId AND br.returnedAt IS NULL", BorrowRecord.class)
                .setParameter("copyId", copyId)
                .getResultList();
    }
}