package com.springbank;

import com.springbank.dto.Request.CuentaRequestDTO;
import com.springbank.dto.Response.CuentaResponseDTO;
import com.springbank.dto.Response.SaldoResponseDTO;
import com.springbank.dto.Response.TransaccionResponseDTO;
import com.springbank.entity.Cliente;
import com.springbank.entity.Cuenta;
import com.springbank.entity.Usuario;
import com.springbank.enums.EstadoTransaccion;
import com.springbank.enums.TipoCuenta;
import com.springbank.enums.TipoTransaccion;
import com.springbank.exception.ClienteNoEncontrado;
import com.springbank.exception.CuentaInvalida;
import com.springbank.exception.CuentaNoEncontrada;
import com.springbank.exception.TipoCuentaException;
import com.springbank.repository.ClienteRepository;
import com.springbank.repository.CuentaRepository;
import com.springbank.service.CuentaService;
import com.springbank.service.TransaccionService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CuentaServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private TransaccionService transaccionService;

    @InjectMocks
    private CuentaService cuentaService;

    public CuentaServiceTest() {
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
    void crearCuenta_Success() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setCuentas(new ArrayList<>()); // Sin cuentas previas

        CuentaRequestDTO dto = new CuentaRequestDTO(TipoCuenta.AHORRO, 1L);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(cuentaRepository.save(any(Cuenta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cuenta cuentaCreada = cuentaService.crearCuenta(dto);

        assertEquals(TipoCuenta.AHORRO, cuentaCreada.getTipoCuenta());
        assertEquals(cliente, cuentaCreada.getCliente());
        assertNotNull(cuentaCreada.getNumeroCuenta());
        verify(cuentaRepository).save(any(Cuenta.class));
    }

    @Test
    void crearCuenta_TipoCuentaInvalido_LanzaExcepcion() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setCuentas(new ArrayList<>());

        CuentaRequestDTO dto = new CuentaRequestDTO(null, 1L); //tipo invalido

        TipoCuentaException ex = assertThrows(TipoCuentaException.class, () -> cuentaService.crearCuenta(dto));
        assertTrue(ex.getMessage().contains("invalido"));
    }

    @Test
    void crearCuenta_CuentaDuplicada_LanzaExcepcion() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        Cuenta cuentaExistente = new Cuenta();
        cuentaExistente.setTipoCuenta(TipoCuenta.AHORRO);
        cliente.setCuentas(List.of(cuentaExistente));

        CuentaRequestDTO dto = new CuentaRequestDTO(TipoCuenta.AHORRO, 1L); // Ya existe una cuenta AHORRO

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        CuentaInvalida ex = assertThrows(CuentaInvalida.class, () -> cuentaService.crearCuenta(dto));
        assertTrue(ex.getMessage().contains("ya posee una cuenta"));
    }

    @Test
    void crearCuenta_ClienteNoEncontrado_LanzaExcepcion() {
        CuentaRequestDTO dto = new CuentaRequestDTO(TipoCuenta.CORRIENTE, 99L);

        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        ClienteNoEncontrado ex = assertThrows(ClienteNoEncontrado.class, () -> cuentaService.crearCuenta(dto));
        assertTrue(ex.getMessage().contains("No se encontró cuenta con el id"));
    }

    @Test
    void obtenerSaldo_CuentaExistente() {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(123456L);
        cuenta.setSaldo(new BigDecimal("1000"));

        when(cuentaRepository.numeroCuenta(123456L)).thenReturn(cuenta);

        SaldoResponseDTO saldo = cuentaService.obtenerSaldo(123456L);

        assertEquals(123456L, saldo.getNumeroCuenta());
        assertEquals(new BigDecimal("1000"), saldo.getSaldo());
    }

    @Test
    void obtenerSaldo_CuentaNoEncontrada_LanzaExcepcion() {
        when(cuentaRepository.numeroCuenta(999L)).thenReturn(null);

        CuentaNoEncontrada ex = assertThrows(CuentaNoEncontrada.class, () -> cuentaService.obtenerSaldo(999L));
        assertTrue(ex.getMessage().contains("No se encontró cuenta con número"));
    }

    @Test
    void obtenerTransaccionesPorNumeroCuenta_DevuelveLista() {
        TransaccionResponseDTO t1 = new TransaccionResponseDTO(
                1L, new BigDecimal("100.00"), TipoTransaccion.TRANSFERENCIA,
                10L, 1001L, EstadoTransaccion.COMPLETADA,
                "Transferencia a cuenta", LocalDateTime.now(), 1002L, 11L
        );

        TransaccionResponseDTO t2 = new TransaccionResponseDTO(
                2L, new BigDecimal("200.00"), TipoTransaccion.TRANSFERENCIA,
                10L, 1001L, EstadoTransaccion.COMPLETADA,
                "Otra transferencia", LocalDateTime.now(), 1003L, 12L
        );

        List<TransaccionResponseDTO> transacciones = List.of(t1, t2);

        when(transaccionService.obtenerTransaccionesPorNumeroCuenta(123L)).thenReturn(transacciones);

        List<TransaccionResponseDTO> resultado = cuentaService.obtenerTransaccionesPorNumeroCuenta(123L);

        assertEquals(2, resultado.size());
    }

    @Test
    void obtenerTodos_CuentasExistentes() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta(123L);
        cuenta.setTipoCuenta(TipoCuenta.AHORRO);
        cuenta.setSaldo(BigDecimal.ZERO);
        cuenta.setCliente(cliente);
        cuenta.setFechaApertura(LocalDateTime.now());
        cuenta.setVersion(1L);

        when(cuentaRepository.findAll()).thenReturn(List.of(cuenta));

        List<CuentaResponseDTO> cuentas = cuentaService.obtenerTodos();

        assertEquals(1, cuentas.size());
        assertEquals(123L, cuentas.get(0).getNumeroCuenta());
    }

    @Test
    void obtenerTodos_SinCuentas_LanzaExcepcion() {
        when(cuentaRepository.findAll()).thenReturn(Collections.emptyList());

        CuentaNoEncontrada ex = assertThrows(CuentaNoEncontrada.class, () -> cuentaService.obtenerTodos());
        assertTrue(ex.getMessage().contains("No hay cuentas en el sistema"));
    }

    @Test
    void buscarPorNumeroCuenta_CuentaExistente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta(123L);
        cuenta.setTipoCuenta(TipoCuenta.CORRIENTE);
        cuenta.setSaldo(BigDecimal.ZERO);
        cuenta.setCliente(cliente);
        cuenta.setFechaApertura(LocalDateTime.now());
        cuenta.setVersion(1L);

        when(cuentaRepository.numeroCuenta(123L)).thenReturn(cuenta);

        CuentaResponseDTO dto = cuentaService.buscarPorNumeroCuenta(123L);

        assertEquals(123L, dto.getNumeroCuenta());
    }

    @Test
    void buscarPorNumeroCuenta_NoExiste_LanzaExcepcion() {
        when(cuentaRepository.numeroCuenta(999L)).thenReturn(null);

        CuentaNoEncontrada ex = assertThrows(CuentaNoEncontrada.class, () -> cuentaService.buscarPorNumeroCuenta(999L));
        assertTrue(ex.getMessage().contains("No se encontró cuenta con número"));
    }

    @Test
    void obtenerCuentaConUsername_CuentasExistentes() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta(123L);
        cuenta.setTipoCuenta(TipoCuenta.AHORRO);
        cuenta.setSaldo(BigDecimal.ZERO);
        cuenta.setCliente(cliente);
        cuenta.setFechaApertura(LocalDateTime.now());
        cuenta.setVersion(1L);

        when(cuentaRepository.findByUsername("user1")).thenReturn(List.of(cuenta));

        List<CuentaResponseDTO> cuentas = cuentaService.obtenerCuentaConUsername("user1");

        assertFalse(cuentas.isEmpty());
        assertEquals(123L, cuentas.get(0).getNumeroCuenta());
    }

    @Test
    void obtenerCuentaConUsername_NoCuentas_LanzaExcepcion() {
        when(cuentaRepository.findByUsername("user2")).thenReturn(Collections.emptyList());

        CuentaNoEncontrada ex = assertThrows(CuentaNoEncontrada.class, () -> cuentaService.obtenerCuentaConUsername("user2"));
        assertTrue(ex.getMessage().contains("No se encontró ninguna cuenta"));
    }

    @Test
    void esPropietarioOAdmin_PropietarioVerdadero() {
        Cliente cliente = new Cliente();
        Usuario usuario = new Usuario();
        usuario.setUsername("user1");
        cliente.setUsuario(usuario);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        boolean resultado = cuentaService.esPropietarioOAdmin(1L, "user1", false);

        assertTrue(resultado);
    }

    @Test
    void esPropietarioOAdmin_AdminVerdadero() {
        boolean resultado = cuentaService.esPropietarioOAdmin(1L, "otroUser", true);
        // No importa si cliente existe, debe devolver true si es admin
        assertTrue(resultado);
    }

    @Test
    void esPropietarioOAdmin_NoPropietarioNoAdmin_Falso() {
        Cliente cliente = new Cliente();
        Usuario usuario = new Usuario();
        usuario.setUsername("user1");
        cliente.setUsuario(usuario);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        boolean resultado = cuentaService.esPropietarioOAdmin(1L, "user2", false);

        assertFalse(resultado);
    }

    @Test
    void esPropietarioOAdmin_ClienteNoEncontrado_LanzaExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        ClienteNoEncontrado ex = assertThrows(ClienteNoEncontrado.class, ()
                -> cuentaService.esPropietarioOAdmin(99L, "user", false)
        );
        assertTrue(ex.getMessage().contains("No se encontró cuenta con el id"));
    }
}
