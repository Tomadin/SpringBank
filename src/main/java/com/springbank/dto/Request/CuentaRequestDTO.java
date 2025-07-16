
package com.springbank.dto.Request;

import com.springbank.enums.TipoCuenta;
import jakarta.validation.constraints.NotNull;




public class CuentaRequestDTO {
    @NotNull(message = "El tipo de cuenta es obligatorio.")
    private final TipoCuenta tipoCuenta;
    @NotNull(message = "El id del cliente es obligatorio.")
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
