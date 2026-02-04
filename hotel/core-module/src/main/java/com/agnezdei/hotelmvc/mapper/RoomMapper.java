package com.agnezdei.hotelmvc.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.agnezdei.hotelmvc.dto.RoomDTO;
import com.agnezdei.hotelmvc.model.Room;

public class RoomMapper {
    
    public static RoomDTO toDTO(Room room) {
        if (room == null) return null;
        
        String type = room.getType() != null ? room.getType().name() : "";
        String status = room.getStatus() != null ? room.getStatus().name() : "";
        
        return new RoomDTO(
            room.getId(),
            room.getNumber(),
            type,
            room.getPrice(),
            status,
            room.getCapacity(),
            room.getStars()
        );
    }
    
    public static Room toEntity(RoomDTO dto) {
        if (dto == null) return null;
        
        Room room = new Room();
        room.setId(dto.getId());
        room.setNumber(dto.getNumber());
        room.setPrice(dto.getPrice());
        room.setCapacity(dto.getCapacity());
        room.setStars(dto.getStars());
        
        if (dto.getType() != null && !dto.getType().isEmpty()) {
            try {
                room.setType(com.agnezdei.hotelmvc.model.RoomType.valueOf(dto.getType()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Неизвестный тип номера: " + dto.getType());
            }
        }
        
        if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
            try {
                room.setStatus(com.agnezdei.hotelmvc.model.RoomStatus.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Неизвестный статус номера: " + dto.getStatus());
            }
        }
        
        return room;
    }
    
    public static List<RoomDTO> toDTOList(List<Room> rooms) {
        return rooms.stream()
                   .map(RoomMapper::toDTO)
                   .collect(Collectors.toList());
    }
}