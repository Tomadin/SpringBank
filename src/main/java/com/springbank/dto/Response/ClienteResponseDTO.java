
package com.springbank.dto.Response;


import java.util.List;


public class ClienteResponseDTO{
    private final Long id;
    private final String nombre;
    private final String apellido;
    private final UsuarioResponseDTO usuario;
    private final List<Long> cuentasId;

    public ClienteResponseDTO(Long id, String nombre, String apellido, UsuarioResponseDTO usuario, List<Long> cuentasId) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
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

    public UsuarioResponseDTO getUsuario() {
        return usuario;
    }

    public List<Long> getCuentasId() {
        return cuentasId;
    }

    
    
    
}
