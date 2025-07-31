package com.springbank.controller;

import com.springbank.dto.Response.UsuarioResponseDTO;
import com.springbank.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    
    @Operation(summary = "Traer los usuarios", description = "Un admin puede traer todos los usuarios de la BBDD.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuarios traidos correctamente."),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida.")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<UsuarioResponseDTO>> traerTodos() {
        List<UsuarioResponseDTO> usuarios = usuarioService.traerTodos();
        return ResponseEntity.ok(usuarios);
    }
    
    @Operation(summary = "Traer usuario", description = "Traer un usuario de la BBDD, a traves de su username.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuarios traidos correctamente."),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida.")
    })
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and #username == authentication.name)")
    @GetMapping("/{username}")
    public ResponseEntity<UsuarioResponseDTO> traerUsuarioUsername(@PathVariable String username){
        UsuarioResponseDTO usuario = usuarioService.buscarPorUsernameDTO(username);
        return ResponseEntity.ok(usuario); 
    }
    
}
