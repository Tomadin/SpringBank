
package com.springbank.dto.Response;


import com.springbank.enums.RolUsuario;


public class UsuarioResponseDTO {
    final private Long id;
    final private String username;
    final private RolUsuario rol;
    final private Long clienteId;

    public UsuarioResponseDTO(Long id, String username, RolUsuario rol, Long clienteId) {
        this.id = id;
        this.username = username;
        this.rol = rol;
        this.clienteId = clienteId;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public Long getClienteId() {
        return clienteId;
    }

    
    
    
    
}
