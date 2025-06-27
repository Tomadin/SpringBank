
package com.springbank.controller;

import com.springbank.dto.Request.TransaccionRequestDTO;
import com.springbank.enums.TipoTransaccion;
import com.springbank.exception.TransaccionException;
import com.springbank.service.TransaccionService;
import java.math.BigDecimal;
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
    
    @PostMapping("/depositar")
    public ResponseEntity<String> depositar(@RequestBody TransaccionRequestDTO transaccionRequestDTO) {
        if (!transaccionRequestDTO.getTipo().equals(TipoTransaccion.DEPOSITO)) {
            throw new TransaccionException("Ingreso una transaccion de tipo "+transaccionRequestDTO.getTipo()+" en un deposito.");
        }
        
        transaccionService.realizarDeposito(transaccionRequestDTO.getMonto(), transaccionRequestDTO.getCuentaDestinoId());
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Depósito realizado correctamente.");
    }
    
    @PostMapping("/retirar")
    public ResponseEntity<String> retirar(@RequestBody TransaccionRequestDTO transaccionRequestDTO){
        if (!transaccionRequestDTO.getTipo().equals(TipoTransaccion.RETIRO)) {
            throw new TransaccionException("Ingreso una transaccion de tipo "+transaccionRequestDTO.getTipo()+" en un retiro.");
        }
        
        transaccionService.realizarRetiro(transaccionRequestDTO.getMonto(), transaccionRequestDTO.getCuentaDestinoId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Retiro realizado correctamente.");
    }
    
}
