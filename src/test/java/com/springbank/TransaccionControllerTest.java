package com.springbank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbank.config.SecurityConfig;
import com.springbank.controller.TransaccionController;
import com.springbank.dto.Request.TransaccionRequestDTO;
import com.springbank.enums.TipoTransaccion;
import com.springbank.repository.TokenRepository;
import com.springbank.repository.UsuarioRepository;
import com.springbank.service.JwtService;
import com.springbank.service.TransaccionService;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(TransaccionController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class TransaccionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransaccionService transaccionService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;
    @MockitoBean
    private TokenRepository tokenRepository;
    @MockitoBean
    private JwtService jwtService;

    // Usamos ObjectMapper para convertir objetos a JSON y viceversa
    @Autowired
    private ObjectMapper objectMapper;

    public TransaccionControllerTest() {
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

    // Test para POST /depositar - éxito
    @Test
    @WithMockUser(username = "usuarioTest", roles = {"CLIENTE"})
    void depositar_Correcto_Retorna201() throws Exception {
        TransaccionRequestDTO dto = new TransaccionRequestDTO(new BigDecimal("1000"), TipoTransaccion.DEPOSITO, 111L, 123L, "prueba");

        // Cuando se llama realizarDeposito no hace nada (void)
        doNothing().when(transaccionService).realizarDeposito(dto.getMonto(), dto.getNumeroCuentaDestino());

        mockMvc.perform(post("/api/v1/transacciones/depositar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Depósito realizado correctamente."));
    }

    // Test para POST /depositar - tipo inválido (debe lanzar excepción)
    @Test
    @WithMockUser(username = "usuarioTest", roles = {"CLIENTE"})
    void depositar_TipoIncorrecto_RetornaError() throws Exception {
        TransaccionRequestDTO dto = new TransaccionRequestDTO(new BigDecimal("1000"), TipoTransaccion.RETIRO, 111L, 123L, "prueba");

        mockMvc.perform(post("/api/v1/transacciones/depositar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

    }

    // Test para POST /retirar - éxito
    @Test
    @WithMockUser(username = "usuarioTest", roles = {"CLIENTE"})
    void retirar_Correcto_Retorna201() throws Exception {
        TransaccionRequestDTO dto = new TransaccionRequestDTO(new BigDecimal("1000"), TipoTransaccion.RETIRO, 111L, 123L, "prueba");

        // Simular que la cuenta pertenece al usuario
        when(transaccionService.perteneceCuentaAUsuario(111L, "usuarioTest")).thenReturn(true);
        doNothing().when(transaccionService).realizarRetiro(dto.getMonto(), dto.getNumeroCuentaOrigen());

        mockMvc.perform(post("/api/v1/transacciones/retirar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Retiro realizado correctamente."));
    }

    // Test para POST /retirar - usuario no propietario -> forbidden
    @Test
    @WithMockUser(username = "usuarioTest", roles = {"CLIENTE"})
    void retirar_NoEsPropietario_Retorna403() throws Exception {
        TransaccionRequestDTO dto = new TransaccionRequestDTO(new BigDecimal("500"), TipoTransaccion.RETIRO, 111L, 123L, "prueba");

        when(transaccionService.perteneceCuentaAUsuario(123L, "usuarioTest")).thenReturn(false);

        mockMvc.perform(post("/api/v1/transacciones/retirar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("No tienes permiso para operar con esta cuenta."));
    }

    // Test para POST /transferir - éxito
    @Test
    @WithMockUser(username = "usuarioTest", roles = {"CLIENTE"})
    void transferir_Correcto_Retorna201() throws Exception {
        TransaccionRequestDTO dto = new TransaccionRequestDTO(new BigDecimal("700"), TipoTransaccion.TRANSFERENCIA, 123L, 456L, "prueba");

        when(transaccionService.perteneceCuentaAUsuario(123L, "usuarioTest")).thenReturn(true);
        doNothing().when(transaccionService).realizarTransferencia(dto.getMonto(), dto.getNumeroCuentaOrigen(), dto.getNumeroCuentaDestino());

        mockMvc.perform(post("/api/v1/transacciones/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Transferencia realizada correctamente."));
    }

    // Test para POST /transferir - tipo incorrecto (debe lanzar excepción)
    @Test
    @WithMockUser(username = "usuarioTest", roles = {"CLIENTE"})
    void transferir_TipoIncorrecto_RetornaError() throws Exception {
        TransaccionRequestDTO dto = new TransaccionRequestDTO(new BigDecimal("700"), TipoTransaccion.RETIRO, 123L, 456L, "prueba");

        mockMvc.perform(post("/api/v1/transacciones/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // Test para POST /transferir - usuario no propietario -> forbidden
    @Test
    @WithMockUser(username = "usuarioTest", roles = {"CLIENTE"})
    void transferir_NoEsPropietario_Retorna403() throws Exception {
        TransaccionRequestDTO dto = new TransaccionRequestDTO(new BigDecimal("700"), TipoTransaccion.TRANSFERENCIA, 123L, 456L, "prueba");

        when(transaccionService.perteneceCuentaAUsuario(123L, "usuarioTest")).thenReturn(false);

        mockMvc.perform(post("/api/v1/transacciones/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("No tienes permiso para operar con esta cuenta."));
    }
}
