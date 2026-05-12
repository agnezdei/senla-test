package com.agnezdei.library.controller;

import com.agnezdei.library.dto.BookCopyDTO;
import com.agnezdei.library.model.BookCopy;
import com.agnezdei.library.service.BookCopyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookCopyControllerTest {

    @Mock
    private BookCopyService copyService;

    @InjectMocks
    private BookCopyController copyController;

    @Test
    void getCopy_shouldReturnOk() {
        Long id = 1L;
        BookCopyDTO dto = new BookCopyDTO();
        when(copyService.getCopyById(id)).thenReturn(dto);

        ResponseEntity<BookCopyDTO> response = copyController.getCopy(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    void getCopiesByStatus_shouldReturnList() {
        BookCopy.Status status = BookCopy.Status.AVAILABLE;
        List<BookCopyDTO> list = List.of(new BookCopyDTO());
        when(copyService.getCopiesByStatus(status)).thenReturn(list);

        ResponseEntity<List<BookCopyDTO>> response = copyController.getCopiesByStatus(status);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getCopiesByCatalog_shouldReturnList() {
        Long catalogId = 5L;
        List<BookCopyDTO> list = List.of(new BookCopyDTO());
        when(copyService.getCopiesByCatalog(catalogId)).thenReturn(list);

        ResponseEntity<List<BookCopyDTO>> response = copyController.getCopiesByCatalog(catalogId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
    }

    @Test
    void createCopy_shouldReturnCreated() {
        BookCopyDTO input = new BookCopyDTO();
        BookCopyDTO output = new BookCopyDTO();
        when(copyService.createCopy(any(BookCopyDTO.class))).thenReturn(output);

        ResponseEntity<BookCopyDTO> response = copyController.createCopy(input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(output);
    }

    @Test
    void updateCopy_shouldReturnOk() {
        Long id = 1L;
        BookCopyDTO input = new BookCopyDTO();
        BookCopyDTO output = new BookCopyDTO();
        when(copyService.updateCopy(eq(id), any(BookCopyDTO.class))).thenReturn(output);

        ResponseEntity<BookCopyDTO> response = copyController.updateCopy(id, input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(output);
    }

    @Test
    void deleteCopy_shouldReturnNoContent() {
        Long id = 1L;
        doNothing().when(copyService).deleteCopy(id);

        ResponseEntity<Void> response = copyController.deleteCopy(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(copyService).deleteCopy(id);
    }
}