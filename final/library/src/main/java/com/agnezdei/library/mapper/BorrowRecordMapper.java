package com.agnezdei.library.mapper;

import com.agnezdei.library.dto.BorrowRecordDTO;
import com.agnezdei.library.model.BorrowRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {UserMapper.class, BookCopyMapper.class})
public interface BorrowRecordMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "copy.id", target = "copyId")
    BorrowRecordDTO toDto(BorrowRecord entity);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "copyId", target = "copy.id")
    @Mapping(target = "user", ignore = true) // чтобы не создавать лишние объекты
    @Mapping(target = "copy", ignore = true)
    BorrowRecord toEntity(BorrowRecordDTO dto);

    void updateEntity(BorrowRecordDTO dto, @MappingTarget BorrowRecord entity);

    List<BorrowRecordDTO> toDtoList(List<BorrowRecord> entities);
}