
package com.springbank.controller;

import com.springbank.dto.Request.CuentaRequestDTO;
import com.springbank.dto.Response.SaldoResponseDTO;
import com.springbank.dto.Response.CuentaResponseDTO;
import com.springbank.dto.Response.TransaccionResponseDTO;
import com.springbank.dto.Response.UsuarioResponseDTO;
import com.springbank.service.CuentaService;
import com.springbank.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/v1/cuentas")
public class CuentaController {

    private final CuentaService cuentaService;
    private final UsuarioService usuarioService;

    @Autowired
    public CuentaController(CuentaService cuentaService, UsuarioService usuarioService) {
        this.cuentaService = cuentaService;
        this.usuarioService = usuarioService;
    }

    
    @Operation(summary = "Crear una Cuenta", description = "Un admin o un cliente pueden crear una cuenta, con el id del dueño de la misma, el clienteId.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cuenta creada correctamente."),
        @ApiResponse(responseCode = "400", description = "Datos inválidos.")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENTE')")
    @PostMapping
    public ResponseEntity<?> crearCuenta(@RequestBody CuentaRequestDTO cuentaRequestDTO, Authentication authentication) {

        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String username = authentication.getName();

        if (!esAdmin) {
            UsuarioResponseDTO usuario = usuarioService.buscarPorUsernameDTO(username);
            if (!usuario.getClienteId().equals(cuentaRequestDTO.getClienteId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes crear cuentas para otro cliente.");
            }
        }

        cuentaService.crearCuenta(cuentaRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Cuenta creado correctamente.");
    }

    @Operation(summary = "Ver saldo de una cuenta", description = "Ver el saldo de una cuenta, a traves de su numeroCuenta. Un Cliente solo puede ver su saldo y un admin el de cualquier cliente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Saldo traido correctamente."),
        @ApiResponse(responseCode = "400", description = "Datos inválidos.")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENTE')")
    @GetMapping("/{numeroCuenta}/saldo")
    public ResponseEntity<?> traerSaldoPorNumeroCuenta(@PathVariable Long numeroCuenta, Authentication authentication) {

        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String username = authentication.getName();

        CuentaResponseDTO cuentaResponse = cuentaService.buscarPorNumeroCuenta(numeroCuenta);
        if (cuentaResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cuenta no encontrada.");
        }

        //validamos el acceso si no es admin o si no es propietario
        if (!cuentaService.esPropietarioOAdmin(cuentaResponse.getCliente(), username, esAdmin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para ver el saldo de esta cuenta.");
        }
        //Si pasa la validación, obtener el saldo para la respuesta.
        SaldoResponseDTO saldoResponseDTO = cuentaService.obtenerSaldo(numeroCuenta);

        return ResponseEntity.ok(saldoResponseDTO);
    }
    
    @Operation(summary = "Ver transacciones de una Cuenta", description = "Ver las transacciones de una cuenta, a traves de su numeroCuenta. Un Cliente solo puede ver sus transacciones y un admin las de cualquier cliente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transacciones traidas correctamente."),
        @ApiResponse(responseCode = "400", description = "Datos inválidos.")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENTE')")
    @GetMapping("/{numeroCuenta}/transacciones")
    public ResponseEntity<?> traerTransaccionesPorNumeroCuenta(@PathVariable Long numeroCuenta, Authentication authentication) {

        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String username = authentication.getName();

        CuentaResponseDTO cuentaResponse = cuentaService.buscarPorNumeroCuenta(numeroCuenta);
        if (cuentaResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cuenta no encontrada.");
        }

        //validamos el acceso si no es admin o si no es propietario
        if (!cuentaService.esPropietarioOAdmin(cuentaResponse.getCliente(), username, esAdmin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para ver las transacciones de esta cuenta.");
        }
        //Si pasa la validación, obtener el saldo para la respuesta.

        List<TransaccionResponseDTO> transferenciasDTO = cuentaService.obtenerTransaccionesPorNumeroCuenta(numeroCuenta);

        return ResponseEntity.ok(transferenciasDTO);
    }

    
    
}
