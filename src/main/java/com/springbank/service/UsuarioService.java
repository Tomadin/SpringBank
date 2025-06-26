
package com.springbank.service;

import com.springbank.dto.Request.AsignarUsuarioDTO;
import com.springbank.dto.Response.UsuarioResponseDTO;
import com.springbank.entity.Cliente;
import com.springbank.entity.Usuario;
import com.springbank.repository.ClienteRepository;
import com.springbank.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {
    
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    ClienteRepository clienteRepository;
    
    @Transactional
    public Usuario crearUsuario(AsignarUsuarioDTO asignarUsuarioDTO) {
        Cliente cliente = clienteRepository.findById(asignarUsuarioDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + asignarUsuarioDTO.getClienteId()));
        
        Usuario usuario = new Usuario(asignarUsuarioDTO.getUsername(), 
                asignarUsuarioDTO.getPassword(), 
                cliente);
        
        return usuarioRepository.save(usuario);
    }

    
    
}
