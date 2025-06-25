
package com.springbank.dto.Response;


import com.springbank.enums.TipoCuenta;
import java.math.BigDecimal;
import java.time.LocalDateTime;


public class CuentaResponseDTO {
    final private Long id;
    final private Long numeroCuenta;
    final private TipoCuenta tipoCuenta;
    final private BigDecimal saldo;
    final private Long clienteId;
    final private LocalDateTime fechaApertura;
    final private Long version;

    public CuentaResponseDTO(Long id, Long numeroCuenta, TipoCuenta tipoCuenta, BigDecimal saldo, Long clienteId, LocalDateTime fechaApertura, Long version) {
        this.id = id;
        this.numeroCuenta = numeroCuenta;
        this.tipoCuenta = tipoCuenta;
        this.saldo = saldo;
        this.clienteId = clienteId;
        this.fechaApertura = fechaApertura;
        this.version = version;
    }
    
    public Long getId() {
        return id;
    }

    public Long getNumeroCuenta() {
        return numeroCuenta;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public Long getCliente() {
        return clienteId;
    }

    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }

    public Long getVersion() {
        return version;
    }
    
    
}
