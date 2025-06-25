
package com.springbank.dto.Request;

import com.springbank.enums.EstadoTransaccion;
import com.springbank.enums.TipoTransaccion;
import java.math.BigDecimal;
import java.time.LocalDateTime;


public class TransferenciaRequestDTO {
    final private BigDecimal monto = BigDecimal.ZERO;
    final private TipoTransaccion tipo;
    final private Long cuentaOrigenId;
    final private Long cuentaDestinoId;
    final private EstadoTransaccion estado;
    final private String motivo;
    final private LocalDateTime fecha;

    public TransferenciaRequestDTO( TipoTransaccion tipo, Long cuentaOrigenId, Long cuentaDestinoId, EstadoTransaccion estado, String motivo, LocalDateTime fecha) {
        this.tipo = tipo;
        this.cuentaOrigenId = cuentaOrigenId;
        this.cuentaDestinoId = cuentaDestinoId;
        this.estado = estado;
        this.motivo = motivo;
        this.fecha = fecha;
    }


    public BigDecimal getMonto() {
        return monto;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public Long getCuentaOrigen() {
        return cuentaOrigenId;
    }

    public Long getCuentaDestino() {
        return cuentaDestinoId;
    }

    public EstadoTransaccion getEstado() {
        return estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
    
    
}
