package com.springbank.controller;

import com.springbank.dto.Request.LoginRequestDTO;
import com.springbank.dto.Request.RegistrarRequestDTO;
import com.springbank.dto.Request.UsuarioClienteRequestDTO;
import com.springbank.dto.Request.UsuarioRequestDTO;
import com.springbank.dto.Response.TokenResponseDTO;
import com.springbank.enums.RolUsuario;
import com.springbank.exception.DniInvalido;
import com.springbank.exception.EmailInvalido;
import com.springbank.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Value("${jwt.secret-key}")
    String secretKey;
    @Autowired
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Registrar Usuario",
            description = "Registra un nuevo usuario. Retorna un token JWT si el registro es exitoso."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado correctamente", content = @Content(schema = @Schema(implementation = TokenResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida (datos faltantes o inválidos)", content = @Content),
        @ApiResponse(responseCode = "409", description = "El usuario ya existe", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos necesarios para registrar al usuario y asignarlo a un cliente creado previamente.",
            required = true,
            content = @Content(schema = @Schema(implementation = UsuarioRequestDTO.class))
    )
    @PostMapping("/registrar-usuario")
    public ResponseEntity<TokenResponseDTO> register(@RequestBody UsuarioRequestDTO request) { //crear atributos de TokenDTO, RegistroRequestDTO y el service de authService
        final TokenResponseDTO token = authService.registrar(request);
        return ResponseEntity.ok(token);
    }
    
    @Operation(
            summary = "Registrar Cliente con su Usuario",
            description = "Registra un nuevo Cliente y asignarle un nuevo usuario. Retorna un token JWT si el registro es exitoso."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cliente y Usuario creados correctamente", content = @Content(schema = @Schema(implementation = TokenResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida (datos faltantes o inválidos)", content = @Content),
        @ApiResponse(responseCode = "409", description = "El usuario ya existe", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos necesarios para registrar al usuario y asignarlo a un cliente creado previamente.",
            required = true,
            content = @Content(schema = @Schema(implementation = UsuarioClienteRequestDTO.class))
    )
    @PostMapping("/registrar-cliente-usuario")
    public ResponseEntity<TokenResponseDTO> registerClientAndUser(@RequestBody UsuarioClienteRequestDTO request) throws DniInvalido, EmailInvalido { //crear atributos de TokenDTO, RegistroRequestDTO y el service de authService
        final TokenResponseDTO token = authService.registrarClienteUsuario(request);
        return ResponseEntity.ok(token);
    }

    @Operation(
            summary = "Registrar de Usuario",
            description = "Registra un nuevo usuario. Retorna un token JWT si el registro es exitoso."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso", content = @Content(schema = @Schema(implementation = TokenResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas.", content = @Content),})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Credenciales del usuario.",
            required = true,
            content = @Content(schema = @Schema(implementation = LoginRequestDTO.class))
    )
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginRequestDTO request) {
        final TokenResponseDTO token = authService.login(request);
        return ResponseEntity.ok(token);
    }

    @Operation(
            summary = "Refrescar token JWT",
            description = "Obtiene un nuevo token JWT a partir de un token válido que está por expirar."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token renovado", content = @Content(schema = @Schema(implementation = TokenResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Token inválido o expirado", content = @Content)
    })
    @PostMapping("/refresh")
    public TokenResponseDTO refreshToken(@Parameter(
            description = "Encabezado con el token Bearer",
            required = true,
            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    ) @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        return authService.refreshToken(authHeader);
    }

}
