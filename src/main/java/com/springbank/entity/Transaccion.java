
package com.springbank.entity;

import com.springbank.enums.EstadoTransaccion;
import com.springbank.enums.TipoTransaccion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "transacciones")
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monto = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING)
    private TipoTransaccion tipo; //DEPOSITO, RETIRO, TRANSFERENCIA
    @ManyToOne
    @JoinColumn(name = "cuenta_origen_id")
    private Cuenta cuentaOrigen;
    @ManyToOne
    @JoinColumn(name = "cuenta_destino_id")
    private Cuenta cuentaDestino;
    @Enumerated(EnumType.STRING)
    private EstadoTransaccion estado = EstadoTransaccion.ACTIVA; //ACTIVA, COMPLETADA, FALLIDA, ABORTADA
    private String descripcion;
    @CreationTimestamp
    private LocalDateTime fecha;

    public Transaccion() {
    }

    public Transaccion(TipoTransaccion tipo, Cuenta cuentaOrigen, Cuenta cuentaDestino, String descripcion, LocalDateTime fecha) {
        this.tipo = tipo;
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransaccion tipo) {
        this.tipo = tipo;
    }

    public Cuenta getCuentaOrigen() {
        return cuentaOrigen;
    }

    public void setCuentaOrigen(Cuenta cuentaOrigen) {
        this.cuentaOrigen = cuentaOrigen;
    }

    public Cuenta getCuentaDestino() {
        return cuentaDestino;
    }

    public void setCuentaDestino(Cuenta cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    public EstadoTransaccion getEstado() {
        return estado;
    }

    public void setEstado(EstadoTransaccion estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Transaccion{" + "id=" + id + ", monto=" + monto + ", tipo=" + tipo + ", cuentaOrigen=" + cuentaOrigen + ", cuentaDestino=" + cuentaDestino + ", estado=" + estado + ", descripcion=" + descripcion + ", fecha=" + fecha + '}';
    }
    
    
    
}
