package com.springbank.service;

import com.springbank.entity.Cliente;
import com.springbank.entity.Usuario;
import com.springbank.repository.ClienteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ClienteService {

    @Autowired
    ClienteRepository clienteRepository;
    
    // UTILIZAR DTOs
    

    private Cliente cargarCliente(String nombre, String apellido, String email, String dni) {
        return new Cliente(nombre, apellido, email, dni);
    }

    public void crearCliente(String nombre, String apellido, String email, String dni) {
        clienteRepository.save(cargarCliente(nombre, apellido, email, dni));
    }

    public void asignarUsuarioPorId(Long id, Usuario usuario) {
        clienteRepository.findById(id).ifPresent(cliente -> {
            cliente.setUsuario(usuario);
            clienteRepository.save(cliente);
        });
    }

    //public void asignarCuentaPorId(Long id, Cuenta cuenta){
        
    //}
    
    
    
    
}
