package com.springbank.service;

import com.springbank.dto.Request.CuentaRequestDTO;
import com.springbank.entity.Cuenta;
import com.springbank.entity.Transaccion;
import com.springbank.enums.EstadoTransaccion;
import com.springbank.enums.TipoTransaccion;
import com.springbank.exception.TransaccionExeption;
import com.springbank.repository.CuentaRepository;
import com.springbank.repository.TransaccionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransaccionService {

    @Autowired
    TransaccionRepository transaccionRepository;
    @Autowired
    CuentaRepository cuentaRepository;

    @Transactional
    public void depositar(BigDecimal monto, Long cuentaId) {
        validarMonto(monto);
        Cuenta cuenta = validarCuenta(cuentaId); //Verificamos y devolvemos la cuenta.
        
        BigDecimal montoViejo= cuenta.getSaldo();
        
        // Sumamos el monto al saldo actual
        BigDecimal nuevoMonto = montoViejo.add(monto);
        
        String descripcion = "Depósito a cuenta " + cuenta.getNumeroCuenta();
        crearTransaccion(monto, TipoTransaccion.DEPOSITO, cuenta, null, descripcion);
        cuenta.setSaldo(nuevoMonto);
        cuentaRepository.save(cuenta); //crear transaccion
    }

    private void validarMonto(BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }
    }

    private Cuenta validarCuenta(Long cuentaOrigenId){
        return cuentaRepository.findById(cuentaOrigenId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con id: " + cuentaOrigenId));
    }
    @Transactional //MODIFICAR PARA SOPORTAR MAS TRANSACCIONES COMO RETIRO O TRANSFERENCIA
    private Transaccion crearTransaccion(BigDecimal monto, TipoTransaccion tipo, Cuenta cuentaOrigen, Cuenta cuentaDestino, String descripcion){
        
        if(tipo.equals(TipoTransaccion.DEPOSITO)){
            cuentaDestino = cuentaOrigen;
        }
        
        Transaccion transaccion = new Transaccion(monto, tipo, cuentaOrigen, cuentaDestino, descripcion);
        
        return transaccionRepository.save(transaccion);
    }
    
}
