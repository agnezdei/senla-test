package com.agnezdei.library.mapper;

import com.agnezdei.library.dto.UserRegistrationDTO;
import com.agnezdei.library.dto.UserResponseDTO;
import com.agnezdei.library.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    // User -> UserResponseDTO
    @Mapping(source = "role", target = "role")
    UserResponseDTO toResponseDto(User entity);

    // UserRegistrationDTO -> User
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "borrowRecords", ignore = true)
    User toEntity(UserRegistrationDTO dto);

    List<UserResponseDTO> toResponseDtoList(List<User> entities);
}