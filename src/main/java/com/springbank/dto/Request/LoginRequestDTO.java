
package com.springbank.dto.Request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {
    @NotBlank(message = "El username no puede estar vacío.")
    private final String username;
    @NotBlank(message = "La contraseña no puede estar vacía.")
    private final String password;

    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
