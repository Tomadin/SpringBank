package com.springbank.service;

import com.springbank.dto.Request.LoginRequestDTO;
import com.springbank.dto.Request.RegistrarRequestDTO;
import com.springbank.dto.Request.UsuarioRequestDTO;
import com.springbank.dto.Response.TokenResponseDTO;
import com.springbank.entity.Token;
import com.springbank.entity.Usuario;
import com.springbank.enums.TokenTipoEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springbank.repository.TokenRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private JwtService jwtService; //completar
    @Autowired
    private TokenRepository tokenRepository;

    public TokenResponseDTO registrar(UsuarioRequestDTO request) {
        Usuario usuario = usuarioService.crearUsuario(request);  //Crear JWTService y metodo generateToken(usuario);
        String tokenJwt = jwtService.generarToken(usuario);
        String refreshToken = jwtService.generarRefreshToken(usuario);
        saveUserToken(usuario, tokenJwt);
        return new TokenResponseDTO(tokenJwt, refreshToken);
    }

    public TokenResponseDTO login(LoginRequestDTO request) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public TokenResponseDTO refreshToken(String authHeader) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Transactional
    private void saveUserToken(Usuario user, String jwtToken) {
        Token token = new Token(jwtToken, TokenTipoEnum.BEARER, false, false, user);
        tokenRepository.save(token);
    }

}
