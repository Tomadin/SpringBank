package com.springbank.service;

import com.springbank.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    @Value("${jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String generarToken(Usuario usuario) {
        return construirToken(usuario, jwtExpiration);
    }

    String generarRefreshToken(Usuario usuario) {
        return construirToken(usuario, refreshExpiration);
    }

    private String construirToken(final Usuario user, final long expiration) {
        return Jwts.builder()
                .setId(user.getId().toString()) //Define el JWT ID, es opcional 
                .setClaims(Map.of("name", user.getCliente().getNombre()))
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes); //Utiliza el algoritmo hmac Sha, esto nos dara una Secret Key
    }

    public String traerUsername(String token) {
        final Claims jwtToken = Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) 
                .build()
                .parseClaimsJws(token)
                .getBody();
        return jwtToken.getSubject();
    }

    public boolean isTokenValid(String token, Usuario user) {
        final String username = traerUsername(token);
        return (username.equals(user.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return traerExpiracion(token).before(new Date());
    }

    private Date traerExpiracion(String token) {
        final Claims jwtToken = Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) 
                .build()
                .parseClaimsJws(token)
                .getBody();
        return jwtToken.getExpiration();
    }

}
