package com.springbank.controller;

import com.springbank.dto.Request.TransaccionRequestDTO;
import com.springbank.enums.TipoTransaccion;
import com.springbank.exception.TransaccionException;
import com.springbank.service.TransaccionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1/transacciones")
public class TransaccionController {

    @Autowired
    TransaccionService transaccionService;


    @Operation(summary = "Realizar un depósito", description = "Una cuenta recibe una cantidad de dinero en su saldo.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Depósito realizado correctamente."),
        @ApiResponse(responseCode = "400", description = "Datos inválidos.")
    })
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/depositar")
    public ResponseEntity<String> depositar(@RequestBody TransaccionRequestDTO transaccionRequestDTO) {
        if (!transaccionRequestDTO.getTipo().equals(TipoTransaccion.DEPOSITO)) {
            throw new TransaccionException("Ingreso una transaccion de tipo " + transaccionRequestDTO.getTipo() + " en un deposito.");
        }

        transaccionService.realizarDeposito(transaccionRequestDTO.getMonto(), transaccionRequestDTO.getNumeroCuentaDestino());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Depósito realizado correctamente.");
    }

    @Operation(summary = "Realizar un retiro", description = "Una cuenta realiza un retiro siempre y cuando sea su cuenta y tenga el saldo seleccionado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Retiro realizado correctamente."),
        @ApiResponse(responseCode = "400", description = "Datos inválidos.")
    })
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/retirar")
    public ResponseEntity<String> retirar(@RequestBody TransaccionRequestDTO transaccionRequestDTO, Authentication authentication) {
        if (!transaccionRequestDTO.getTipo().equals(TipoTransaccion.RETIRO)) {
            throw new TransaccionException("Ingreso una transaccion de tipo " + transaccionRequestDTO.getTipo() + " en un retiro.");
        }

        String username = authentication.getName();

        if (!transaccionService.perteneceCuentaAUsuario(transaccionRequestDTO.getNumeroCuentaOrigen(), username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permiso para operar con esta cuenta.");
        }

        transaccionService.realizarRetiro(transaccionRequestDTO.getMonto(), transaccionRequestDTO.getNumeroCuentaOrigen());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Retiro realizado correctamente.");
    }

    @Operation(summary = "Crear una nueva transferencia", description = "Crear una transferencia entre dos cuentas, a traves de sus numeros de cuenta.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transferencia enviada correctamente."),
        @ApiResponse(responseCode = "400", description = "Datos inválidos.")
    })
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/transferir") //Testear el funcionamiento
    public ResponseEntity<String> transferir(@RequestBody TransaccionRequestDTO transaccionRequestDTO, Authentication authentication) {
        if (!transaccionRequestDTO.getTipo().equals(TipoTransaccion.TRANSFERENCIA)) {
            throw new TransaccionException("Ingreso una transaccion de tipo " + transaccionRequestDTO.getTipo() + " en una transferencia.");
        }

        String username = authentication.getName();
        
        if (!transaccionService.perteneceCuentaAUsuario(transaccionRequestDTO.getNumeroCuentaOrigen(), username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permiso para operar con esta cuenta.");
        }

        transaccionService.realizarTransferencia(transaccionRequestDTO.getMonto(), transaccionRequestDTO.getNumeroCuentaOrigen(), transaccionRequestDTO.getNumeroCuentaDestino());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Transferencia realizada correctamente.");
    }

}
