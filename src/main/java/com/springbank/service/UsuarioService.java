package com.springbank.service;

import com.springbank.dto.Request.UsuarioRequestDTO;
import com.springbank.dto.Response.UsuarioResponseDTO;
import com.springbank.entity.Cliente;
import com.springbank.entity.Usuario;
import com.springbank.exception.UsernameNoEncontradoException;
import com.springbank.repository.ClienteRepository;
import com.springbank.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    ClienteRepository clienteRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario crearUsuario(UsuarioRequestDTO asignarUsuarioDTO) {
        Cliente cliente = clienteRepository.findById(asignarUsuarioDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + asignarUsuarioDTO.getClienteId()));

        String passwordEncriptado = passwordEncoder.encode(asignarUsuarioDTO.getPassword());
        
        Usuario usuario = new Usuario(asignarUsuarioDTO.getUsername(),
                passwordEncriptado,
                cliente);

        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorUsername(String username) {
        Usuario user = (Usuario) usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNoEncontradoException("No se encontró una cuenta con el username: "+username));
        return user;
    }
    
}
