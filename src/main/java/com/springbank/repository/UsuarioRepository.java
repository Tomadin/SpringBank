
package com.springbank.repository;

import com.springbank.entity.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
   
    @Query("SELECT u FROM usuarios u WHERE u.username =  :username")
    List<Usuario> findByUsername(@Param("username") String username);
            
}
