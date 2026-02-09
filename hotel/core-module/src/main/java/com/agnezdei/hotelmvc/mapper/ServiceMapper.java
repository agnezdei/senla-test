package com.agnezdei.hotelmvc.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.agnezdei.hotelmvc.dto.ServiceDTO;
import com.agnezdei.hotelmvc.model.Service;

public class ServiceMapper {
    
    public static ServiceDTO toDTO(Service service) {
        if (service == null) return null;
        
        String category = service.getCategory() != null ? service.getCategory().name() : "";
        
        return new ServiceDTO(
            service.getId(),
            service.getName(),
            service.getPrice(),
            category
        );
    }
    
    public static Service toEntity(ServiceDTO dto) {
        if (dto == null) return null;
        
        Service service = new Service();
        service.setId(dto.getId());
        service.setName(dto.getName());
        service.setPrice(dto.getPrice());
        
        if (dto.getCategory() != null && !dto.getCategory().isEmpty()) {
            try {
                service.setCategory(com.agnezdei.hotelmvc.model.ServiceCategory.valueOf(dto.getCategory()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Неизвестная категория услуги: " + dto.getCategory());
            }
        }
        
        return service;
    }
    
    public static List<ServiceDTO> toDTOList(List<Service> services) {
        return services.stream()
                      .map(ServiceMapper::toDTO)
                      .collect(Collectors.toList());
    }
}