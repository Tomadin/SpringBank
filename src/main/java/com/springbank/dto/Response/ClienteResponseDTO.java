
package com.springbank.dto.Response;


import java.util.List;


public class ClienteResponseDTO{
    private final Long id;
    private final String nombre;
    private final String apellido;
    private final String email;
    private final String dni;
    private final UsuarioResponseDTO usuario;
    private final List<Long> cuentasId;

    public ClienteResponseDTO(Long id, String nombre, String apellido, String email, String dni, UsuarioResponseDTO usuario, List<Long> cuentasId) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.dni = dni;
        this.usuario = usuario;
        this.cuentasId = cuentasId;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getDni() {
        return dni;
    }

    public UsuarioResponseDTO getUsuario() {
        return usuario;
    }

    public List<Long> getCuentasId() {
        return cuentasId;
    }

    
    
    
}
