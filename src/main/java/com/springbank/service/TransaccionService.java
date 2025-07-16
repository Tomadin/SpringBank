package com.springbank.service;

import com.springbank.dto.Response.TransaccionResponseDTO;
import com.springbank.entity.Cliente;
import com.springbank.entity.Cuenta;
import com.springbank.entity.Transaccion;
import com.springbank.enums.TipoTransaccion;
import com.springbank.exception.ClienteNoEncontrado;
import com.springbank.exception.CuentaNoEncontrada;
import com.springbank.exception.MontoInvalidoException;
import com.springbank.exception.SaldoInsuficienteException;
import com.springbank.exception.TransaccionException;
import com.springbank.repository.ClienteRepository;
import com.springbank.repository.CuentaRepository;
import com.springbank.repository.TransaccionRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransaccionService {

    @Autowired
    private TransaccionRepository transaccionRepository;
    @Autowired
    private CuentaRepository cuentaRepository;
    @Autowired
    private ClienteRepository clienteRepository;

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
        if (monto == null || monto.compareTo(new BigDecimal("99.99")) <= 0) {
            throw new MontoInvalidoException("El monto debe ser mayor a cien. Valor recibido: " + monto);
        }
    }

    private Cuenta obtenerCuentaValida(Long numeroCuentaOrigenId) {

        Cuenta cuenta = cuentaRepository.numeroCuenta(numeroCuentaOrigenId);
        if (cuenta == null) {
            throw new CuentaNoEncontrada("Cuenta no encontrada con numero de cuenta: " + numeroCuentaOrigenId);
        }

        return cuenta;
    }

    private Transaccion crearTransaccion(BigDecimal monto, TipoTransaccion tipo, Cuenta cuentaOrigen, Cuenta cuentaDestino, String descripcion) {
        Transaccion transaccion;
        if (tipo.equals(TipoTransaccion.DEPOSITO) || tipo.equals(TipoTransaccion.RETIRO)) {
            transaccion = new Transaccion(monto, tipo, cuentaOrigen, cuentaOrigen, descripcion);
        } else {
            transaccion = new Transaccion(monto, tipo, cuentaOrigen, cuentaDestino, descripcion);
        }

        return transaccion;
    }

    @Transactional
    public void realizarRetiro(BigDecimal monto, Long numeroCuentaOrigenId) {
        validarMonto(monto);
        Cuenta cuenta = obtenerCuentaValida(numeroCuentaOrigenId);
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
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar el retiro. Disponible: " + saldo);
        }
    }

    @Transactional
    public void realizarTransferencia(BigDecimal monto, Long numeroCuentaOrigenId, Long numeroCuentaDestinoId) {
        //Cuentas
        Cuenta cuentaOrigen = obtenerCuentaValida(numeroCuentaOrigenId);
        Cuenta cuentaDestino = obtenerCuentaValida(numeroCuentaDestinoId);
        //Validaciones
        validarMonto(monto);
        validarCuentas(numeroCuentaOrigenId, numeroCuentaDestinoId);
        validarSaldo(monto, cuentaOrigen.getSaldo());

        String descripcion
                = "Transaccion desde cuenta " + cuentaOrigen.getNumeroCuenta() + ", titular: "
                + cuentaOrigen.getCliente().getNombre() + " " + cuentaOrigen.getCliente().getApellido()
                + " hacia cuenta " + cuentaDestino.getNumeroCuenta() + ", titular: "
                + cuentaDestino.getCliente().getNombre() + " " + cuentaDestino.getCliente().getApellido();

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

    private void validarCuentas(Long numeroCuentaOrigenId, Long numeroCuentaDestinoId) {
        if (numeroCuentaOrigenId.equals(numeroCuentaDestinoId)) {
            throw new TransaccionException("No se puede realizar una transferencia a la misma cuenta.");
        }
    }

    public List<TransaccionResponseDTO> obtenerTransaccionesPorNumeroCuenta(Long numeroCuenta) {
        List<Transaccion> transacciones = transaccionRepository.obtenerHistorialPorNumeroCuenta(numeroCuenta);
        if (transacciones.isEmpty()) {
            throw new TransaccionException("No hay transacciones con el numero de cuenta " + numeroCuenta);
        }

        List<TransaccionResponseDTO> transferenciasResponseDTO = new ArrayList<>();

        for (Transaccion transaccion : transacciones) {
            transferenciasResponseDTO.add(new TransaccionResponseDTO(
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

    public List<TransaccionResponseDTO> obtenerTodos() {
        List<Transaccion> transacciones = transaccionRepository.findAll();
        List<TransaccionResponseDTO> transaccionesResponseDTO = new ArrayList<>();
        
        if (transacciones.isEmpty()) {
            throw new TransaccionException("No hay transacciones en el sistema.");
        }
        
        for (Transaccion transaccion : transacciones) {
            //Creamos un response para cada transaccion.
            TransaccionResponseDTO transaccionResponseDTO = new TransaccionResponseDTO(transaccion.getId(), transaccion.getMonto(),
                    transaccion.getTipo(), transaccion.getCuentaOrigen().getId(),
                    transaccion.getCuentaDestino().getId(),
                    transaccion.getEstado(), transaccion.getDescripcion(),
                    transaccion.getFecha(), transaccion.getCuentaOrigen().getNumeroCuenta(),
                    transaccion.getCuentaDestino().getNumeroCuenta());
            //Agregamos el response a la lista
            transaccionesResponseDTO.add(transaccionResponseDTO);
        }
        return transaccionesResponseDTO;
    }
    
    public boolean perteneceCuentaAUsuario(Long numeroCuentaOrigen, String username) {
        Cuenta cuenta = cuentaRepository.numeroCuenta(numeroCuentaOrigen);
        if (cuenta == null) {
            throw new CuentaNoEncontrada("No se encontró cuenta con número: " + numeroCuentaOrigen);
        }

        Cliente cliente = clienteRepository.findById(cuenta.getCliente().getId())
                .orElseThrow(() -> new ClienteNoEncontrado("Cliente no encontrado con id: " + cuenta.getCliente().getId()));

        return cliente.getUsuario().getUsername().equals(username);
    }
    
}
