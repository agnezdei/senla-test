package com.agnezdei.library.mapper;

import com.agnezdei.library.dto.CatalogDTO;
import com.agnezdei.library.model.Catalog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CatalogMapper {
    @Mapping(source = "parent.id", target = "parentId")
    CatalogDTO toDto(Catalog entity);

    @Mapping(source = "parentId", target = "parent.id")
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "bookCopies", ignore = true)
    Catalog toEntity(CatalogDTO dto);

    void updateEntity(CatalogDTO dto, @MappingTarget Catalog entity);

    List<CatalogDTO> toDtoList(List<Catalog> entities);
}