package com.springbank.service;

import com.springbank.dto.Request.AsignarCuentaDTO;
import com.springbank.entity.Cliente;
import com.springbank.entity.Cuenta;
import com.springbank.exception.CuentaInvalida;
import com.springbank.repository.CuentaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CuentaService {

    @Autowired
    CuentaRepository cuentaRepository;

    @Transactional
    public Cuenta crearCuenta(AsignarCuentaDTO cuentaDTO, Cliente cliente) {

        validarCuenta(cuentaDTO, cliente);

        Cuenta cuenta = new Cuenta(
                cuentaDTO.getTipoCuenta(),
                cliente,
                LocalDateTime.now()
        );

        cuenta.setNumeroCuenta(generarNumeroCuentaUnico(cliente));

        return cuentaRepository.save(cuenta); //Guardamos y devolvemos la cuenta
    }

    private void validarCuenta(AsignarCuentaDTO cuentaDTO, Cliente cliente) {
        for (Cuenta cuenta : cliente.getCuentas()) {
            if (cuenta.getTipoCuenta().equals(cuentaDTO.getTipoCuenta())) {
                throw new CuentaInvalida("El cliente ya posee una cuenta del tipo: " + cuentaDTO.getTipoCuenta());
            }
        }
    }

    public BigDecimal obtenerSaldo(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new CuentaInvalida("No se encontró cuenta con el id " + id));

        return cuenta.getSaldo();
    }

    private Long generarNumeroCuentaUnico(Cliente cliente) {
        long base = cliente.getId() * 1_000_000L;
        long sufijo = generarNumeroAleatorio(6); // 6 dígitos
        return base + sufijo;
    }

    private long generarNumeroAleatorio(int digitos) {
        long min = (long) Math.pow(10, digitos - 1); 
        long max = (long) Math.pow(10, digitos) - 1; 
        return min + (long) (Math.random() * (max - min + 1));
    }

}
