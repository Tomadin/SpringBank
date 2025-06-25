
package com.springbank.dto.Response;


import com.springbank.dto.CuentaDTO;
import com.springbank.dto.UsuarioDTO;
import java.io.Serializable;
import java.util.List;


public class ClienteResponseDTO implements Serializable{
    private final String nombre;
    private final String apellido;
    private final String email;
    private final UsuarioDTO usuario;
    private final List<CuentaDTO> cuentas;

    public ClienteResponseDTO(String nombre, String apellido, String email, UsuarioDTO usuario, List<CuentaDTO> cuentas) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
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
    
    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public List<CuentaDTO> getCuentas() {
        return cuentas;
    }

    @Override
    public String toString() {
        return "ClienteRequestDTO{" + "nombre=" + nombre + ", apellido=" + apellido + ", email=" + email  + ", usuario=" + usuario + ", cuentas=" + cuentas + '}';
    }
    
    
}
