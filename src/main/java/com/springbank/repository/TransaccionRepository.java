package com.springbank.repository;

import com.springbank.entity.Transaccion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    @Query("SELECT t FROM Transaccion t WHERE t.cuentaOrigen.numeroCuenta = :numeroCuenta ORDER BY t.fecha DESC")
    List<Transaccion> obtenerHistorialPorNumeroCuenta(@Param("numeroCuenta") Long numeroCuenta);
}
