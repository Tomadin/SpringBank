
package com.springbank;

import com.springbank.config.SecurityConfig;
import com.springbank.controller.ClienteController;
import com.springbank.dto.Request.ClienteRequestDTO;
import com.springbank.service.ClienteService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbank.config.SecurityConfig;
import com.springbank.dto.Response.ClienteResponseDTO;
import com.springbank.dto.Response.UsuarioResponseDTO;
import com.springbank.enums.RolUsuario;
import com.springbank.exception.EmailInvalido;
import com.springbank.repository.TokenRepository;
import com.springbank.repository.UsuarioRepository;
import com.springbank.service.JwtService;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(ClienteController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
public class ClienteControllerTest {

    @MockitoBean
    private ClienteService clienteService;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TokenRepository tokenRepository;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public ClienteControllerTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    @WithMockUser(username = "admin", roles = {"CLIENTE"})
    void crearCliente_Correcto_Retorna201() throws Exception {
        ClienteRequestDTO request = new ClienteRequestDTO("Juan", "Pérez", "juan@test.com", "12345678");

        mockMvc.perform(post("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Cliente creado correctamente."));
    }

    @Test
    @WithMockUser(roles = {"CLIENTE"})
    void crearCliente_EmailInvalido_LanzaExcepcion() throws Exception {
        ClienteRequestDTO request = new ClienteRequestDTO("Juan", "Pérez", "email_invalido", "12345678");

        doThrow(new EmailInvalido("Email inválido")).when(clienteService).crearCliente(any());

        mockMvc.perform(post("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // Depende cómo manejes la excepción
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void traerClienteId_AdminAccedeCorrectamente() throws Exception {
        UsuarioResponseDTO usuario = new UsuarioResponseDTO(1L, "admin", RolUsuario.ADMIN, 1L);

        List<Long> cuentas = List.of(1001L, 1002L);
        ClienteResponseDTO response = new ClienteResponseDTO(1L, "Juan", "perez", usuario, cuentas);

        when(clienteService.obtenerClientePorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    @WithMockUser(username = "juan123", roles = {"CLIENTE"})
    void traerClienteId_ClienteAutorizado_Retorna200() throws Exception {
        UsuarioResponseDTO usuario = new UsuarioResponseDTO(1L, "juan123", RolUsuario.CLIENTE, 2L);

        List<Long> cuentas = List.of(1001L, 1002L);
        ClienteResponseDTO response = new ClienteResponseDTO(2L, "Juan", "Perez", usuario, cuentas);

        when(clienteService.obtenerClientePorId(2L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/clientes/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    @WithMockUser(username = "otroUsuario", roles = {"CLIENTE"})
    void traerClienteId_ClienteSinPermiso_Retorna403() throws Exception {
        UsuarioResponseDTO usuario = new UsuarioResponseDTO(1L, "juan123", RolUsuario.CLIENTE, 2L);

        List<Long> cuentas = List.of(1001L, 1002L);
        ClienteResponseDTO response = new ClienteResponseDTO(2L, "Juan", "Perez", usuario, cuentas);

        when(clienteService.obtenerClientePorId(2L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/clientes/2"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("No tienes permiso para acceder a este recurso."));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void traerTodos_Admin_RetornaListaClientes() throws Exception {
        UsuarioResponseDTO usuario1 = new UsuarioResponseDTO(1L, "juan123", RolUsuario.ADMIN, 2L);
        List<Long> cuentas1 = List.of(1001L, 1002L);
        ClienteResponseDTO cliente1 = new ClienteResponseDTO(1L, "Juan", "Perez", usuario1, cuentas1);

        UsuarioResponseDTO usuario2 = new UsuarioResponseDTO(2L, "ana123", RolUsuario.ADMIN, 3L);
        List<Long> cuentas2 = List.of(1003L, 1004L);
        ClienteResponseDTO cliente2 = new ClienteResponseDTO(3L, "Ana", "Martinez", usuario2, cuentas2);

        List<ClienteResponseDTO> clientes = List.of(cliente1, cliente2);

        when(clienteService.obtenerTodos()).thenReturn(clientes);

        mockMvc.perform(get("/api/v1/clientes/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Juan"))
                .andExpect(jsonPath("$[1].nombre").value("Ana"));
    }

    @Test
    @WithMockUser(roles = {"CLIENTE"})
    void traerTodos_SinPermiso_Retorna403() throws Exception {
        mockMvc.perform(get("/api/v1/clientes/all"))
                .andDo(print()) // para ver detalle en consola
                .andExpect(status().isForbidden());
    }

}
