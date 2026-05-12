package com.agnezdei.library.repository;

import com.agnezdei.library.model.BookCopy;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class BookCopyDAO extends AbstractDAO<BookCopy, Long> {

    public BookCopyDAO() {
        super(BookCopy.class);
    }

    public List<BookCopy> findByCatalogId(Long catalogId) {
        return em.createQuery("SELECT bc FROM BookCopy bc WHERE bc.catalog.id = :catalogId", BookCopy.class)
                .setParameter("catalogId", catalogId)
                .getResultList();
    }

    public List<BookCopy> findByStatus(BookCopy.Status status) {
        return em.createQuery("SELECT bc FROM BookCopy bc WHERE bc.status = :status", BookCopy.class)
                .setParameter("status", status)
                .getResultList();
    }

    public Optional<BookCopy> findByInventoryNumber(String inventoryNumber) {
        return em.createQuery("SELECT bc FROM BookCopy bc WHERE bc.inventoryNumber = :inv", BookCopy.class)
                .setParameter("inv", inventoryNumber)
                .getResultStream()
                .findFirst();
    }
}