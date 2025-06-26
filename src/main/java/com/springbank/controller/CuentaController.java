/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/RestController.java to edit this template
 */
package com.springbank.controller;

import com.springbank.dto.Request.AsignarCuentaDTO;
import com.springbank.dto.Request.CuentaRequestDTO;
import com.springbank.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api/v1/cuentas")
public class CuentaController {
    private final CuentaService cuentaService;
    
    @Autowired
    public CuentaController(CuentaService cuentaService){
        this.cuentaService = cuentaService;
    }
    
    @PostMapping
    public ResponseEntity<String> crearCuenta(@RequestBody AsignarCuentaDTO asignarCuentaDTO){
        
        cuentaService.crearCuenta(asignarCuentaDTO);
        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Cuenta creado correctamente.");
    }
    
}


