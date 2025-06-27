
package com.springbank.dto.Request;

import com.springbank.enums.RolUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class UsuarioRequestDTO { //utilizar @Valid en el controlador para validad los campos @NotBlank y @NotNull
    @NotBlank
    final private String username;
    @NotBlank
    final private String password;
    @NotNull
    final private RolUsuario rol;
    @NotNull
    final private Long clienteId;

    public UsuarioRequestDTO(String username, String password, RolUsuario rol, Long clienteId) {
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.clienteId = clienteId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public Long getClienteId() {
        return clienteId;
    }
    
    
}
