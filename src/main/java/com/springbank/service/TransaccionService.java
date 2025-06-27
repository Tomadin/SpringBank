package com.springbank.service;

import com.springbank.dto.Response.TransferenciaResponseDTO;
import com.springbank.entity.Cuenta;
import com.springbank.entity.Transaccion;
import com.springbank.enums.EstadoTransaccion;
import com.springbank.enums.TipoTransaccion;
import com.springbank.exception.TransaccionException;
import com.springbank.repository.CuentaRepository;
import com.springbank.repository.TransaccionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    public void realizarDeposito(BigDecimal monto, Long cuentaId) {
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

    private Cuenta obtenerCuentaValida(Long cuentaOrigenId) {
        return cuentaRepository.findById(cuentaOrigenId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con id: " + cuentaOrigenId));
    }
    //MODIFICAR PARA SOPORTAR MAS TRANSACCIONES COMO RETIRO O TRANSFERENCIA

    private Transaccion crearTransaccion(BigDecimal monto, TipoTransaccion tipo, Cuenta cuentaOrigen, Cuenta cuentaDestino, String descripcion) {

        if (tipo.equals(TipoTransaccion.DEPOSITO) || tipo.equals(TipoTransaccion.RETIRO)) {
            cuentaDestino = cuentaOrigen;
        }

        Transaccion transaccion = new Transaccion(monto, tipo, cuentaOrigen, cuentaDestino, descripcion);

        return transaccion;
    }

    @Transactional
    public void realizarRetiro(BigDecimal monto, Long cuentaId) {
        validarMonto(monto);
        Cuenta cuenta = obtenerCuentaValida(cuentaId);
        validarSaldo(monto, cuenta.getSaldo());
        
        String descripcion = "Retiro en cuenta " + cuenta.getNumeroCuenta();
        Transaccion transaccion = crearTransaccion(monto, TipoTransaccion.RETIRO, cuenta, cuenta, descripcion);

        transaccion.marcarComoCompletada();
        transaccionRepository.save(transaccion);

        BigDecimal nuevoSaldo = cuenta.getSaldo().subtract(monto);

        cuenta.setSaldo(nuevoSaldo);
        cuentaRepository.save(cuenta);

    }

    private void validarSaldo(BigDecimal monto, BigDecimal saldo) {
        if (monto.compareTo(saldo) > 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar el retiro. Disponible: " + saldo);
        }
    }

    @Transactional
    public void realizarTransferencia(BigDecimal monto, Long cuentaOrigenId, Long cuentaDestinoId){
        //Cuentas
        Cuenta cuentaOrigen = obtenerCuentaValida(cuentaOrigenId);
        Cuenta cuentaDestino = obtenerCuentaValida(cuentaDestinoId);
        //Validaciones
        validarMonto(monto);
        validarCuentas(cuentaOrigenId, cuentaDestinoId);
        validarSaldo(monto, cuentaOrigen.getSaldo());
        
        String descripcion = 
                "Transaccion desde cuenta " + cuentaOrigen.getNumeroCuenta()+", titular: " +
                cuentaOrigen.getCliente().getNombre()+" "+cuentaOrigen.getCliente().getApellido()+ 
                "hacia cuenta "+cuentaDestino.getNumeroCuenta()+", titular: " +
                cuentaDestino.getCliente().getNombre()+" "+cuentaDestino.getCliente().getApellido();
        
        Transaccion transaccion = crearTransaccion(monto, TipoTransaccion.TRANSFERENCIA, cuentaOrigen, cuentaDestino, descripcion);
        transaccion.marcarComoCompletada();
        transaccionRepository.save(transaccion);
        //Restamos el monto al saldo de la cuenta de origen
        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(monto));
        //Sumamos el monto al saldo de la cuenta de destino
        cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(monto));
        
        cuentaRepository.save(cuentaOrigen);
        cuentaRepository.save(cuentaDestino);
    }

    private void validarCuentas(Long cuentaOrigenId, Long cuentaDestinoId) {
        if(cuentaOrigenId.equals(cuentaDestinoId)){
            throw new TransaccionException("No se puede realizar una transferencia a la misma cuenta.");
        }
    }

    List<TransferenciaResponseDTO> obtenerTransaccionesPorNumeroCuenta(Long numeroCuenta) {
        List<Transaccion> transacciones = transaccionRepository.obtenerHistorialPorNumeroCuenta(numeroCuenta);
        if(transacciones == null){
            throw new TransaccionException("No hay transacciones con el numero de cuenta "+numeroCuenta);
        }
        
        List<TransferenciaResponseDTO> transferenciasResponseDTO = new ArrayList<>();
        
        for (Transaccion transaccion : transacciones) {
            transferenciasResponseDTO.add(new TransferenciaResponseDTO(
                    transaccion.getId(),
                    transaccion.getMonto(),
                    transaccion.getTipo(),
                    transaccion.getCuentaOrigen().getId(),
                    transaccion.getCuentaDestino().getId(),
                    transaccion.getEstado(),
                    transaccion.getDescripcion(), 
                    transaccion.getFecha(),
                    transaccion.getCuentaOrigen().getNumeroCuenta(),
                    transaccion.getCuentaDestino().getNumeroCuenta()
                    ));
        }
        
        return transferenciasResponseDTO;
        
    }
    
    
}

