
package com.springbank.dto.Request;

import com.springbank.enums.TipoTransaccion;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;


public class TransaccionRequestDTO {
    @NotBlank
    final private BigDecimal monto;
    @NotBlank
    final private TipoTransaccion tipo;
    @NotBlank
    final private Long cuentaOrigenId;
    @NotBlank
    final private Long cuentaDestinoId;
    @Nullable
    final private String motivo;


    public TransaccionRequestDTO(BigDecimal monto, TipoTransaccion tipo, Long cuentaOrigenId, Long cuentaDestinoId, String motivo) {
        this.monto = monto;
        this.tipo = tipo;
        this.cuentaOrigenId = cuentaOrigenId;
        this.cuentaDestinoId = cuentaDestinoId;
        this.motivo = motivo;
    }


    public BigDecimal getMonto() {
        return monto;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public Long getCuentaOrigenId() {
        return cuentaOrigenId;
    }

    public Long getCuentaDestinoId() {
        return cuentaDestinoId;
    }

    public String getMotivo() {
        return motivo;
    } 
    
}
