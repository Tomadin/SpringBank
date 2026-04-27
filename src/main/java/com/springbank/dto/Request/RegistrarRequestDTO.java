
package com.springbank.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrarRequestDTO {
    @NotBlank(message = "El username no puede estar vacío.")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres.")
    private final String username;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    private final String password;

    public RegistrarRequestDTO(String username, String password) {
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
