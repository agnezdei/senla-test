package com.agnezdei.library.service;

import com.agnezdei.library.dto.CatalogDTO;
import com.agnezdei.library.exception.BusinessLogicException;
import com.agnezdei.library.exception.EntityNotFoundException;
import com.agnezdei.library.mapper.CatalogMapper;
import com.agnezdei.library.model.Catalog;
import com.agnezdei.library.repository.CatalogDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock private CatalogDAO catalogDAO;
    @Mock private CatalogMapper catalogMapper;
    @InjectMocks private CatalogService catalogService;

    private Catalog parent;
    private Catalog child;
    private CatalogDTO parentDto;
    private CatalogDTO childDto;

    @BeforeEach
    void setUp() {
        parent = new Catalog();
        parent.setId(1L);
        parent.setName("Parent");
        parent.setChildren(Collections.emptyList());

        child = new Catalog();
        child.setId(2L);
        child.setName("Child");
        child.setParent(parent);
        child.setChildren(Collections.emptyList());

        parentDto = new CatalogDTO();
        parentDto.setId(1L);
        parentDto.setName("Parent");

        childDto = new CatalogDTO();
        childDto.setId(2L);
        childDto.setName("Child");
        childDto.setParentId(1L);
    }

    @Test
    void createCatalog_withParent_success() {
        when(catalogDAO.findById(1L)).thenReturn(Optional.of(parent));
        when(catalogMapper.toEntity(childDto)).thenReturn(child);
        when(catalogDAO.save(any(Catalog.class))).thenReturn(child);
        when(catalogMapper.toDto(child)).thenReturn(childDto);

        CatalogDTO result = catalogService.createCatalog(childDto);

        assertThat(result).isEqualTo(childDto);
        ArgumentCaptor<Catalog> captor = ArgumentCaptor.forClass(Catalog.class);
        verify(catalogDAO).save(captor.capture());
        Catalog saved = captor.getValue();
        assertThat(saved.getParent()).isEqualTo(parent);
    }

    @Test
    void updateCatalog_moveToDescendant_shouldThrowCyclic() {
        // Создаём цепочку: parent (1) -> child (2)
        parent.setChildren(List.of(child));
        child.setParent(parent);

        // Пытаемся переместить parent под child (2)
        parentDto.setParentId(2L);

        when(catalogDAO.findById(1L)).thenReturn(Optional.of(parent));
        when(catalogDAO.findById(2L)).thenReturn(Optional.of(child));

        assertThatThrownBy(() -> catalogService.updateCatalog(1L, parentDto))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("цикл");
    }

    @Test
    void deleteCatalog_shouldThrowIfHasChildren() {
        parent.setChildren(List.of(child));
        when(catalogDAO.findById(1L)).thenReturn(Optional.of(parent));

        assertThatThrownBy(() -> catalogService.deleteCatalog(1L))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("подкаталоги");
    }
}