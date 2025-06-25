
package com.springbank.dto.Request;

import com.springbank.enums.TipoCuenta;
import java.math.BigDecimal;



public class CuentaRequestDTO {
    private final TipoCuenta tipoCuenta;
    private final BigDecimal saldo;
    private final Long clienteId;

    public CuentaRequestDTO(TipoCuenta tipoCuenta, BigDecimal saldo, Long clienteId) {
        this.tipoCuenta = tipoCuenta;
        this.saldo = saldo;
        this.clienteId = clienteId;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public Long getClienteId() {
        return clienteId;
    }

    
}
