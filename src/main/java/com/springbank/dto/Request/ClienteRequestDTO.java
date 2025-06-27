
package com.springbank.dto.Request;

import jakarta.validation.constraints.NotBlank;


public class ClienteRequestDTO{
    @NotBlank
    private final String nombre;
    @NotBlank
    private final String apellido;
    @NotBlank
    private final String email;
    @NotBlank
    private final String dni;


    public ClienteRequestDTO(String nombre, String apellido, String email, String dni) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.dni = dni;

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
    



    @Override
    public String toString() {
        return "ClienteRequestDTO{" + "nombre=" + nombre + ", apellido=" + apellido + ", email=" + email + ", dni=" + dni + '}';
    }
    
    
}
