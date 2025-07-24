/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.springbank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbank.config.SecurityConfig;
import com.springbank.controller.UsuarioController;
import com.springbank.dto.Response.UsuarioResponseDTO;
import com.springbank.enums.RolUsuario;
import com.springbank.repository.TokenRepository;
import com.springbank.repository.UsuarioRepository;
import com.springbank.service.CuentaService;
import com.springbank.service.JwtService;
import com.springbank.service.UsuarioService;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UsuarioController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CuentaService cuentaService;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private TokenRepository tokenRepository;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioResponseDTO usuarioAdmin;
    private UsuarioResponseDTO usuarioCliente;

    public UsuarioControllerTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        usuarioAdmin = new UsuarioResponseDTO(1L, "admin", RolUsuario.ADMIN, 1L);
        usuarioCliente = new UsuarioResponseDTO(2L, "cliente", RolUsuario.CLIENTE, 2L);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void traerTodos_Admin_200() throws Exception {
        List<UsuarioResponseDTO> usuarios = List.of(usuarioAdmin, usuarioCliente);

        when(usuarioService.traerTodos()).thenReturn(usuarios);

        mockMvc.perform(get("/api/v1/usuarios/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[1].username").value("cliente"));
    }

    @Test
    @WithMockUser(username = "cliente", roles = {"CLIENTE"})
    void traerUsuario_Cliente_SuPropioPerfil_200() throws Exception {
        when(usuarioService.buscarPorUsernameDTO("cliente")).thenReturn(usuarioCliente);

        mockMvc.perform(get("/api/v1/usuarios/cliente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("cliente"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }

    @Test
    @WithMockUser(username = "otroCliente", roles = {"CLIENTE"})
    void traerUsuario_Cliente_OtroPerfil_403() throws Exception {
        // Aunque el servicio lo devuelva, el PreAuthorize lo bloquea antes
        when(usuarioService.buscarPorUsernameDTO("cliente")).thenReturn(usuarioCliente);

        mockMvc.perform(get("/api/v1/usuarios/cliente"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void traerUsuario_Admin_CualquierPerfil_200() throws Exception {
        when(usuarioService.buscarPorUsernameDTO("cliente")).thenReturn(usuarioCliente);

        mockMvc.perform(get("/api/v1/usuarios/cliente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("cliente"));
    }

}
