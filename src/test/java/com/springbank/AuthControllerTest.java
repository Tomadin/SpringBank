
package com.springbank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbank.config.SecurityConfig;
import com.springbank.controller.AuthController;
import com.springbank.dto.Request.LoginRequestDTO;
import com.springbank.dto.Request.UsuarioRequestDTO;
import com.springbank.dto.Response.TokenResponseDTO;
import com.springbank.enums.RolUsuario;
import com.springbank.repository.TokenRepository;
import com.springbank.repository.UsuarioRepository;
import com.springbank.service.AuthService;
import com.springbank.service.CuentaService;
import com.springbank.service.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CuentaService cuentaService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private TokenRepository tokenRepository;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;
    
    private TokenResponseDTO tokenResponse;
    
    
    public AuthControllerTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
        tokenResponse = new TokenResponseDTO("18e4539b4f903983f173a061c0c0ca43a5472f9ee7c8726262d8b69ba2ed9562dafa1ede8b7554b42896591009b7b96d", "b48b805aebd85266aabeedb511bfbec0f84c007df4307999fc711ddde45bb7212aad06ee5e4f61a5f652b82427124d0f");
    }
    
    @AfterEach
    public void tearDown() {
    }

    @Test
    void register_ValidRequest_200() throws Exception {
        UsuarioRequestDTO request = new UsuarioRequestDTO("usuarioNuevo", "123456", RolUsuario.CLIENTE, 1L);
        when(authService.registrar(any(UsuarioRequestDTO.class))).thenReturn(tokenResponse);

        mockMvc.perform(post("/api/v1/auth/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(tokenResponse.getToken()));
    }

    @Test
    void login_ValidRequest_200() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("cliente", "cliente123");
        when(authService.login(any(LoginRequestDTO.class))).thenReturn(tokenResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(tokenResponse.getToken()));
    }

    @Test
    void refresh_ValidHeader_200() throws Exception {
        when(authService.refreshToken("Bearer valid-token")).thenReturn(tokenResponse);

        mockMvc.perform(post("/api/v1/auth/refresh")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(tokenResponse.getToken()));
    }
}
