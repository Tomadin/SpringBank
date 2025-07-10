package com.springbank.repository;

import com.springbank.entity.Token;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("SELECT t FROM tokens t WHERE t.usuario.id = :userId AND (t.expired = false OR t.revoked = false)")
    List<Token> findValidOrNotRevokedTokensByUserId(@Param("userId") Long userId);

}
