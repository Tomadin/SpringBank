
package com.springbank.dto.Request;

import com.springbank.dto.CuentaDTO;
import com.springbank.dto.UsuarioDTO;
import java.io.Serializable;
import java.util.List;


public class ClienteRequestDTO implements Serializable{
    private final String nombre;
    private final String apellido;
    private final String email;
    private final String dni;
    private final UsuarioDTO usuario;
    private final List<CuentaDTO> cuentas;

    public ClienteRequestDTO(String nombre, String apellido, String email, String dni, UsuarioDTO usuario, List<CuentaDTO> cuentas) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.dni = dni;
        this.usuario = usuario;
        this.cuentas = cuentas;
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
    

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public List<CuentaDTO> getCuentas() {
        return cuentas;
    }

    @Override
    public String toString() {
        return "ClienteRequestDTO{" + "nombre=" + nombre + ", apellido=" + apellido + ", email=" + email + ", dni=" + dni + ", usuario=" + usuario + ", cuentas=" + cuentas + '}';
    }
    
    
}
