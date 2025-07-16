package com.springbank.service;

import com.springbank.dto.Request.UsuarioRequestDTO;
import com.springbank.dto.Response.UsuarioResponseDTO;
import com.springbank.entity.Cliente;
import com.springbank.entity.Usuario;
import com.springbank.enums.RolUsuario;
import com.springbank.exception.UsernameNoEncontradoException;
import com.springbank.exception.UsuarioException;
import com.springbank.repository.ClienteRepository;
import com.springbank.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.List;
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

        if (cliente.getUsuario() != null) {
            throw new RuntimeException("Este cliente ya tiene un usuario asignado.");
        }
        cliente.setUsuario(usuario);

        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorUsername(String username) {
        Usuario user = (Usuario) usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNoEncontradoException("No se encontró una cuenta con el username: " + username));
        UsuarioResponseDTO userResponse = new UsuarioResponseDTO(user.getId(), user.getUsername(), user.getRol(), user.getCliente().getId());
        return user;
    }

    public UsuarioResponseDTO buscarPorUsernameDTO(String username) {
        Usuario user = (Usuario) usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNoEncontradoException("No se encontró una cuenta con el username: " + username));
        UsuarioResponseDTO userResponse = new UsuarioResponseDTO(user.getId(), user.getUsername(), user.getRol(), user.getCliente().getId());
        return userResponse;
    }

    public List<Usuario> traerUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<UsuarioResponseDTO> traerTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        if (usuarios.isEmpty()) {
            throw new UsuarioException("No existen usuarios.");
        }

        List<UsuarioResponseDTO> usuariosResponse = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            UsuarioResponseDTO usuarioResponse = new UsuarioResponseDTO(usuario.getId(), usuario.getUsername(), usuario.getRol(), usuario.getCliente().getId());
            usuariosResponse.add(usuarioResponse);
        }
        return usuariosResponse;
    }

    public List<UsuarioResponseDTO> obtenerTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<UsuarioResponseDTO> usuariosResponseDTO = new ArrayList<>();
        if(usuarios.isEmpty()){
            throw new UsuarioException("No hay Usuarios en el sistema. ");
        }
        for (Usuario usuario : usuarios) {
            UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO(usuario.getId(), usuario.getUsername(),
                    usuario.getRol(), usuario.getCliente().getId());
            usuariosResponseDTO.add(usuarioResponseDTO);
        }
        return usuariosResponseDTO;
    }
    
}
