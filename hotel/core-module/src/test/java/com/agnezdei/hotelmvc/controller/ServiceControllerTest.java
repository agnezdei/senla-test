package com.agnezdei.hotelmvc.controller;

import com.agnezdei.hotelmvc.dto.ServiceDTO;
import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import com.agnezdei.hotelmvc.service.ServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceControllerTest {

    @Mock
    private ServiceService serviceService;

    @InjectMocks
    private ServiceController serviceController;

    private Service testService;

    @BeforeEach
    void setUp() {
        testService = new Service("WiFi", 10.0, ServiceCategory.FOOD);
        testService.setId(1L);
    }

    @Test
    void addService_success() throws BusinessLogicException {
        String expectedResult = "Успех: Услуга добавлена";
        when(serviceService.addService("WiFi", 10.0, ServiceCategory.FOOD))
                .thenReturn(expectedResult);

        ResponseEntity<String> response = serviceController.addService("WiFi", 10.0, ServiceCategory.FOOD);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expectedResult);
    }

    @Test
    void addService_alreadyExists() throws BusinessLogicException {
        when(serviceService.addService(anyString(), anyDouble(), any(ServiceCategory.class)))
                .thenThrow(new BusinessLogicException("Услуга уже существует"));

        try {
            serviceController.addService("WiFi", 10.0, ServiceCategory.FOOD);
        } catch (BusinessLogicException e) {
            assertThat(e.getMessage()).contains("Услуга уже существует");
        }
    }

    @Test
    void changeServicePrice_success() throws Exception {
        String expected = "Успех: Цена изменена";
        when(serviceService.changePrice("WiFi", 20.0)).thenReturn(expected);

        ResponseEntity<String> response = serviceController.changeServicePrice("WiFi", 20.0);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    void changeServicePrice_notFound() throws Exception {
        when(serviceService.changePrice("WiFi", 20.0))
                .thenThrow(new EntityNotFoundException("Услуга не найдена"));

        assertThatThrownBy(() -> serviceController.changeServicePrice("WiFi", 20.0))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getAllServices_success() {
        when(serviceService.getAllServices()).thenReturn(List.of(testService));

        ResponseEntity<List<ServiceDTO>> response = serviceController.getAllServices(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getName()).isEqualTo("WiFi");
    }

    @Test
    void getAllServices_sorted() {
        when(serviceService.getAllServicesSortedByCategoryAndPrice()).thenReturn(List.of(testService));

        ResponseEntity<List<ServiceDTO>> response = serviceController.getAllServices("category,price");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void exportServices_success() {
        when(serviceService.exportToCsv("/path/file.csv")).thenReturn("Успех");

        ResponseEntity<String> response = serviceController.exportServices("/path/file.csv");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Успех");
    }

    @Test
    void importServices_success() {
        when(serviceService.importFromCsv("/path/file.csv")).thenReturn("Успех");

        ResponseEntity<String> response = serviceController.importServices("/path/file.csv");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Успех");
    }
}