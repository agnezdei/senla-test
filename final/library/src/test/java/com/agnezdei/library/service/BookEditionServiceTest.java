package com.agnezdei.library.service;

import com.agnezdei.library.dto.BookEditionDTO;
import com.agnezdei.library.exception.BusinessLogicException;
import com.agnezdei.library.exception.EntityNotFoundException;
import com.agnezdei.library.mapper.BookEditionMapper;
import com.agnezdei.library.model.BookEdition;
import com.agnezdei.library.repository.BookEditionDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookEditionServiceTest {

    @Mock private BookEditionDAO bookEditionDAO;
    @Mock private BookEditionMapper bookEditionMapper;

    @InjectMocks private BookEditionService bookEditionService;

    private BookEdition edition;
    private BookEditionDTO editionDto;

    @BeforeEach
    void setUp() {
        edition = new BookEdition();
        edition.setId(1L);
        edition.setTitle("Test Book");
        edition.setIsbn("1234567890");
        edition.setAuthor("Author");

        editionDto = new BookEditionDTO();
        editionDto.setId(1L);
        editionDto.setTitle("Test Book");
        editionDto.setIsbn("1234567890");
        editionDto.setAuthor("Author");
    }

    @Test
    void createEdition_success() {
        BookEditionDTO input = new BookEditionDTO();
        input.setTitle("New Book");
        input.setIsbn("1111111111");

        when(bookEditionDAO.findByIsbn("1111111111")).thenReturn(Optional.empty());
        when(bookEditionMapper.toEntity(input)).thenReturn(edition);
        when(bookEditionDAO.save(any(BookEdition.class))).thenReturn(edition);
        when(bookEditionMapper.toDto(edition)).thenReturn(editionDto);

        BookEditionDTO result = bookEditionService.createEdition(input);

        assertThat(result).isEqualTo(editionDto);
        verify(bookEditionDAO).save(edition);
    }

    @Test
    void createEdition_isbnAlreadyExists_throwsException() {
        when(bookEditionDAO.findByIsbn("1234567890")).thenReturn(Optional.of(edition));

        assertThatThrownBy(() -> bookEditionService.createEdition(editionDto))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("ISBN");
    }

    @Test
    void updateEdition_success() {
        BookEditionDTO updateDto = new BookEditionDTO();
        updateDto.setTitle("Updated Title");
        updateDto.setIsbn("9999999999");

        when(bookEditionDAO.findById(1L)).thenReturn(Optional.of(edition));
        when(bookEditionDAO.findByIsbn("9999999999")).thenReturn(Optional.empty());
        doNothing().when(bookEditionMapper).updateEntity(updateDto, edition);
        when(bookEditionDAO.update(any(BookEdition.class))).thenReturn(edition);
        when(bookEditionMapper.toDto(edition)).thenReturn(updateDto);

        BookEditionDTO result = bookEditionService.updateEdition(1L, updateDto);

        assertThat(result.getTitle()).isEqualTo("Updated Title");
        verify(bookEditionDAO).update(edition);
    }

    @Test
    void updateEdition_isbnAlreadyUsedByOther_throwsException() {
        BookEdition otherEdition = new BookEdition();
        otherEdition.setId(2L);
        BookEditionDTO updateDto = new BookEditionDTO();
        updateDto.setIsbn("9999999999");

        when(bookEditionDAO.findById(1L)).thenReturn(Optional.of(edition));
        when(bookEditionDAO.findByIsbn("9999999999")).thenReturn(Optional.of(otherEdition));

        assertThatThrownBy(() -> bookEditionService.updateEdition(1L, updateDto))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("уже используется");
    }

    @Test
    void deleteEdition_success() {
        when(bookEditionDAO.findById(1L)).thenReturn(Optional.of(edition));
        edition.setCopies(List.of()); // no copies
        doNothing().when(bookEditionDAO).delete(edition);

        bookEditionService.deleteEdition(1L);

        verify(bookEditionDAO).delete(edition);
    }

    @Test
    void deleteEdition_hasCopies_throwsException() {
        when(bookEditionDAO.findById(1L)).thenReturn(Optional.of(edition));
        edition.setCopies(List.of(new com.agnezdei.library.model.BookCopy())); // non-empty

        assertThatThrownBy(() -> bookEditionService.deleteEdition(1L))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("связанные экземпляры");
    }

    @Test
    void searchByTitle_success() {
        when(bookEditionDAO.findByTitleContaining("Test")).thenReturn(List.of(edition));
        when(bookEditionMapper.toDto(edition)).thenReturn(editionDto);

        List<BookEditionDTO> result = bookEditionService.searchByTitle("Test");

        assertThat(result).hasSize(1).contains(editionDto);
    }

    @Test
    void searchByTitle_blankQuery_returnsEmptyList() {
        List<BookEditionDTO> result = bookEditionService.searchByTitle("");
        assertThat(result).isEmpty();
        verifyNoInteractions(bookEditionDAO);
    }

    @Test
    void searchByAuthor_success() {
        when(bookEditionDAO.findByAuthor("Author")).thenReturn(List.of(edition));
        when(bookEditionMapper.toDto(edition)).thenReturn(editionDto);

        List<BookEditionDTO> result = bookEditionService.searchByAuthor("Author");

        assertThat(result).hasSize(1);
    }
}