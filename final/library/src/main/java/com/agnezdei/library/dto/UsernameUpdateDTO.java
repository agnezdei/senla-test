package com.agnezdei.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UsernameUpdateDTO {
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 3, max = 100, message = "Допустимая длина имени от 3 до 100 символов")
    private String newUsername;

    public UsernameUpdateDTO() {}
    public UsernameUpdateDTO(String newUsername) { this.newUsername = newUsername; }

    public String getNewUsername() { return newUsername; }
    public void setNewUsername(String newUsername) { this.newUsername = newUsername; }
}