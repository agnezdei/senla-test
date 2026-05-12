package com.agnezdei.library.service;

import com.agnezdei.library.dto.PasswordChangeDTO;
import com.agnezdei.library.dto.UserRegistrationDTO;
import com.agnezdei.library.dto.UserResponseDTO;
import com.agnezdei.library.dto.UsernameUpdateDTO;
import com.agnezdei.library.exception.BusinessLogicException;
import com.agnezdei.library.exception.EntityNotFoundException;
import com.agnezdei.library.mapper.UserMapper;
import com.agnezdei.library.model.User;
import com.agnezdei.library.repository.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserDAO userDAO;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserMapper userMapper;
    @InjectMocks private UserService userService;

    private User user;
    private UserRegistrationDTO registrationDTO;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setPasswordHash("encoded");
        user.setRole(User.Role.USER);

        registrationDTO = new UserRegistrationDTO("john", "secret");
        responseDTO = new UserResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setUsername("john");
        responseDTO.setRole("USER");
    }

    @Test
    void register_shouldSaveUserAndReturnDto() {
        when(userDAO.findByUsername("john")).thenReturn(Optional.empty());
        when(userMapper.toEntity(registrationDTO)).thenReturn(user);
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(userDAO.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.register(registrationDTO);

        assertThat(result).isEqualTo(responseDTO);
        verify(userDAO).save(user);
    }

    @Test
    void register_shouldThrowWhenUsernameExists() {
        when(userDAO.findByUsername("john")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.register(registrationDTO))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("уже существует");
        verify(userDAO, never()).save(any());
    }

    @Test
    void getAllUsers_shouldReturnList() {
        when(userDAO.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(responseDTO);

        List<UserResponseDTO> result = userService.getAllUsers();
        assertThat(result).hasSize(1).contains(responseDTO);
    }

    @Test
    void getUserById_shouldReturnDto() {
        when(userDAO.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.getUserById(1L);
        assertThat(result).isEqualTo(responseDTO);
    }

    @Test
    void getUserById_shouldThrowWhenNotFound() {
        when(userDAO.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void updateUsername_success() {
        UsernameUpdateDTO dto = new UsernameUpdateDTO("newJohn");
        when(userDAO.findById(1L)).thenReturn(Optional.of(user));
        when(userDAO.findByUsername("newJohn")).thenReturn(Optional.empty());
        when(userDAO.update(any(User.class))).thenReturn(user);

        userService.updateUsername(1L, dto);

        assertThat(user.getUsername()).isEqualTo("newJohn");
        verify(userDAO).update(user);
    }

    @Test
    void updateUsername_shouldThrowWhenNewUsernameAlreadyExists() {
        User otherUser = new User();
        otherUser.setId(2L);
        UsernameUpdateDTO dto = new UsernameUpdateDTO("existing");
        when(userDAO.findById(1L)).thenReturn(Optional.of(user));
        when(userDAO.findByUsername("existing")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> userService.updateUsername(1L, dto))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("уже занято");
        verify(userDAO, never()).update(any());
    }

    @Test
    void updatePassword_success() {
        PasswordChangeDTO dto = new PasswordChangeDTO("oldPass", "newPass");
        when(userDAO.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "encoded")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("newEncoded");
        when(userDAO.update(any(User.class))).thenReturn(user);

        userService.updatePassword(1L, dto);

        assertThat(user.getPasswordHash()).isEqualTo("newEncoded");
        verify(userDAO).update(user);
    }

    @Test
    void updatePassword_shouldThrowWhenOldPasswordMismatch() {
        PasswordChangeDTO dto = new PasswordChangeDTO("wrong", "newPass");
        when(userDAO.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThatThrownBy(() -> userService.updatePassword(1L, dto))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Старый пароль указан неверно");
    }

    @Test
    void deleteUser_success() {
        when(userDAO.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userDAO).delete(user);

        userService.deleteUser(1L);
        verify(userDAO).delete(user);
    }

    @Test
    void deleteUser_notFound_throwsException() {
        when(userDAO.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findIdByUsername_shouldReturnId() {
        when(userDAO.findByUsername("john")).thenReturn(Optional.of(user));
        Long id = userService.findIdByUsername("john");
        assertThat(id).isEqualTo(1L);
    }

    @Test
    void findIdByUsername_shouldThrowWhenNotFound() {
        when(userDAO.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findIdByUsername("unknown"))
                .isInstanceOf(EntityNotFoundException.class);
    }
}