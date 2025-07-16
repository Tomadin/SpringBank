
package com.springbank.controller;

import com.springbank.dto.Response.ClienteResponseDTO;
import com.springbank.dto.Response.CuentaResponseDTO;
import com.springbank.dto.Response.TransaccionResponseDTO;
import com.springbank.dto.Response.UsuarioResponseDTO;
import com.springbank.service.ClienteService;
import com.springbank.service.CuentaService;
import com.springbank.service.TransaccionService;
import com.springbank.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Admin", description = "Endpoints exclusivos para el rol ADMIN")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    
    private final ClienteService clienteService;
    private final TransaccionService transaccionService;
    private final CuentaService cuentaService;
    private final UsuarioService usuarioService;

    public AdminController(ClienteService clienteService, TransaccionService transaccionService, CuentaService cuentaService, UsuarioService usuarioService) {
        this.clienteService = clienteService;
        this.transaccionService = transaccionService;
        this.cuentaService = cuentaService;
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Obtener todos los clientes", description = "Devuelve una lista de todos los clientes registrados en el sistema.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/clientes")
    public ResponseEntity<List<ClienteResponseDTO>> verClientes(){
        List<ClienteResponseDTO> clientes = clienteService.obtenerTodos();
        return ResponseEntity.ok(clientes);
    }
    
    @Operation(summary = "Obtener cliente por dni", description = "Devuelve un cliente de la BBDD por su dni.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/clientes/dni/{dni}")
    public ResponseEntity<ClienteResponseDTO> verClientePorDNI(@PathVariable String dni){
        ClienteResponseDTO cliente = clienteService.obtenerClientePorDni(dni);
        return ResponseEntity.ok(cliente);
    }
    
    
    @Operation(summary= "Obtener todas las transacciones.", description= "Devuelve una lista todas las transacciones registradas en el sistema.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/transacciones")
    public ResponseEntity<List<TransaccionResponseDTO>> verTransacciones(){
        List<TransaccionResponseDTO> transacciones = transaccionService.obtenerTodos();
        
        return ResponseEntity.ok(transacciones);
    }
    
    
    @Operation(summary= "Obtener todas las cuentas.", description= "Devuelve una lista todas las cuentas registradas en el sistema.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cuentas")
    public ResponseEntity<List<CuentaResponseDTO>> verCuentas(){
        List<CuentaResponseDTO> cuentas = cuentaService.obtenerTodos();
        return ResponseEntity.ok(cuentas);
    }
    
    @Operation(summary= "Obtener cuenta por su numero de cuenta.", description= "Devuelve una cuenta registrada en el sistema.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cuentas/{numeroCuenta}")
    public ResponseEntity<CuentaResponseDTO> verCuentasPorNumeroCuenta(@PathVariable Long numeroCuenta){
        CuentaResponseDTO cuenta = cuentaService.buscarPorNumeroCuenta(numeroCuenta);
        return ResponseEntity.ok(cuenta);
    }
    
    
    @Operation(summary= "Obtener todos los usuarios.", description= "Devuelve una lista de todos los usuarios registradas en el sistema.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponseDTO>> verUsuarios(){
        List<UsuarioResponseDTO> usuarios = usuarioService.obtenerTodos();
        return ResponseEntity.ok(usuarios);
    }
    
    
    @Operation(summary= "Obtener usuario por su username.", description= "Devuelve un usuario registrado en el sistema a traves de su username.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/usuarios/username/{username}")
    public ResponseEntity<UsuarioResponseDTO> verUsuarioPorUsername(@PathVariable String username){
        UsuarioResponseDTO usuario = usuarioService.buscarPorUsernameDTO(username);
        return ResponseEntity.ok(usuario);
    }
    
}
