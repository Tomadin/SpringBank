package com.springbank.controller;

import com.springbank.dto.Response.UsuarioResponseDTO;
import com.springbank.service.UsuarioService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<UsuarioResponseDTO>> traerTodos() {
        List<UsuarioResponseDTO> usuarios = usuarioService.traerTodos();
        return ResponseEntity.ok(usuarios);
    }
    
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and #username == authentication.name)")
    @GetMapping("/{username}")
    public ResponseEntity<UsuarioResponseDTO> traerUsuarioUsername(@PathVariable String username){
        UsuarioResponseDTO usuario = usuarioService.buscarPorUsernameDTO(username);
        return ResponseEntity.ok(usuario); 
    }
    
}
