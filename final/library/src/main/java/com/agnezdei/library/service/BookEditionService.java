package com.agnezdei.library.service;

import com.agnezdei.library.dto.BookEditionDTO;
import com.agnezdei.library.exception.BusinessLogicException;
import com.agnezdei.library.exception.EntityNotFoundException;
import com.agnezdei.library.mapper.BookEditionMapper;
import com.agnezdei.library.model.BookEdition;
import com.agnezdei.library.repository.BookEditionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookEditionService {

    private static final Logger log = LoggerFactory.getLogger(BookEditionService.class);

    private final BookEditionDAO bookEditionDAO;
    private final BookEditionMapper bookEditionMapper;

    public BookEditionService(BookEditionDAO bookEditionDAO, BookEditionMapper bookEditionMapper) {
        this.bookEditionDAO = bookEditionDAO;
        this.bookEditionMapper = bookEditionMapper;
    }

    public BookEditionDTO createEdition(BookEditionDTO dto) {
        log.info("Создание книжного издания: title={}, isbn={}", dto.getTitle(), dto.getIsbn());

        if (dto.getIsbn() != null && bookEditionDAO.findByIsbn(dto.getIsbn()).isPresent()) {
            log.warn("Попытка создать издание с уже существующим ISBN: {}", dto.getIsbn());
            throw new BusinessLogicException("Издание с ISBN " + dto.getIsbn() + " уже существует");
        }

        BookEdition edition = bookEditionMapper.toEntity(dto);
        bookEditionDAO.save(edition);
        log.info("Книжное издание создано: id={}", edition.getId());
        return bookEditionMapper.toDto(edition);
    }

    public BookEditionDTO updateEdition(Long id, BookEditionDTO dto) {
        log.info("Обновление книжного издания: id={}", id);

        BookEdition edition = bookEditionDAO.findById(id)
                .orElseThrow(() -> {
                    log.error("Издание не найдено: id={}", id);
                    return new EntityNotFoundException("Книжное издание не найдено с id: " + id);
                });

        if (dto.getIsbn() != null && !dto.getIsbn().equals(edition.getIsbn())) {
            if (bookEditionDAO.findByIsbn(dto.getIsbn()).isPresent()) {
                log.warn("Попытка обновить издание {} с уже существующим ISBN: {}", id, dto.getIsbn());
                throw new BusinessLogicException("ISBN " + dto.getIsbn() + " уже используется другим изданием");
            }
        }

        bookEditionMapper.updateEntity(dto, edition);

        bookEditionDAO.update(edition);
        log.info("Книжное издание обновлено: id={}", id);
        return bookEditionMapper.toDto(edition);
    }

    public void deleteEdition(Long id) {
        log.info("Удаление книжного издания: id={}", id);

        BookEdition edition = bookEditionDAO.findById(id)
                .orElseThrow(() -> {
                    log.error("Издание не найдено для удаления: id={}", id);
                    return new EntityNotFoundException("Книжное издание не найдено с id: " + id);
                });

        if (edition.getCopies() != null && !edition.getCopies().isEmpty()) {
            log.warn("Невозможно удалить издание {}: у него есть экземпляры книг", id);
            throw new BusinessLogicException("Невозможно удалить издание, так как у него есть связанные экземпляры книг");
        }

        bookEditionDAO.delete(edition);
        log.info("Книжное издание удалено: id={}", id);
    }

    @Transactional(readOnly = true)
    public BookEditionDTO getEditionById(Long id) {
        log.info("Получение издания по id: {}", id);
        BookEdition edition = bookEditionDAO.findById(id)
                .orElseThrow(() -> {
                    log.error("Издание не найдено: id={}", id);
                    return new EntityNotFoundException("Книжное издание не найдено с id: " + id);
                });
        return bookEditionMapper.toDto(edition);
    }

    @Transactional(readOnly = true)
    public List<BookEditionDTO> getAllEditions() {
        log.info("Получение всех изданий");
        return bookEditionDAO.findAll().stream()
                .map(bookEditionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookEditionDTO> searchByTitle(String title) {
        log.info("Поиск изданий по названию: {}", title);
        if (title == null || title.isBlank()) {
            log.warn("Поиск по названию: передан пустой запрос");
            return List.of();
        }
        return bookEditionDAO.findByTitleContaining(title).stream()
                .map(bookEditionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookEditionDTO> searchByAuthor(String author) {
        log.info("Поиск изданий по автору: {}", author);
        if (author == null || author.isBlank()) {
            log.warn("Поиск по автору: передан пустой запрос");
            return List.of();
        }
        return bookEditionDAO.findByAuthor(author).stream()
                .map(bookEditionMapper::toDto)
                .collect(Collectors.toList());
    }
}
