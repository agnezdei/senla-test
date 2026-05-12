package com.agnezdei.library.controller;

import com.agnezdei.library.dto.PasswordChangeDTO;
import com.agnezdei.library.dto.UsernameUpdateDTO;
import com.agnezdei.library.dto.UserResponseDTO;
import com.agnezdei.library.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private Authentication userAuthentication;
    private static final String TEST_USERNAME = "john";
    private static final Long TEST_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        // Создаём аутентификацию обычного пользователя (без стаббинга сервиса)
        userAuthentication = new UsernamePasswordAuthenticationToken(TEST_USERNAME, null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void getCurrentUser_shouldReturnUser() {
        when(userService.findIdByUsername(TEST_USERNAME)).thenReturn(TEST_USER_ID);
        UserResponseDTO expected = new UserResponseDTO();
        expected.setId(TEST_USER_ID);
        expected.setUsername(TEST_USERNAME);
        when(userService.getUserById(TEST_USER_ID)).thenReturn(expected);

        ResponseEntity<UserResponseDTO> response = userController.getCurrentUser(userAuthentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
        verify(userService).findIdByUsername(TEST_USERNAME);
        verify(userService).getUserById(TEST_USER_ID);
    }

    @Test
    void updateUsername_success() {
        UsernameUpdateDTO dto = new UsernameUpdateDTO();
        dto.setNewUsername("newJohn");
        when(userService.findIdByUsername(TEST_USERNAME)).thenReturn(TEST_USER_ID);
        doNothing().when(userService).updateUsername(TEST_USER_ID, dto);

        ResponseEntity<Void> response = userController.updateUsername(userAuthentication, dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userService).updateUsername(TEST_USER_ID, dto);
    }

    @Test
    void updatePassword_success() {
        PasswordChangeDTO dto = new PasswordChangeDTO();
        dto.setOldPassword("old");
        dto.setNewPassword("new");
        when(userService.findIdByUsername(TEST_USERNAME)).thenReturn(TEST_USER_ID);
        doNothing().when(userService).updatePassword(TEST_USER_ID, dto);

        ResponseEntity<Void> response = userController.updatePassword(userAuthentication, dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userService).updatePassword(TEST_USER_ID, dto);
    }

    @Test
    void getAllUsers_asAdmin_shouldReturnList() {
        // Для админ-метода не нужна аутентификация в параметрах, только в SecurityContext
        // Но у нас нет SecurityContextHolder, так как метод не использует Authentication.
        // Просто мокаем сервис и вызываем метод.
        List<UserResponseDTO> expectedList = List.of(new UserResponseDTO(), new UserResponseDTO());
        when(userService.getAllUsers()).thenReturn(expectedList);

        ResponseEntity<List<UserResponseDTO>> response = userController.getAllUsers();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        verify(userService).getAllUsers();
    }

    @Test
    void deleteUser_asAdmin_shouldReturnNoContent() {
        Long userIdToDelete = 5L;
        doNothing().when(userService).deleteUser(userIdToDelete);

        ResponseEntity<Void> response = userController.deleteUser(userIdToDelete);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userService).deleteUser(userIdToDelete);
    }
}