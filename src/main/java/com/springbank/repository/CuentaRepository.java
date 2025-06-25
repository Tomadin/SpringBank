
package com.springbank.repository;

import com.springbank.entity.Cuenta;
import com.springbank.entity.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    @Query("SELECT c FROM cuentas c WHERE c.cliente_id =  :cliente_id")
    List<Usuario> findByDni(@Param("cliente_id") Long cliente_id);
    
    @Query("SELECT c FROM cuentas c WHERE c.numeroCuenta = :numeroCuenta")
    List<Usuario> findByEmail(@Param("numeroCuenta") Long numeroCuenta);
}
