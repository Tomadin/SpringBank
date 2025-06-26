package com.springbank.controller;

import com.springbank.dto.Request.ClienteRequestDTO;
import com.springbank.dto.Response.ClienteResponseDTO;
import com.springbank.exception.ClienteNoEncontrado;

import com.springbank.service.ClienteService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

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
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> traerClienteId(@PathVariable Long id) throws ClienteNoEncontrado {
        ClienteResponseDTO cliente = clienteService.obtenerClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ClienteResponseDTO>> traerTodos() {
        List<ClienteResponseDTO> clientes = clienteService.obtenerTodos();
        return ResponseEntity.ok(clientes);
    }

    
}
