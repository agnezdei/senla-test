package com.agnezdei.library.controller;

import com.agnezdei.library.dto.UserRegistrationDTO;
import com.agnezdei.library.dto.UserResponseDTO;
import com.agnezdei.library.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @Test
    void registerUser_shouldReturnCreated() {
        UserRegistrationDTO registration = new UserRegistrationDTO("john", "secret");
        UserResponseDTO expectedResponse = new UserResponseDTO();
        expectedResponse.setId(1L);
        expectedResponse.setUsername("john");

        when(userService.register(any(UserRegistrationDTO.class))).thenReturn(expectedResponse);

        ResponseEntity<UserResponseDTO> response = authController.registerUser(registration);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        assertThat(response.getBody().getUsername()).isEqualTo("john");
    }
}