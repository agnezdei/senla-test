package com.agnezdei.hotelmvc.service;

import com.agnezdei.hotelmvc.csv.CsvExporter;
import com.agnezdei.hotelmvc.csv.ServiceCsvImporter;
import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import com.agnezdei.hotelmvc.repository.ServiceDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceServiceTest {

    @Mock private ServiceDAO serviceDAO;
    @Mock private CsvExporter csvExporter;
    @Mock private ServiceCsvImporter serviceImporter;

    @InjectMocks private ServiceService serviceService;

    private Service service;

    @BeforeEach
    void setUp() {
        service = new Service("WiFi", 10.0, ServiceCategory.FOOD);
        service.setId(1L);
    }

    @Test
    void exportToCsv_success() throws Exception {
        when(serviceDAO.findAll()).thenReturn(Collections.singletonList(service));
        doNothing().when(csvExporter).exportServices(anyList(), anyString());

        String result = serviceService.exportToCsv("/path/file.csv");

        assertThat(result).startsWith("Успех");
    }

    @Test
    void importFromCsv_success() {
        when(serviceImporter.importServices(anyString())).thenReturn("Imported");

        String result = serviceService.importFromCsv("/path/file.csv");

        assertThat(result).isEqualTo("Imported");
    }

    @Test
    void addService_success() throws BusinessLogicException {
        when(serviceDAO.findByName("WiFi")).thenReturn(Optional.empty());
        when(serviceDAO.save(any(Service.class))).thenReturn(service);

        String result = serviceService.addService("WiFi", 10.0, ServiceCategory.FOOD);

        assertThat(result).contains("Успех");
        verify(serviceDAO).save(any(Service.class));
    }

    @Test
    void addService_alreadyExists() {
        when(serviceDAO.findByName("WiFi")).thenReturn(Optional.of(service));

        assertThatThrownBy(() -> serviceService.addService("WiFi", 10.0, ServiceCategory.FOOD))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("уже существует");
    }

    @Test
    void changePrice_success() throws Exception {
        when(serviceDAO.findByName("WiFi")).thenReturn(Optional.of(service));

        String result = serviceService.changePrice("WiFi", 20.0);

        assertThat(result).contains("изменена");
        assertThat(service.getPrice()).isEqualTo(20.0);
        verify(serviceDAO).update(service);
    }

    @Test
    void changePrice_serviceNotFound() {
        when(serviceDAO.findByName("WiFi")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serviceService.changePrice("WiFi", 20.0))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getAllServices_success() {
        when(serviceDAO.findAll()).thenReturn(Collections.singletonList(service));

        List<Service> services = serviceService.getAllServices();

        assertThat(services).containsExactly(service);
    }

    @Test
    void getAllServicesSortedByCategoryAndPrice_success() {
        when(serviceDAO.findAllOrderedByCategoryAndPrice()).thenReturn(Collections.singletonList(service));

        List<Service> services = serviceService.getAllServicesSortedByCategoryAndPrice();

        assertThat(services).containsExactly(service);
    }

    @Test
    void findByName_found() {
        when(serviceDAO.findByName("WiFi")).thenReturn(Optional.of(service));

        Optional<Service> found = serviceService.findByName("WiFi");

        assertThat(found).isPresent().contains(service);
    }
}