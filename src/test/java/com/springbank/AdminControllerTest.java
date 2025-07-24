package com.springbank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbank.controller.AdminController;
import com.springbank.dto.Response.ClienteResponseDTO;
import com.springbank.dto.Response.CuentaResponseDTO;
import com.springbank.dto.Response.TransaccionResponseDTO;
import com.springbank.dto.Response.UsuarioResponseDTO;
import com.springbank.enums.EstadoTransaccion;
import com.springbank.enums.RolUsuario;
import com.springbank.enums.TipoCuenta;
import com.springbank.enums.TipoTransaccion;
import com.springbank.repository.TokenRepository;
import com.springbank.repository.UsuarioRepository;
import com.springbank.service.ClienteService;
import com.springbank.service.CuentaService;
import com.springbank.service.JwtService;
import com.springbank.service.TransaccionService;
import com.springbank.service.UsuarioService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(AdminController.class)
@Import(AdminController.class)
@AutoConfigureMockMvc
public class AdminControllerTest {

    @MockitoBean
    private ClienteService clienteService;
    @MockitoBean
    private TransaccionService transaccionService;
    @MockitoBean
    private CuentaService cuentaService;
    @MockitoBean
    private UsuarioService usuarioService;

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

    public AdminControllerTest() {
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
    @WithMockUser(roles = "ADMIN")
    void verClientes_shouldReturnListOfClientes() throws Exception {
        UsuarioResponseDTO usuario1 = new UsuarioResponseDTO(1L, "juan123", RolUsuario.ADMIN, 2L);
        List<Long> cuentas1 = List.of(1001L, 1002L);

        List<ClienteResponseDTO> clientes = List.of(new ClienteResponseDTO(1L, "Juan", "Perez", usuario1, cuentas1));
        when(clienteService.obtenerTodos()).thenReturn(clientes);

        mockMvc.perform(get("/api/v1/admin/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan"))
                .andExpect(jsonPath("$[0].apellido").value("Perez"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void verClientePorDni_shouldReturnCliente() throws Exception {
        String dni = "12345678";
        UsuarioResponseDTO usuario = new UsuarioResponseDTO(1L, "juan123", RolUsuario.ADMIN, 2L);
        List<Long> cuentas = List.of(1001L, 1002L);
        ClienteResponseDTO cliente = new ClienteResponseDTO(1L, "Juan", "Perez", usuario, cuentas);
        when(clienteService.obtenerClientePorDni(dni)).thenReturn(cliente);

        mockMvc.perform(get("/api/v1/admin/clientes/dni/" + dni))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Perez"))
                .andExpect(jsonPath("$.usuario.username").value("juan123"))
                .andExpect(jsonPath("$.usuario.rol").value("ADMIN"))
                .andExpect(jsonPath("$.cuentasId[0]").value(1001));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void verTransacciones_shouldReturnList() throws Exception {

        Long numeroCuentaOrigen = 12345L;
        Long numeroCuentaDestino = 67891L;

        List<TransaccionResponseDTO> transacciones = List.of(new TransaccionResponseDTO(1L,
                new BigDecimal("500.0"), TipoTransaccion.TRANSFERENCIA, 1L,
                2L, EstadoTransaccion.ACTIVA, "Transferencia", LocalDateTime.MIN,
                numeroCuentaOrigen, numeroCuentaDestino));
        when(transaccionService.obtenerTodos()).thenReturn(transacciones);

        mockMvc.perform(get("/api/v1/admin/transacciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].monto").value(500.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void verCuentas_shouldReturnList() throws Exception {

        Long numeroCuentaOrigen = 12345L;

        List<CuentaResponseDTO> cuentas = List.of(new CuentaResponseDTO(1L, numeroCuentaOrigen,
                TipoCuenta.CORRIENTE, BigDecimal.ZERO,
                1L, LocalDateTime.MAX, 1L));
        when(cuentaService.obtenerTodos()).thenReturn(cuentas);

        mockMvc.perform(get("/api/v1/admin/cuentas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoCuenta").value("CORRIENTE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void verCuentaPorNumero_shouldReturnCuenta() throws Exception {
        Long numeroCuenta = 123456L;
        CuentaResponseDTO cuenta = new CuentaResponseDTO(1L, numeroCuenta, TipoCuenta.CORRIENTE,
                BigDecimal.ZERO, 1L, LocalDateTime.MAX, 1L);
        when(cuentaService.buscarPorNumeroCuenta(numeroCuenta)).thenReturn(cuenta);

        mockMvc.perform(get("/api/v1/admin/cuentas/" + numeroCuenta))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoCuenta").value("CORRIENTE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void verUsuarios_shouldReturnList() throws Exception {
        List<UsuarioResponseDTO> usuarios = List.of(new UsuarioResponseDTO(1L, "admin", RolUsuario.ADMIN, 1L));
        when(usuarioService.obtenerTodos()).thenReturn(usuarios);

        mockMvc.perform(get("/api/v1/admin/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void verUsuarioPorUsername_shouldReturnUsuario() throws Exception {
        String username = "jadmin";
        UsuarioResponseDTO usuario = new UsuarioResponseDTO(1L, username, RolUsuario.ADMIN, 1L);
        when(usuarioService.buscarPorUsernameDTO(username)).thenReturn(usuario);

        mockMvc.perform(get("/api/v1/admin/usuarios/username/" + username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username));
    }
}
