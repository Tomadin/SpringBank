
package com.springbank.entity;

import com.springbank.enums.TipoCuenta;
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
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "cuentas")
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable= false)
    private String numeroCuenta;
    @Enumerated(EnumType.STRING)
    private TipoCuenta tipoCuenta; // AHORRO, CORRIENTE
    @Column(nullable = false, precision = 15, scale= 2) //Precision= numero total de numeros que puede tener el valor, scale= numeros despues de la coma
    private BigDecimal saldo = BigDecimal.ZERO; 
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    @CreationTimestamp
    private LocalDateTime fechaApertura;
    @Version
    private Long version; // Para optimistic locking

    public Cuenta() {
    }

    public Cuenta(String numeroCuenta, TipoCuenta tipoCuenta, Cliente cliente, LocalDateTime fechaApertura, Long version) {
        this.numeroCuenta = numeroCuenta;
        this.tipoCuenta = tipoCuenta;
        this.cliente = cliente;
        this.fechaApertura = fechaApertura;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDateTime fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Cuenta{" + "id=" + id + ", numeroCuenta=" + numeroCuenta + ", tipoCuenta=" + tipoCuenta + ", saldo=" + saldo + ", cliente=" + cliente + ", fechaApertura=" + fechaApertura + ", version=" + version + '}';
    }
    
    
}
