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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1/transacciones")
public class TransaccionController {

    @Autowired
    TransaccionService transaccionService;

    @Operation(summary = "Realizar un depósito")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Depósito realizado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
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

    @Operation(summary = "Realizar un retiro")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Retiro realizado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/retirar")
    public ResponseEntity<String> retirar(@RequestBody TransaccionRequestDTO transaccionRequestDTO) {
        if (!transaccionRequestDTO.getTipo().equals(TipoTransaccion.RETIRO)) {
            throw new TransaccionException("Ingreso una transaccion de tipo " + transaccionRequestDTO.getTipo() + " en un retiro.");
        }

        transaccionService.realizarRetiro(transaccionRequestDTO.getMonto(), transaccionRequestDTO.getNumeroCuentaDestino());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Retiro realizado correctamente.");
    }

    @Operation(summary = "Crear una nueva transferencia")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transferencia enviada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/transferir") //Testear el funcionamiento
    public ResponseEntity<String> transferir(@RequestBody TransaccionRequestDTO transaccionRequestDTO) {
        if (!transaccionRequestDTO.getTipo().equals(TipoTransaccion.TRANSFERENCIA)) {
            throw new TransaccionException("Ingreso una transaccion de tipo " + transaccionRequestDTO.getTipo() + " en una transferencia.");
        }

        transaccionService.realizarTransferencia(transaccionRequestDTO.getMonto(), transaccionRequestDTO.getNumeroCuentaOrigen(), transaccionRequestDTO.getNumeroCuentaDestino());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Transferencia realizada correctamente.");
    }

}
