package com.agnezdei.hotelmvc.service;

import com.agnezdei.hotelmvc.csv.CsvExporter;
import com.agnezdei.hotelmvc.csv.GuestServiceCsvImporter;
import com.agnezdei.hotelmvc.exceptions.BusinessLogicException;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.model.Guest;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import com.agnezdei.hotelmvc.repository.GuestDAO;
import com.agnezdei.hotelmvc.repository.GuestServiceDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuestServiceServiceTest {

    @Mock private GuestServiceDAO guestServiceDAO;
    @Mock private GuestDAO guestDAO;
    @Mock private CsvExporter csvExporter;
    @Mock private GuestServiceCsvImporter guestServiceImporter;
    @Mock private ServiceService serviceService;

    @InjectMocks private GuestServiceService guestServiceService;

    private Guest guest;
    private Service service;
    private com.agnezdei.hotelmvc.model.GuestService guestServiceEntity;

    @BeforeEach
    void setUp() {
        guest = new Guest("John Doe", "AB123456");
        guest.setId(1L);
        service = new Service("WiFi", 10.0, ServiceCategory.FOOD);
        service.setId(1L);
        guestServiceEntity = new com.agnezdei.hotelmvc.model.GuestService(guest, service, LocalDate.now());
        guestServiceEntity.setId(1L);
    }

    @Test
    void exportToCsv_success() throws Exception {
        when(guestServiceDAO.findAll()).thenReturn(Collections.singletonList(guestServiceEntity));
        doNothing().when(csvExporter).exportGuestServices(anyList(), anyString());

        String result = guestServiceService.exportToCsv("/path/file.csv");

        assertThat(result).startsWith("Успех");
    }

    @Test
    void exportToCsv_ioException() throws Exception {
        when(guestServiceDAO.findAll()).thenReturn(Collections.emptyList());
        doThrow(new java.io.IOException("IO error")).when(csvExporter).exportGuestServices(anyList(), anyString());

        String result = guestServiceService.exportToCsv("/path/file.csv");

        assertThat(result).contains("Ошибка файла");
    }

    @Test
    void importFromCsv_success() {
        when(guestServiceImporter.importGuestServices(anyString())).thenReturn("Imported");

        String result = guestServiceService.importFromCsv("/path/file.csv");

        assertThat(result).isEqualTo("Imported");
    }

    @Test
    void addServiceToGuest_success() throws Exception {
        when(guestDAO.findByPassportNumber("AB123456")).thenReturn(Optional.of(guest));
        when(serviceService.findByName("WiFi")).thenReturn(Optional.of(service));
        when(guestServiceDAO.save(any(com.agnezdei.hotelmvc.model.GuestService.class)))
                .thenReturn(guestServiceEntity);

        String result = guestServiceService.addServiceToGuest("AB123456", "WiFi", LocalDate.now());

        assertThat(result).contains("Успех");
        verify(guestServiceDAO).save(any(com.agnezdei.hotelmvc.model.GuestService.class));
    }

    @Test
    void addServiceToGuest_guestNotFound() {
        when(guestDAO.findByPassportNumber("AB123456")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> guestServiceService.addServiceToGuest("AB123456", "WiFi", LocalDate.now()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void addServiceToGuest_serviceNotFound() {
        when(guestDAO.findByPassportNumber("AB123456")).thenReturn(Optional.of(guest));
        when(serviceService.findByName("WiFi")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> guestServiceService.addServiceToGuest("AB123456", "WiFi", LocalDate.now()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не найдена");
    }

    // -------------------- removeServiceFromGuest --------------------
    @Test
    void removeServiceFromGuest_success() throws Exception {
        when(guestServiceDAO.findById(1L)).thenReturn(Optional.of(guestServiceEntity));
        doNothing().when(guestServiceDAO).deleteById(1L);

        String result = guestServiceService.removeServiceFromGuest(1L);

        assertThat(result).contains("Успех");
        verify(guestServiceDAO).deleteById(1L);
    }

    @Test
    void removeServiceFromGuest_notFound() {
        when(guestServiceDAO.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> guestServiceService.removeServiceFromGuest(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getGuestServices_success() throws BusinessLogicException {
        when(guestServiceDAO.findByGuestId(1L)).thenReturn(Collections.singletonList(guestServiceEntity));

        var dtoList = guestServiceService.getGuestServices(1L);

        assertThat(dtoList).hasSize(1);
        assertThat(dtoList.get(0).getServiceName()).isEqualTo("WiFi");
    }

    @Test
    void getGuestServices_dbError() {
        when(guestServiceDAO.findByGuestId(1L)).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> guestServiceService.getGuestServices(1L))
                .isInstanceOf(BusinessLogicException.class);
    }
}