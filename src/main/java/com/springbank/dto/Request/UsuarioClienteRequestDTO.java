
package com.springbank.dto.Request;

import com.springbank.enums.RolUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class UsuarioClienteRequestDTO {//utilizar @Valid en el controlador para validad los campos @NotBlank y @NotNull
    //Usuario
    @NotBlank
    final private String username;
    @NotBlank
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    final private String password;
    @NotNull
    final private RolUsuario rol;
    //Cliente
    @NotBlank
    private final String nombre;
    @NotBlank
    private final String apellido;
    @NotBlank
    private final String email;
    @NotBlank
    private final String dni;

    public UsuarioClienteRequestDTO(String username, String password, RolUsuario rol, Long clienteId, String nombre, String apellido, String email, String dni) {
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.dni = dni;
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

}
