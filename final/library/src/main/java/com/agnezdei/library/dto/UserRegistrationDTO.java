package com.agnezdei.library.dto;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

public class UserRegistrationDTO {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 100, message = "Допустимая длина имени от 3 до 100 символов")
    private String username;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    private String password;

    public UserRegistrationDTO() {}

    public UserRegistrationDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }


    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}