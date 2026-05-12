package com.agnezdei.library.mapper;

import com.agnezdei.library.model.BookEdition;
import com.agnezdei.library.dto.BookEditionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookEditionMapper {
    BookEditionDTO toDto(BookEdition entity);
    BookEdition toEntity(BookEditionDTO dto);
    void updateEntity(BookEditionDTO dto, @MappingTarget BookEdition entity);
    List<BookEditionDTO> toDtoList(List<BookEdition> entities);
    List<BookEdition> toEntityList(List<BookEditionDTO> dtos);
}