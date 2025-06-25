
package com.springbank.dto.Request;


import com.springbank.enums.RolUsuario;


public class UsuarioRequestDTO {
    final private String username;
    final private String password;
    final private RolUsuario rol;
    final private Long clienteID;

    public UsuarioRequestDTO(String username, String password, RolUsuario rol, Long clienteID) {
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.clienteID = clienteID;
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

    public Long getClienteID() {
        return clienteID;
    }

    
    
}
