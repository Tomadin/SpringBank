
package com.springbank.dto.Response;


import com.springbank.enums.EstadoTransaccion;
import com.springbank.enums.TipoTransaccion;
import java.math.BigDecimal;
import java.time.LocalDateTime;


public class TransferenciaResponseDTO {
    final private Long id;
    final private BigDecimal monto;
    final private TipoTransaccion tipo;
    final private Long cuentaOrigenId;
    final private Long numeroCuentaOrigen;
    final private Long numeroCuentaDestino;
    final private Long cuentaDestinoId;
    final private EstadoTransaccion estado;
    final private String motivo;
    final private LocalDateTime fecha;

   
    
    public TransferenciaResponseDTO(Long id, BigDecimal monto, TipoTransaccion tipo, Long cuentaOrigenId, Long cuentaDestinoId,  EstadoTransaccion estado, String motivo, LocalDateTime fecha, Long numeroCuentaOrigen, Long numeroCuentaDestino) {
        this.id = id;
        this.monto = monto;
        this.tipo = tipo;
        this.cuentaOrigenId = cuentaOrigenId;
        this.cuentaDestinoId = cuentaDestinoId;
        this.estado = estado;
        this.motivo = motivo;
        this.fecha = fecha;
        this.numeroCuentaOrigen = numeroCuentaOrigen;
        this.numeroCuentaDestino = numeroCuentaDestino;
    }

    public Long getId() {
        return id;
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

    public Long getCuentaOrigenId() {
        return cuentaOrigenId;
    }

    public Long getNumeroCuentaOrigen() {
        return numeroCuentaOrigen;
    }

    public Long getNumeroCuentaDestino() {
        return numeroCuentaDestino;
    }

    public Long getCuentaDestinoId() {
        return cuentaDestinoId;
    }
    
    
}
