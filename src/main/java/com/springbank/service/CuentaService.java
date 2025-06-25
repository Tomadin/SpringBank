
package com.springbank.service;

import com.springbank.dto.Request.AsignarCuentaDTO;
import com.springbank.entity.Cliente;
import com.springbank.entity.Cuenta;
import com.springbank.repository.CuentaRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CuentaService {
    
    @Autowired
    CuentaRepository cuentaRepository;

    public Cuenta crearCuenta(AsignarCuentaDTO cuentaDTO, Cliente cliente) {

        Cuenta cuenta = new Cuenta(
                cuentaDTO.getTipoCuenta(),
                cliente,
                LocalDateTime.now()
        );
        
        return cuenta; //Devolvemos la cuenta
    }

    void guardarCuenta(Cuenta cuenta) {
        cuentaRepository.save(cuenta);
    }
    
}
