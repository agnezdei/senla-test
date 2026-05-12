package com.agnezdei.library.service;

import com.agnezdei.library.dto.BookCopyDTO;
import com.agnezdei.library.exception.BusinessLogicException;
import com.agnezdei.library.exception.EntityNotFoundException;
import com.agnezdei.library.mapper.BookCopyMapper;
import com.agnezdei.library.model.BookCopy;
import com.agnezdei.library.model.BookEdition;
import com.agnezdei.library.model.Catalog;
import com.agnezdei.library.repository.BookCopyDAO;
import com.agnezdei.library.repository.BookEditionDAO;
import com.agnezdei.library.repository.CatalogDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookCopyService {

    private static final Logger log = LoggerFactory.getLogger(BookCopyService.class);

    private final BookCopyDAO bookCopyDAO;
    private final BookEditionDAO bookEditionDAO;
    private final CatalogDAO catalogDAO;
    private final BookCopyMapper bookCopyMapper;

    public BookCopyService(BookCopyDAO bookCopyDAO,
                           BookEditionDAO bookEditionDAO,
                           CatalogDAO catalogDAO,
                           BookCopyMapper bookCopyMapper) {
        this.bookCopyDAO = bookCopyDAO;
        this.bookEditionDAO = bookEditionDAO;
        this.catalogDAO = catalogDAO;
        this.bookCopyMapper = bookCopyMapper;
    }

    public BookCopyDTO createCopy(BookCopyDTO dto) {
        log.info("Создание экземпляра книги: inventoryNumber={}", dto.getInventoryNumber());

        if (bookCopyDAO.findByInventoryNumber(dto.getInventoryNumber()).isPresent()) {
            log.warn("Попытка создать экземпляр с уже существующим инвентарным номером: {}", dto.getInventoryNumber());
            throw new BusinessLogicException("Экземпляр с инвентарным номером " + dto.getInventoryNumber() + " уже существует");
        }

        BookEdition edition = bookEditionDAO.findById(dto.getEditionId())
                .orElseThrow(() -> {
                    log.error("Издание не найдено: editionId={}", dto.getEditionId());
                    return new EntityNotFoundException("Издание не найдено с id: " + dto.getEditionId());
                });

        Catalog catalog = catalogDAO.findById(dto.getCatalogId())
                .orElseThrow(() -> {
                    log.error("Каталог не найден: catalogId={}", dto.getCatalogId());
                    return new EntityNotFoundException("Каталог не найден с id: " + dto.getCatalogId());
                });

        BookCopy copy = bookCopyMapper.toEntity(dto);
        copy.setEdition(edition);
        copy.setCatalog(catalog);
        copy.setStatus(BookCopy.Status.AVAILABLE);
        bookCopyDAO.save(copy);
        log.info("Экземпляр книги создан: id={}, inventoryNumber={}", copy.getId(), copy.getInventoryNumber());
        return bookCopyMapper.toDto(copy);
    }

    public BookCopyDTO updateCopy(Long id, BookCopyDTO dto) {
        log.info("Обновление экземпляра книги: id={}", id);

        BookCopy copy = bookCopyDAO.findById(id)
                .orElseThrow(() -> {
                    log.error("Экземпляр не найден: id={}", id);
                    return new EntityNotFoundException("Экземпляр книги не найден с id: " + id);
                });

        if (dto.getInventoryNumber() != null && !dto.getInventoryNumber().equals(copy.getInventoryNumber())) {
            if (bookCopyDAO.findByInventoryNumber(dto.getInventoryNumber()).isPresent()) {
                log.warn("Попытка обновить экземпляр {} с уже существующим инвентарным номером: {}", id, dto.getInventoryNumber());
                throw new BusinessLogicException("Инвентарный номер " + dto.getInventoryNumber() + " уже используется");
            }
            copy.setInventoryNumber(dto.getInventoryNumber());
        }

        if (dto.getEditionId() != null && !dto.getEditionId().equals(copy.getEdition().getId())) {
            BookEdition edition = bookEditionDAO.findById(dto.getEditionId())
                    .orElseThrow(() -> {
                        log.error("Новое издание не найдено: editionId={}", dto.getEditionId());
                        return new EntityNotFoundException("Издание не найдено с id: " + dto.getEditionId());
                    });
            copy.setEdition(edition);
        }

        if (dto.getCatalogId() != null && !dto.getCatalogId().equals(copy.getCatalog().getId())) {
            Catalog catalog = catalogDAO.findById(dto.getCatalogId())
                    .orElseThrow(() -> {
                        log.error("Новый каталог не найден: catalogId={}", dto.getCatalogId());
                        return new EntityNotFoundException("Каталог не найден с id: " + dto.getCatalogId());
                    });
            copy.setCatalog(catalog);
        }

        bookCopyDAO.update(copy);
        log.info("Экземпляр книги обновлён: id={}", id);
        return bookCopyMapper.toDto(copy);
    }

    public void deleteCopy(Long id) {
        log.info("Удаление экземпляра книги: id={}", id);

        BookCopy copy = bookCopyDAO.findById(id)
                .orElseThrow(() -> {
                    log.error("Экземпляр не найден для удаления: id={}", id);
                    return new EntityNotFoundException("Экземпляр книги не найден с id: " + id);
                });

        if (copy.getStatus() == BookCopy.Status.BORROWED) {
            log.warn("Невозможно удалить экземпляр {}: он выдан", id);
            throw new BusinessLogicException("Невозможно удалить экземпляр книги, который находится в аренде");
        }

        bookCopyDAO.delete(copy);
        log.info("Экземпляр книги удалён: id={}", id);
    }

    @Transactional(readOnly = true)
    public BookCopyDTO getCopyById(Long id) {
        log.info("Получение экземпляра по id: {}", id);
        BookCopy copy = bookCopyDAO.findById(id)
                .orElseThrow(() -> {
                    log.error("Экземпляр не найден: id={}", id);
                    return new EntityNotFoundException("Экземпляр книги не найден с id: " + id);
                });
        return bookCopyMapper.toDto(copy);
    }

    @Transactional(readOnly = true)
    public List<BookCopyDTO> getCopiesByCatalog(Long catalogId) {
        log.info("Получение экземпляров по каталогу: catalogId={}", catalogId);
        return bookCopyDAO.findByCatalogId(catalogId).stream()
                .map(bookCopyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookCopyDTO> getCopiesByStatus(BookCopy.Status status) {
        log.info("Получение экземпляров по статусу: {}", status);
        return bookCopyDAO.findByStatus(status).stream()
                .map(bookCopyMapper::toDto)
                .collect(Collectors.toList());
    }
}