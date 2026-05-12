package com.agnezdei.library.service;

import com.agnezdei.library.dto.CatalogDTO;
import com.agnezdei.library.exception.BusinessLogicException;
import com.agnezdei.library.exception.EntityNotFoundException;
import com.agnezdei.library.mapper.CatalogMapper;
import com.agnezdei.library.model.Catalog;
import com.agnezdei.library.repository.CatalogDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CatalogService {

    private static final Logger log = LoggerFactory.getLogger(CatalogService.class);

    private final CatalogDAO catalogDAO;
    private final CatalogMapper catalogMapper;

    public CatalogService(CatalogDAO catalogDAO, CatalogMapper catalogMapper) {
        this.catalogDAO = catalogDAO;
        this.catalogMapper = catalogMapper;
    }

    public CatalogDTO createCatalog(CatalogDTO dto) {
        log.info("Создание каталога: name={}, parentId={}", dto.getName(), dto.getParentId());

        Catalog parent = null;
        if (dto.getParentId() != null) {
            parent = catalogDAO.findById(dto.getParentId())
                    .orElseThrow(() -> {
                        log.error("Родительский каталог не найден: parentId={}", dto.getParentId());
                        return new EntityNotFoundException("Родительский каталог не найден с id: " + dto.getParentId());
                    });
        }

        Catalog catalog = catalogMapper.toEntity(dto);
        catalog.setParent(parent);
        catalogDAO.save(catalog);
        log.info("Каталог создан: id={}", catalog.getId());
        return catalogMapper.toDto(catalog);
    }

    public CatalogDTO updateCatalog(Long id, CatalogDTO dto) {
        log.info("Обновление каталога: id={}", id);

        Catalog catalog = catalogDAO.findById(id)
                .orElseThrow(() -> {
                    log.error("Каталог не найден: id={}", id);
                    return new EntityNotFoundException("Каталог не найден с id: " + id);
                });

        if (dto.getName() != null) {
            catalog.setName(dto.getName());
        }

        if (dto.getParentId() != null) {
            if (dto.getParentId().equals(catalog.getId())) {
                throw new BusinessLogicException("Нельзя сделать каталог родителем самого себя");
            }

            if (isDescendant(catalog, dto.getParentId())) {
                throw new BusinessLogicException("Нельзя переместить каталог в собственного потомка — это создаст цикл");
            }

            Catalog newParent = catalogDAO.findById(dto.getParentId())
                    .orElseThrow(() -> {
                        log.error("Новый родительский каталог не найден: parentId={}", dto.getParentId());
                        return new EntityNotFoundException("Новый родительский каталог не найден с id: " + dto.getParentId());
                    });
            catalog.setParent(newParent);
        } else {
            catalog.setParent(null);
        }

        catalogDAO.update(catalog);
        log.info("Каталог обновлён: id={}", id);
        return catalogMapper.toDto(catalog);
    }

    public void deleteCatalog(Long id) {
        log.info("Удаление каталога: id={}", id);

        Catalog catalog = catalogDAO.findById(id)
                .orElseThrow(() -> {
                    log.error("Каталог не найден для удаления: id={}", id);
                    return new EntityNotFoundException("Каталог не найден с id: " + id);
                });

        if (!catalog.getChildren().isEmpty()) {
            log.warn("Невозможно удалить каталог {}: у него есть подкаталоги", id);
            throw new BusinessLogicException("Невозможно удалить каталог, так как у него есть подкаталоги");
        }

        if (!catalog.getBookCopies().isEmpty()) {
            log.warn("Невозможно удалить каталог {}: в нём есть экземпляры книг", id);
            throw new BusinessLogicException("Невозможно удалить каталог, так как в нём есть экземпляры книг");
        }

        catalogDAO.delete(catalog);
        log.info("Каталог удалён: id={}", id);
    }

    @Transactional(readOnly = true)
    public CatalogDTO getCatalogById(Long id) {
        log.info("Получение каталога по id: {}", id);
        Catalog catalog = catalogDAO.findById(id)
                .orElseThrow(() -> {
                    log.error("Каталог не найден: id={}", id);
                    return new EntityNotFoundException("Каталог не найден с id: " + id);
                });
        return catalogMapper.toDto(catalog);
    }

    @Transactional(readOnly = true)
    public CatalogDTO getCatalogTree(Long rootId) {
        log.info("Построение дерева каталогов от rootId={}", rootId);
        Catalog root = catalogDAO.findById(rootId)
                .orElseThrow(() -> {
                    log.error("Корневой каталог не найден: id={}", rootId);
                    return new EntityNotFoundException("Каталог не найден с id: " + rootId);
                });
        return buildTree(root);
    }

    @Transactional(readOnly = true)
    public List<CatalogDTO> getRootCatalogs() {
        log.info("Получение корневых каталогов");
        return catalogDAO.findRootCatalogs().stream()
                .map(catalogMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CatalogDTO> getChildren(Long parentId) {
        log.info("Получение дочерних каталогов для parentId={}", parentId);
        return catalogDAO.findChildren(parentId).stream()
                .map(catalogMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CatalogDTO> getFullTree() {
        log.info("Получение полного дерева всех корневых каталогов");
        return catalogDAO.findRootCatalogs().stream()
                .map(this::buildTree)
                .collect(Collectors.toList());
    }

    // Вспомогательные методы

    private CatalogDTO buildTree(Catalog catalog) {
        CatalogDTO dto = catalogMapper.toDto(catalog);
        List<CatalogDTO> children = catalog.getChildren().stream()
                .map(this::buildTree)
                .collect(Collectors.toList());
        dto.setChildren(children);
        return dto;
    }

    private boolean isDescendant(Catalog catalog, Long possibleDescendantId) {
        Catalog node = catalogDAO.findById(possibleDescendantId).orElse(null);
        while (node != null && node.getParent() != null) {
            if (node.getParent().getId().equals(catalog.getId())) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }
}