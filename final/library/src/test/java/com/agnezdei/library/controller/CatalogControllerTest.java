package com.agnezdei.library.controller;

import com.agnezdei.library.dto.CatalogDTO;
import com.agnezdei.library.service.CatalogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogControllerTest {

    @Mock
    private CatalogService catalogService;

    @InjectMocks
    private CatalogController catalogController;

    @Test
    void getRootCatalogs_shouldReturnList() {
        List<CatalogDTO> list = List.of(new CatalogDTO());
        when(catalogService.getRootCatalogs()).thenReturn(list);

        ResponseEntity<List<CatalogDTO>> response = catalogController.getRootCatalogs();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
    }

    @Test
    void getChildren_shouldReturnList() {
        Long parentId = 1L;
        List<CatalogDTO> list = List.of(new CatalogDTO());
        when(catalogService.getChildren(parentId)).thenReturn(list);

        ResponseEntity<List<CatalogDTO>> response = catalogController.getChildren(parentId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
    }

    @Test
    void getFullTree_shouldReturnList() {
        List<CatalogDTO> list = List.of(new CatalogDTO());
        when(catalogService.getFullTree()).thenReturn(list);

        ResponseEntity<List<CatalogDTO>> response = catalogController.getFullTree();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
    }

    @Test
    void getCatalog_shouldReturnOk() {
        Long id = 1L;
        CatalogDTO dto = new CatalogDTO();
        when(catalogService.getCatalogTree(id)).thenReturn(dto);

        ResponseEntity<CatalogDTO> response = catalogController.getCatalog(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    void createCatalog_shouldReturnCreated() {
        CatalogDTO input = new CatalogDTO();
        CatalogDTO output = new CatalogDTO();
        when(catalogService.createCatalog(any(CatalogDTO.class))).thenReturn(output);

        ResponseEntity<CatalogDTO> response = catalogController.createCatalog(input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(output);
    }

    @Test
    void updateCatalog_shouldReturnOk() {
        Long id = 1L;
        CatalogDTO input = new CatalogDTO();
        CatalogDTO output = new CatalogDTO();
        when(catalogService.updateCatalog(eq(id), any(CatalogDTO.class))).thenReturn(output);

        ResponseEntity<CatalogDTO> response = catalogController.updateCatalog(id, input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(output);
    }

    @Test
    void deleteCatalog_shouldReturnNoContent() {
        Long id = 1L;
        doNothing().when(catalogService).deleteCatalog(id);

        ResponseEntity<Void> response = catalogController.deleteCatalog(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(catalogService).deleteCatalog(id);
    }
}