package com.springbank.controller;

import com.springbank.dto.Request.ClienteRequestDTO;
import com.springbank.dto.Response.ClienteResponseDTO;
import com.springbank.exception.ClienteNoEncontrado;
import com.springbank.exception.DniInvalido;
import com.springbank.exception.EmailInvalido;

import com.springbank.service.ClienteService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    /**
     *
     * IMPÓRTANTE: UTILIZAR APIRESPONSE PARA LAS RESPUESTAS DE LA API
     *
     *
     */
    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    
    @PostMapping
    public ResponseEntity<String> crearCliente(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO) throws Exception {
        try {
            clienteService.crearCliente(clienteRequestDTO);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Cliente creado correctamente.");
        } catch (DniInvalido | EmailInvalido e) {
            throw e;
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENTE')")
    public ResponseEntity<?> traerClienteId(@PathVariable Long id, Authentication authentication) throws ClienteNoEncontrado {//Authentication  es una interfaz de SpringSecurity quien esta autenticado con que roles y otros.

        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String username = authentication.getName();

        ClienteResponseDTO cliente = clienteService.obtenerClientePorId(id);

        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado.");
        }

        if (!esAdmin && (cliente.getUsuario() == null || !username.equals(cliente.getUsuario().getUsername()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para acceder a este recurso.");
        }

        return ResponseEntity.ok(cliente);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<ClienteResponseDTO>> traerTodos() {
        List<ClienteResponseDTO> clientes = clienteService.obtenerTodos();
        return ResponseEntity.ok(clientes);
    }

}
