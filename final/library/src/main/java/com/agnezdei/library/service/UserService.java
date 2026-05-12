package com.agnezdei.library.service;

import com.agnezdei.library.dto.UserRegistrationDTO;
import com.agnezdei.library.dto.UserResponseDTO;
import com.agnezdei.library.dto.UsernameUpdateDTO;
import com.agnezdei.library.dto.PasswordChangeDTO;
import com.agnezdei.library.exception.BusinessLogicException;
import com.agnezdei.library.exception.EntityNotFoundException;
import com.agnezdei.library.mapper.UserMapper;
import com.agnezdei.library.model.User;
import com.agnezdei.library.model.User.Role;
import com.agnezdei.library.repository.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserDAO userDAO, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public UserResponseDTO register(UserRegistrationDTO dto) {
        log.info("Регистрация нового пользователя: username={}", dto.getUsername());

        if (userDAO.findByUsername(dto.getUsername()).isPresent()) {
            throw new BusinessLogicException("Пользователь с именем '" + dto.getUsername() + "' уже существует");
        }

        User user = userMapper.toEntity(dto);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword())); // хешируем пароль
        user.setRole(Role.USER);

        User saved = userDAO.save(user);
        log.info("Пользователь зарегистрирован: id={}", saved.getId());

        return userMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        log.info("Запрос всех пользователей");
        return userDAO.findAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        log.info("Запрос пользователя по id={}", id);
        User user = userDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден с id: " + id));
        return userMapper.toResponseDto(user);
    }

    public void updateUsername(Long userId, UsernameUpdateDTO dto) {
        log.info("Смена username: userId={}, newUsername={}", userId, dto.getNewUsername());

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден с id: " + userId));

        userDAO.findByUsername(dto.getNewUsername()).ifPresent(existing -> {
            if (!existing.getId().equals(userId)) {
                throw new BusinessLogicException("Имя пользователя '" + dto.getNewUsername() + "' уже занято");
            }
        });

        user.setUsername(dto.getNewUsername());
        userDAO.update(user);
        log.info("Username изменён для userId={}", userId);
    }

    public void updatePassword(Long userId, PasswordChangeDTO dto) {
        log.info("Смена пароля: userId={}", userId);

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден с id: " + userId));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPasswordHash())) {
            throw new BusinessLogicException("Старый пароль указан неверно");
        }

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userDAO.update(user);
        log.info("Пароль изменён для userId={}", userId);
    }

    public void deleteUser(Long userId) {
        log.info("Удаление пользователя: userId={}", userId);
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден с id: " + userId));

        userDAO.delete(user);
        log.info("Пользователь удалён: userId={}", userId);
    }

//    public void createAdmin(String username, String password) {
//        log.info("Создание администратора: username={}", username);
//        if (userDAO.findByUsername(username).isPresent()) {
//            throw new BusinessLogicException("Пользователь с именем '" + username + "' уже существует");
//        }
//        User admin = new User();
//        admin.setUsername(username);
//        admin.setPasswordHash(passwordEncoder.encode(password));
//        admin.setRole(Role.ADMIN);
//        userDAO.save(admin);
//        log.info("Администратор создан: username={}", username);
//    }

    @Transactional(readOnly = true)
    public Long findIdByUsername(String username) {
        return userDAO.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"))
                .getId();
    }
}