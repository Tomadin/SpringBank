
package com.springbank;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbank.config.SecurityConfig;
import com.springbank.controller.CuentaController;
import com.springbank.dto.Request.CuentaRequestDTO;
import com.springbank.dto.Response.ClienteResponseDTO;
import com.springbank.dto.Response.CuentaResponseDTO;
import com.springbank.dto.Response.SaldoResponseDTO;
import com.springbank.dto.Response.TransaccionResponseDTO;
import com.springbank.dto.Response.UsuarioResponseDTO;
import com.springbank.enums.EstadoTransaccion;
import com.springbank.enums.RolUsuario;
import com.springbank.enums.TipoCuenta;
import com.springbank.enums.TipoTransaccion;
import com.springbank.repository.TokenRepository;
import com.springbank.repository.UsuarioRepository;
import com.springbank.service.CuentaService;
import com.springbank.service.JwtService;
import com.springbank.service.UsuarioService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(CuentaController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
public class CuentaControllerTest {
    
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
    
    
    public CuentaControllerTest() {
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

    // --- Tests para POST /api/v1/cuentas ---

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void crearCuenta_Admin_CuentaCreada201() throws Exception {
        CuentaRequestDTO request = new CuentaRequestDTO(TipoCuenta.CORRIENTE, 1L);

        
        mockMvc.perform(post("/api/v1/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(content().string("Cuenta creado correctamente."));
        
        verify(cuentaService).crearCuenta(any());
    }

    @Test
    @WithMockUser(username = "cliente1", roles = {"CLIENTE"})
    void crearCuenta_Cliente_ConMismoClienteId_201() throws Exception {
        CuentaRequestDTO request = new CuentaRequestDTO(TipoCuenta.CORRIENTE, 1L);

        UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO(1L, "cliente1", RolUsuario.CLIENTE, 1L);
        when(usuarioService.buscarPorUsernameDTO("cliente1")).thenReturn(usuarioDTO);

        mockMvc.perform(post("/api/v1/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(content().string("Cuenta creado correctamente."));
        
        verify(cuentaService).crearCuenta(any());
    }

    @Test
    @WithMockUser(username = "cliente2", roles = {"CLIENTE"})
    void crearCuenta_Cliente_ConClienteIdDistinto_403() throws Exception {
        CuentaRequestDTO request = new CuentaRequestDTO(TipoCuenta.CORRIENTE, 99L);

        // Cliente autenticado tiene clienteId distinto
        UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO(2L, "cliente2", RolUsuario.CLIENTE, 1L);
        when(usuarioService.buscarPorUsernameDTO("cliente2")).thenReturn(usuarioDTO);

        mockMvc.perform(post("/api/v1/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden())
            .andExpect(content().string("No puedes crear cuentas para otro cliente."));

        verify(cuentaService, never()).crearCuenta(any());
    }

    // --- Tests para GET /api/v1/cuentas/{numeroCuenta}/saldo ---

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void traerSaldo_Admin_ConCuentaExistente_200() throws Exception {
        Long numeroCuenta = 12345L;

        ClienteResponseDTO cliente = new ClienteResponseDTO(10L, "Juan", "Perez", null, List.of());
        CuentaResponseDTO cuentaResponse = new CuentaResponseDTO(1L, numeroCuenta, TipoCuenta.CORRIENTE, BigDecimal.ZERO, 10L, LocalDateTime.MAX, 1L);
        SaldoResponseDTO saldoResponse = new SaldoResponseDTO(numeroCuenta, BigDecimal.valueOf(5000L));

        when(cuentaService.buscarPorNumeroCuenta(numeroCuenta)).thenReturn(cuentaResponse);
        when(cuentaService.esPropietarioOAdmin(cliente.getId(), "admin", true)).thenReturn(true);
        when(cuentaService.obtenerSaldo(numeroCuenta)).thenReturn(saldoResponse);

        mockMvc.perform(get("/api/v1/cuentas/{numeroCuenta}/saldo", numeroCuenta))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.saldo").value(5000.0));
    }

    @Test
    @WithMockUser(username = "cliente1", roles = {"CLIENTE"})
    void traerSaldo_ClienteNoPropietario_403() throws Exception {
        Long numeroCuenta = 12345L;

        ClienteResponseDTO cliente = new ClienteResponseDTO(10L, "Juan", "Perez", null, List.of());
        CuentaResponseDTO cuentaResponse = new CuentaResponseDTO(1L, numeroCuenta, TipoCuenta.CORRIENTE, BigDecimal.ZERO, 10L, LocalDateTime.MAX, 1L);
        

        when(cuentaService.buscarPorNumeroCuenta(numeroCuenta)).thenReturn(cuentaResponse);
        when(cuentaService.esPropietarioOAdmin(cliente.getId(), "cliente1", false)).thenReturn(false);

        mockMvc.perform(get("/api/v1/cuentas/{numeroCuenta}/saldo", numeroCuenta))
            .andExpect(status().isForbidden())
            .andExpect(content().string("No tienes permiso para ver el saldo de esta cuenta."));
    }

    @Test
    @WithMockUser(roles = {"CLIENTE"})
    void traerSaldo_CuentaNoExiste_404() throws Exception {
        Long numeroCuenta = 99999L;
        when(cuentaService.buscarPorNumeroCuenta(numeroCuenta)).thenReturn(null);

        mockMvc.perform(get("/api/v1/cuentas/{numeroCuenta}/saldo", numeroCuenta))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Cuenta no encontrada."));
    }

    // --- Tests para GET /api/v1/cuentas/{numeroCuenta}/transacciones ---

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void traerTransacciones_Admin_ConCuentaExistente_200() throws Exception {
        Long numeroCuenta = 12345L;
        ClienteResponseDTO cliente = new ClienteResponseDTO(10L, "Juan", "Perez", null, List.of());
        CuentaResponseDTO cuentaResponse = new CuentaResponseDTO(1L, numeroCuenta, TipoCuenta.CORRIENTE, BigDecimal.ZERO, 10L, LocalDateTime.MAX, 1L);
        

        List<TransaccionResponseDTO> transacciones = List.of(
            new TransaccionResponseDTO(1L, BigDecimal.ONE, 
                    TipoTransaccion.RETIRO, numeroCuenta, numeroCuenta,
                    EstadoTransaccion.ACTIVA, "retiro", LocalDateTime.MIN, numeroCuenta,
                    numeroCuenta),
            new TransaccionResponseDTO(2L, BigDecimal.ONE, 
                    TipoTransaccion.TRANSFERENCIA, 32145L, numeroCuenta,
                    EstadoTransaccion.ACTIVA, "Transaccion", LocalDateTime.MIN, numeroCuenta,
                    numeroCuenta)
        );

        when(cuentaService.buscarPorNumeroCuenta(numeroCuenta)).thenReturn(cuentaResponse);
        when(cuentaService.esPropietarioOAdmin(cliente.getId(), "admin", true)).thenReturn(true);
        when(cuentaService.obtenerTransaccionesPorNumeroCuenta(numeroCuenta)).thenReturn(transacciones);

        mockMvc.perform(get("/api/v1/cuentas/{numeroCuenta}/transacciones", numeroCuenta))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(transacciones.size()));
    }

    @Test
    @WithMockUser(username = "cliente1", roles = {"CLIENTE"})
    void traerTransacciones_ClienteNoPropietario_403() throws Exception {
        Long numeroCuenta = 12345L;
        ClienteResponseDTO cliente = new ClienteResponseDTO(10L, "Juan", "Perez", null, List.of());
        CuentaResponseDTO cuentaResponse = new CuentaResponseDTO(1L, numeroCuenta, TipoCuenta.CORRIENTE, BigDecimal.ZERO, 10L, LocalDateTime.MAX, 1L);
        
        when(cuentaService.buscarPorNumeroCuenta(numeroCuenta)).thenReturn(cuentaResponse);
        when(cuentaService.esPropietarioOAdmin(cliente.getId(), "cliente1", false)).thenReturn(false);

        mockMvc.perform(get("/api/v1/cuentas/{numeroCuenta}/transacciones", numeroCuenta))
            .andExpect(status().isForbidden())
            .andExpect(content().string("No tienes permiso para ver las transacciones de esta cuenta."));
    }

    @Test
    @WithMockUser(roles = {"CLIENTE"})
    void traerTransacciones_CuentaNoExiste_404() throws Exception {
        Long numeroCuenta = 99999L;
        when(cuentaService.buscarPorNumeroCuenta(numeroCuenta)).thenReturn(null);

        mockMvc.perform(get("/api/v1/cuentas/{numeroCuenta}/transacciones", numeroCuenta))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Cuenta no encontrada."));
    }
}
