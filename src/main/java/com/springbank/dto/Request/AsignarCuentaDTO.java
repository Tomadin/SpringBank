
package com.springbank.dto.Request;

import com.springbank.enums.TipoCuenta;



public class AsignarCuentaDTO {
    final private TipoCuenta tipoCuenta;
    final private Long clienteId;

    public AsignarCuentaDTO(TipoCuenta tipoCuenta, Long clienteId) {
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
