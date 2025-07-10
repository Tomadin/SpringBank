
package com.springbank.controller;

import com.springbank.dto.Request.LoginRequestDTO;
import com.springbank.dto.Request.RegistrarRequestDTO;
import com.springbank.dto.Request.UsuarioRequestDTO;
import com.springbank.dto.Response.TokenResponseDTO;
import com.springbank.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/registrar")
    public ResponseEntity<TokenResponseDTO> register(@RequestBody UsuarioRequestDTO request){ //crear atributos de TokenDTO, RegistroRequestDTO y el service de authService
        final TokenResponseDTO token = authService.registrar(request);
        return ResponseEntity.ok(token);
    }
    
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginRequestDTO request){
        final TokenResponseDTO token = authService.login(request);
        return ResponseEntity.ok(token);
    }
    
    @PostMapping("/refresh")
    public TokenResponseDTO refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader){
        return authService.refreshToken(authHeader);
    }
    
}
