package com.springbank.service;

import com.springbank.entity.Cuenta;
import com.springbank.entity.Transaccion;
import com.springbank.enums.TipoTransaccion;
import com.springbank.repository.CuentaRepository;
import com.springbank.repository.TransaccionRepository;
import java.math.BigDecimal;
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
        Cuenta cuenta = obtenerCuentaValida(cuentaId); //Verificamos y devolvemos la cuenta.
        
        // Sumamos el monto al saldo actual
        BigDecimal nuevoMonto = cuenta.getSaldo().add(monto);
        
        String descripcion = "Depósito a cuenta " + cuenta.getNumeroCuenta();
        Transaccion transaccion = crearTransaccion(monto, TipoTransaccion.DEPOSITO, cuenta, null, descripcion); //crear transaccion
        
        transaccion.marcarComoCompletada(); //marcamos como completada
        transaccionRepository.save(transaccion); //guardamos la transaccion
        
        cuenta.setSaldo(nuevoMonto);
        cuentaRepository.save(cuenta); 
    }

    private void validarMonto(BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero. Valor recibido: " + monto);
        }
    }

    private Cuenta obtenerCuentaValida(Long cuentaOrigenId){
        return cuentaRepository.findById(cuentaOrigenId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con id: " + cuentaOrigenId));
    }
     //MODIFICAR PARA SOPORTAR MAS TRANSACCIONES COMO RETIRO O TRANSFERENCIA
    private Transaccion crearTransaccion(BigDecimal monto, TipoTransaccion tipo, Cuenta cuentaOrigen, Cuenta cuentaDestino, String descripcion){
        
        if(tipo.equals(TipoTransaccion.DEPOSITO)){
            cuentaDestino = cuentaOrigen;
        }
        
        Transaccion transaccion = new Transaccion(monto, tipo, cuentaOrigen, cuentaDestino, descripcion);
        
        return transaccion;
    }
    
}
