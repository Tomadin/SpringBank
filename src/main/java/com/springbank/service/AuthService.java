package com.springbank.service;

import com.springbank.dto.Request.ClienteRequestDTO;
import com.springbank.dto.Request.LoginRequestDTO;
import com.springbank.dto.Request.RegistrarRequestDTO;
import com.springbank.dto.Request.UsuarioClienteRequestDTO;
import com.springbank.dto.Request.UsuarioRequestDTO;
import com.springbank.dto.Response.ClienteResponseDTO;
import com.springbank.dto.Response.TokenResponseDTO;
import com.springbank.entity.Token;
import com.springbank.entity.Usuario;
import com.springbank.enums.RolUsuario;
import com.springbank.enums.TokenTipoEnum;
import com.springbank.exception.TokenInvalidoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springbank.repository.TokenRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ClienteService clienteService;
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
    
    public TokenResponseDTO registrarClienteUsuario(UsuarioClienteRequestDTO request) {
        //Creamos el Cliente
        ClienteRequestDTO clienteRequestDTO = new ClienteRequestDTO(request.getNombre(),
                request.getApellido(), request.getEmail(), request.getDni());
        clienteService.crearCliente(clienteRequestDTO);
        //Obtenemos la respuesta
        ClienteResponseDTO clienteCreadoDTo = clienteService.obtenerClientePorDni(request.getDni());

        // Creamos el usuario y asignamos el id del cliente que creamos anteriormente.
        UsuarioRequestDTO userDTO = new UsuarioRequestDTO(request.getUsername(), request.getPassword(), request.getRol(), clienteCreadoDTo.getId()); 
        Usuario usuario = usuarioService.crearUsuario(userDTO);  //Crear JWTService y metodo generateToken(usuario);
        String tokenJwt = jwtService.generarToken(usuario);
        String refreshToken = jwtService.generarRefreshToken(usuario);
        saveUserToken(usuario, tokenJwt);
        return new TokenResponseDTO(tokenJwt, refreshToken);
    }
    
    
    public TokenResponseDTO login(LoginRequestDTO request) {
        log.info("Intento de login para usuario: {}", request.getUsername());
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

        final String newRefreshToken = jwtService.generarRefreshToken(user);
        return new TokenResponseDTO(accessToken, newRefreshToken);
    }

    @Transactional
    private void saveUserToken(Usuario user, String jwtToken) {
        Token token = new Token(jwtToken, TokenTipoEnum.BEARER, false, false, user);
        tokenRepository.save(token);
    }

    @Transactional
    private void revokeAllUserTokens(Usuario user) {
        final List<Token> validUserTokens = tokenRepository.findValidTokensByUserId(user.getId());
        if (!validUserTokens.isEmpty()) {
            for (Token token : validUserTokens) {
                token.setExpired(true);
                token.setRevoked(true);
            }
            tokenRepository.saveAll(validUserTokens);
        }
    }

}
