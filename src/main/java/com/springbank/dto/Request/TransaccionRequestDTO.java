
package com.springbank.dto.Request;

import com.springbank.enums.TipoTransaccion;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;


public class TransaccionRequestDTO {
    @NotNull
    final private BigDecimal monto;
    @NotBlank
    final private TipoTransaccion tipo;
    @NotBlank
    final private Long numeroCuentaOrigen;
    @NotBlank
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
