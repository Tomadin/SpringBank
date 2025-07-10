package com.springbank.service;

import com.springbank.dto.Request.LoginRequestDTO;
import com.springbank.dto.Request.RegistrarRequestDTO;
import com.springbank.dto.Request.UsuarioRequestDTO;
import com.springbank.dto.Response.TokenResponseDTO;
import com.springbank.entity.Token;
import com.springbank.entity.Usuario;
import com.springbank.enums.TokenTipoEnum;
import com.springbank.exception.TokenInvalidoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springbank.repository.TokenRepository;
import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private JwtService jwtService; //completar
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private AuthenticationManager authManager;

    public TokenResponseDTO registrar(UsuarioRequestDTO request) {
        Usuario usuario = usuarioService.crearUsuario(request);  //Crear JWTService y metodo generateToken(usuario);
        String tokenJwt = jwtService.generarToken(usuario);
        String refreshToken = jwtService.generarRefreshToken(usuario);
        saveUserToken(usuario, tokenJwt);
        return new TokenResponseDTO(tokenJwt, refreshToken);
    }

    public TokenResponseDTO login(LoginRequestDTO request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword())
        );

        Usuario user = usuarioService.buscarPorUsername(request.getUsername());
        String jwtToken = jwtService.generarToken(user);
        String jwtRefreshTOken = jwtService.generarRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return new TokenResponseDTO(jwtToken, jwtRefreshTOken);
    }

    public TokenResponseDTO refreshToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("El header no contiene un Bearer Token válido.");
        }

        final String refreshToken = authHeader.substring(7);
        final String username = jwtService.traerUsername(refreshToken);

        if (username == null) {
            throw new TokenInvalidoException("El Refresh Token no contiene username.");
        }

        final Usuario user = usuarioService.buscarPorUsername(username);

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new TokenInvalidoException("El refresh token está expirado o es inválido.");
        }

        final String accessToken = jwtService.generarToken(user);
        revokeAllUserTokens(user);             // marcamos todos los tokens como expirados o revocados
        saveUserToken(user, accessToken);   // guardamos el nuevo token

        return new TokenResponseDTO(accessToken, refreshToken); //usamos el mismo refresh token
    }

    @Transactional
    private void saveUserToken(Usuario user, String jwtToken) {
        Token token = new Token(jwtToken, TokenTipoEnum.BEARER, false, false, user);
        tokenRepository.save(token);
    }

    @Transactional
    private void revokeAllUserTokens(Usuario user) {
        final List<Token> validUserTokens = tokenRepository.findValidOrNotRevokedTokensByUserId(user.getId());
        if (!validUserTokens.isEmpty()) {
            for (Token token : validUserTokens) {
                token.setExpired(true);
                token.setRevoked(true);
            }
            tokenRepository.saveAll(validUserTokens);
        }
    }

}
