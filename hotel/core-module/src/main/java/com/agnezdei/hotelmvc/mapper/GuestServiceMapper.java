package com.agnezdei.hotelmvc.mapper;

import com.agnezdei.hotelmvc.dto.GuestServiceDTO;
import com.agnezdei.hotelmvc.model.GuestService;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class GuestServiceMapper {
    
    public static GuestServiceDTO toDTO(GuestService guestService) {
        if (guestService == null) return null;
        
        String guestName = "";
        String serviceName = "";
        String serviceCategory = "";
        
        if (guestService.getGuest() != null) {
            guestName = guestService.getGuest().getName();
        }
        
        if (guestService.getService() != null) {
            serviceName = guestService.getService().getName();
            if (guestService.getService().getCategory() != null) {
                serviceCategory = guestService.getService().getCategory().name();
            }
        }
        
        String serviceDateStr = guestService.getServiceDate() != null ? 
                               guestService.getServiceDate().toString() : "";
        
        return new GuestServiceDTO(
            guestService.getId(),
            guestName,
            serviceName,
            serviceCategory,
            serviceDateStr
        );
    }
    
    public static GuestService toEntity(GuestServiceDTO dto) {
        if (dto == null) return null;
        
        GuestService guestService = new GuestService();
        guestService.setId(dto.getId());
        
        if (dto.getServiceDate() != null && !dto.getServiceDate().isEmpty()) {
            guestService.setServiceDate(LocalDate.parse(dto.getServiceDate()));
        }
        
        return guestService;
    }
    
    public static List<GuestServiceDTO> toDTOList(List<GuestService> guestServices) {
        return guestServices.stream()
                           .map(GuestServiceMapper::toDTO)
                           .collect(Collectors.toList());
    }
}