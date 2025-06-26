
package com.springbank.repository;

import com.springbank.entity.Cuenta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    @Query("SELECT c FROM Cuenta c WHERE c.cliente.id = :cliente_id")
    List<Cuenta> findByClienteId(@Param("cliente_id") Long cliente_id);
    
    @Query("SELECT c FROM Cuenta c WHERE c.numeroCuenta = :numeroCuenta")
    List<Cuenta> numeroCuenta(@Param("numeroCuenta") Long numeroCuenta);
}
