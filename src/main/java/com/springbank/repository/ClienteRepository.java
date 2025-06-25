
package com.springbank.repository;

import com.springbank.entity.Cliente;
import com.springbank.entity.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepositoryImplementation<Cliente, Long> {
    @Query("SELECT c FROM clientes c WHERE c.dni =  :dni")
    List<Usuario> findByDni(@Param("dni") String dni);
    
    @Query("SELECT c FROM clientes c WHERE c.email = :email")
    List<Usuario> findByEmail(@Param("email") String email);
    
}
