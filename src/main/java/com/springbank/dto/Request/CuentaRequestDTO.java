
package com.springbank.dto.Request;

import com.springbank.enums.TipoCuenta;




public class CuentaRequestDTO {
    private final TipoCuenta tipoCuenta;
    private final Long clienteId;

    public CuentaRequestDTO(TipoCuenta tipoCuenta, Long clienteId) {
        this.tipoCuenta = tipoCuenta;
        this.clienteId = clienteId;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }


    public Long getClienteId() {
        return clienteId;
    }

    
}
