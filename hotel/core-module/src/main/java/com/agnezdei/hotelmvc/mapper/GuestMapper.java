package com.agnezdei.hotelmvc.mapper;

import com.agnezdei.hotelmvc.dto.GuestDTO;
import com.agnezdei.hotelmvc.model.Guest;
import java.util.List;
import java.util.stream.Collectors;

public class GuestMapper {
    
    public static GuestDTO toDTO(Guest guest) {
        if (guest == null) return null;
        
        return new GuestDTO(
            guest.getId(),
            guest.getName(),
            guest.getPassportNumber()
        );
    }
    
    public static Guest toEntity(GuestDTO dto) {
        if (dto == null) return null;
        
        Guest guest = new Guest();
        guest.setId(dto.getId());
        guest.setName(dto.getName());
        guest.setPassportNumber(dto.getPassportNumber());
        return guest;
    }
    
    public static List<GuestDTO> toDTOList(List<Guest> guests) {
        return guests.stream()
                    .map(GuestMapper::toDTO)
                    .collect(Collectors.toList());
    }
}