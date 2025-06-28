package com.springbank;

import com.springbank.entity.Cliente;
import com.springbank.entity.Cuenta;
import com.springbank.enums.TipoCuenta;
import com.springbank.repository.CuentaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@SpringBootTest
class SpringbankApplicationTests {

    @Autowired
    private CuentaRepository cuentaRepository;
    /*
    @Test
    void testOptimisticLocking() {
        // Obtener la misma cuenta para simular acceso concurrente
        Cuenta cuentaUsuario1 = cuentaRepository.findById(1L).orElseThrow();
        Cuenta cuentaUsuario2 = cuentaRepository.findById(1L).orElseThrow();

        // Usuario 1 modifica y guarda primero
        cuentaUsuario1.setSaldo(new BigDecimal("2000.00"));
        cuentaRepository.save(cuentaUsuario1); // Incrementa la versión

        // Usuario 2 intenta guardar con la versión antigua
        cuentaUsuario2.setSaldo(new BigDecimal("1000.00"));
        assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
            cuentaRepository.save(cuentaUsuario2); // Debe fallar por versión desactualizada
        });
    }
*/
}
