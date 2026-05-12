package com.agnezdei.library.controller;

import com.agnezdei.library.dto.BookEditionDTO;
import com.agnezdei.library.service.BookEditionService;
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
class BookEditionControllerTest {

    @Mock
    private BookEditionService editionService;

    @InjectMocks
    private BookEditionController editionController;

    @Test
    void searchByTitle_shouldReturnList() {
        String title = "Java";
        List<BookEditionDTO> list = List.of(new BookEditionDTO());
        when(editionService.searchByTitle(title)).thenReturn(list);

        ResponseEntity<List<BookEditionDTO>> response = editionController.searchByTitle(title);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
    }

    @Test
    void searchByAuthor_shouldReturnList() {
        String author = "Smith";
        List<BookEditionDTO> list = List.of(new BookEditionDTO());
        when(editionService.searchByAuthor(author)).thenReturn(list);

        ResponseEntity<List<BookEditionDTO>> response = editionController.searchByAuthor(author);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
    }

    @Test
    void getAllEditions_shouldReturnList() {
        List<BookEditionDTO> list = List.of(new BookEditionDTO());
        when(editionService.getAllEditions()).thenReturn(list);

        ResponseEntity<List<BookEditionDTO>> response = editionController.getAllEditions();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
    }

    @Test
    void getEdition_shouldReturnOk() {
        Long id = 1L;
        BookEditionDTO dto = new BookEditionDTO();
        when(editionService.getEditionById(id)).thenReturn(dto);

        ResponseEntity<BookEditionDTO> response = editionController.getEdition(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    void createEdition_shouldReturnCreated() {
        BookEditionDTO input = new BookEditionDTO();
        BookEditionDTO output = new BookEditionDTO();
        when(editionService.createEdition(any(BookEditionDTO.class))).thenReturn(output);

        ResponseEntity<BookEditionDTO> response = editionController.createEdition(input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(output);
    }

    @Test
    void updateEdition_shouldReturnOk() {
        Long id = 1L;
        BookEditionDTO input = new BookEditionDTO();
        BookEditionDTO output = new BookEditionDTO();
        when(editionService.updateEdition(eq(id), any(BookEditionDTO.class))).thenReturn(output);

        ResponseEntity<BookEditionDTO> response = editionController.updateEdition(id, input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(output);
    }

    @Test
    void deleteEdition_shouldReturnNoContent() {
        Long id = 1L;
        doNothing().when(editionService).deleteEdition(id);

        ResponseEntity<Void> response = editionController.deleteEdition(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(editionService).deleteEdition(id);
    }
}