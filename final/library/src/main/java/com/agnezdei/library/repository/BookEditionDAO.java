package com.agnezdei.library.repository;

import com.agnezdei.library.model.BookEdition;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class BookEditionDAO extends AbstractDAO<BookEdition, Long> {

    public BookEditionDAO() {
        super(BookEdition.class);
    }

    public Optional<BookEdition> findByIsbn(String isbn) {
        return em.createQuery("SELECT be FROM BookEdition be WHERE be.isbn = :isbn", BookEdition.class)
                .setParameter("isbn", isbn)
                .getResultStream()
                .findFirst();
    }

    public List<BookEdition> findByTitleContaining(String title) {
        return em.createQuery("SELECT be FROM BookEdition be WHERE LOWER(be.title) LIKE LOWER(:title)", BookEdition.class)
                .setParameter("title", "%" + title + "%")
                .getResultList();
    }

    public List<BookEdition> findByAuthor(String author) {
        return em.createQuery("SELECT be FROM BookEdition be WHERE LOWER(be.author) LIKE LOWER(:author)", BookEdition.class)
                .setParameter("author", "%" + author + "%")
                .getResultList();
    }
}