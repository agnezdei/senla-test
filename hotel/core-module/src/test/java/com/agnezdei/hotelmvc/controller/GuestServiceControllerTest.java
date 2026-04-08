package com.agnezdei.hotelmvc.controller;

import com.agnezdei.hotelmvc.dto.GuestServiceDTO;
import com.agnezdei.hotelmvc.exceptions.EntityNotFoundException;
import com.agnezdei.hotelmvc.model.GuestService;
import com.agnezdei.hotelmvc.model.Service;
import com.agnezdei.hotelmvc.model.ServiceCategory;
import com.agnezdei.hotelmvc.service.GuestServiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GuestServiceControllerTest {

    @Mock
    private GuestServiceService guestServiceService;

    @InjectMocks
    private GuestServiceController guestServiceController;

    @Test
    void addServiceToGuest_success() throws Exception {
        when(guestServiceService.addServiceToGuest(eq("AB123456"), eq("WiFi"), any(LocalDate.class)))
                .thenReturn("Успех: Услуга добавлена");

        ResponseEntity<String> response = guestServiceController.addServiceToGuest("AB123456", "WiFi", LocalDate.of(2025, 1, 1));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Успех: Услуга добавлена");
    }

    @Test
    void addServiceToGuest_guestNotFound() throws Exception {
        when(guestServiceService.addServiceToGuest(anyString(), anyString(), any(LocalDate.class)))
                .thenThrow(new EntityNotFoundException("Гость не найден"));

        assertThatThrownBy(() -> guestServiceController.addServiceToGuest("XXX", "WiFi", LocalDate.of(2025, 1, 1)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void removeServiceFromGuest_success() throws Exception {
        when(guestServiceService.removeServiceFromGuest(1L)).thenReturn("Успех: Услуга удалена");

        ResponseEntity<String> response = guestServiceController.removeServiceFromGuest(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Успех: Услуга удалена");
    }

    @Test
    void removeServiceFromGuest_notFound() throws Exception {
        when(guestServiceService.removeServiceFromGuest(99L))
                .thenThrow(new EntityNotFoundException("Услуга не найдена"));

        assertThatThrownBy(() -> guestServiceController.removeServiceFromGuest(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getGuestServices_success() throws Exception {
        GuestServiceDTO dto = new GuestServiceDTO();
        dto.setServiceName("WiFi");
        when(guestServiceService.getGuestServices(1L)).thenReturn(List.of(dto));

        ResponseEntity<List<GuestServiceDTO>> response = guestServiceController.getGuestServices(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getServiceName()).isEqualTo("WiFi");
    }

    @Test
    void getGuestServicesByName_success() throws Exception {
        // Подготавливаем мок для метода, который реально вызывается в контроллере по умолчанию (sortedByDate)
        GuestService gs = new GuestService();
        Service service = new Service("WiFi", 10.0, ServiceCategory.FOOD);
        gs.setService(service);
        when(guestServiceService.getGuestServicesByNameSortedByDate("John Doe")).thenReturn(List.of(gs));

        ResponseEntity<List<GuestServiceDTO>> response = guestServiceController.getGuestServicesByName("John Doe", "date");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getServiceName()).isEqualTo("WiFi");
    }

    @Test
    void exportGuestServices_success() {
        when(guestServiceService.exportToCsv("/path/file.csv")).thenReturn("Успех");

        ResponseEntity<String> response = guestServiceController.exportGuestServices("/path/file.csv");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Успех");
    }

    @Test
    void importGuestServices_success() {
        when(guestServiceService.importFromCsv("/path/file.csv")).thenReturn("Успех");

        ResponseEntity<String> response = guestServiceController.importGuestServices("/path/file.csv");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Успех");
    }
}