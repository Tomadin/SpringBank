/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/RestController.java to edit this template
 */
package com.springbank.controller;

import com.springbank.dto.Request.CuentaRequestDTO;
import com.springbank.dto.Response.SaldoResponseDTO;
import com.springbank.dto.Response.TransaccionResponseDTO;
import com.springbank.service.CuentaService;
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
@RequestMapping("/api/v1/cuentas")
public class CuentaController {

    private final CuentaService cuentaService;

    @Autowired
    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @PostMapping
    public ResponseEntity<String> crearCuenta(@RequestBody CuentaRequestDTO cuentaRequestDTO) {

        cuentaService.crearCuenta(cuentaRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Cuenta creado correctamente.");
    }

    @GetMapping("/{numeroCuenta}/saldo")
    public ResponseEntity<SaldoResponseDTO> traerSaldoPorNumeroCuenta(@PathVariable Long numeroCuenta) {
        SaldoResponseDTO saldoResponseDTO = cuentaService.obtenerSaldo(numeroCuenta);
        
        return ResponseEntity.ok(saldoResponseDTO);
    }
    
    @GetMapping("/{numeroCuenta}/transacciones")
    public ResponseEntity<List<TransaccionResponseDTO>> traerTransaccionesPorNumeroCuenta(@PathVariable Long numeroCuenta) {
        List<TransaccionResponseDTO> transferenciasDTO = cuentaService.obtenerTransaccionesPorNumeroCuenta(numeroCuenta);
        
        return ResponseEntity.ok(transferenciasDTO);
    }

}
