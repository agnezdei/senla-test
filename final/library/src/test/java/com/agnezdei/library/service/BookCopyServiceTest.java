package com.agnezdei.library.service;

import com.agnezdei.library.dto.BookCopyDTO;
import com.agnezdei.library.exception.BusinessLogicException;
import com.agnezdei.library.mapper.BookCopyMapper;
import com.agnezdei.library.model.BookCopy;
import com.agnezdei.library.repository.BookCopyDAO;
import com.agnezdei.library.repository.BookEditionDAO;
import com.agnezdei.library.repository.CatalogDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookCopyServiceTest {

    @Mock private BookCopyDAO bookCopyDAO;
    @Mock private BookEditionDAO bookEditionDAO;
    @Mock private CatalogDAO catalogDAO;
    @Mock private BookCopyMapper bookCopyMapper;
    @InjectMocks private BookCopyService bookCopyService;

    @Test
    void createCopy_shouldThrowIfInventoryNumberExists() {
        BookCopyDTO dto = new BookCopyDTO();
        dto.setInventoryNumber("INV-001");
        when(bookCopyDAO.findByInventoryNumber("INV-001")).thenReturn(Optional.of(new BookCopy()));

        assertThatThrownBy(() -> bookCopyService.createCopy(dto))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("уже существует");
    }

    @Test
    void deleteCopy_shouldThrowIfBorrowed() {
        BookCopy copy = new BookCopy();
        copy.setStatus(BookCopy.Status.BORROWED);
        when(bookCopyDAO.findById(1L)).thenReturn(Optional.of(copy));

        assertThatThrownBy(() -> bookCopyService.deleteCopy(1L))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("в аренде");
    }
}