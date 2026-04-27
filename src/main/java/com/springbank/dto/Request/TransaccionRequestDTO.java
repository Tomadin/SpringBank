
package com.springbank.dto.Request;

import com.springbank.enums.TipoTransaccion;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;


public class TransaccionRequestDTO {
    @NotNull
    @Positive
    final private BigDecimal monto;
    @NotNull
    final private TipoTransaccion tipo;
    @NotNull
    final private Long numeroCuentaOrigen;
    @NotNull
    final private Long numeroCuentaDestino;
    @Nullable
    final private String motivo;


    public TransaccionRequestDTO(BigDecimal monto, TipoTransaccion tipo, Long numeroCuentaOrigenId, Long numeroCuentaDestinoId, String motivo) {
        this.monto = monto;
        this.tipo = tipo;
        this.numeroCuentaOrigen = numeroCuentaOrigenId;
        this.numeroCuentaDestino = numeroCuentaDestinoId;
        this.motivo = motivo;
    }


    public BigDecimal getMonto() {
        return monto;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public Long getNumeroCuentaOrigen() {
        return numeroCuentaOrigen;
    }

    public Long getNumeroCuentaDestino() {
        return numeroCuentaDestino;
    }

    public String getMotivo() {
        return motivo;
    } 
  
}
