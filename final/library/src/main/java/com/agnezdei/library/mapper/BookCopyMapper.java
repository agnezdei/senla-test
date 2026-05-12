package com.agnezdei.library.mapper;

import com.agnezdei.library.dto.BookCopyDTO;
import com.agnezdei.library.model.BookCopy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {BookEditionMapper.class, CatalogMapper.class})
public interface BookCopyMapper {
    @Mapping(source = "edition.id", target = "editionId")
    @Mapping(source = "catalog.id", target = "catalogId")
    @Mapping(source = "status", target = "status")
    BookCopyDTO toDto(BookCopy entity);

    @Mapping(source = "editionId", target = "edition.id")
    @Mapping(source = "catalogId", target = "catalog.id")
    @Mapping(target = "borrowRecords", ignore = true)
    BookCopy toEntity(BookCopyDTO dto);

    void updateEntity(BookCopyDTO dto, @MappingTarget BookCopy entity);

    List<BookCopyDTO> toDtoList(List<BookCopy> entities);
}