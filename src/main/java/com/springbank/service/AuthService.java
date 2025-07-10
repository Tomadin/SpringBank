
package com.springbank.service;

import com.springbank.dto.Request.LoginRequestDTO;
import com.springbank.dto.Request.RegistrarRequestDTO;
import com.springbank.dto.Request.UsuarioRequestDTO;
import com.springbank.dto.Response.TokenResponseDTO;
import com.springbank.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    UsuarioService usuarioService;

    public TokenResponseDTO registrar(UsuarioRequestDTO request) {
        Usuario usuario = usuarioService.crearUsuario(request);  //Crear JWTService y metodo generateToken(usuario);
        //tokenJwt = 
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public TokenResponseDTO login(LoginRequestDTO request) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public TokenResponseDTO refreshToken(String authHeader) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
    
}
