
package com.springbank.dto.Response;

import java.math.BigDecimal;


public class SaldoResponseDTO {
    private final Long numeroCuenta;
    private final BigDecimal saldo;

    public SaldoResponseDTO(Long numeroCuenta, BigDecimal saldo) {
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
    }

    public Long getNumeroCuenta() {
        return numeroCuenta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }
    
    
    
            
}
