package com.springbank;

import com.springbank.entity.Cliente;
import com.springbank.entity.Cuenta;
import com.springbank.entity.Transaccion;
import com.springbank.exception.CuentaNoEncontrada;
import com.springbank.exception.MontoInvalidoException;
import com.springbank.exception.SaldoInsuficienteException;
import com.springbank.exception.TransaccionException;
import com.springbank.repository.ClienteRepository;
import com.springbank.repository.CuentaRepository;
import com.springbank.repository.TransaccionRepository;
import com.springbank.service.TransaccionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private TransaccionService transaccionService;

    public TransaccionServiceTest() {
    }

    @BeforeAll
    public static void setUpClass() {

    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test //Deberia actualizar el saldo y guardar la transacción
    public void realizarDepositoTest() {
        // Arrange
        Long cuentaId = 1L;
        BigDecimal monto = new BigDecimal("150.00");

        Cuenta cuentaMock = new Cuenta();
        cuentaMock.setId(cuentaId);
        cuentaMock.setNumeroCuenta(123456L);
        cuentaMock.setSaldo(new BigDecimal("1000.00"));

        when(cuentaRepository.numeroCuenta(cuentaId)).thenReturn(cuentaMock);

        // Act
        transaccionService.realizarDeposito(monto, cuentaId);

        // Assert
        // Verifica que se haya actualizado el saldo
        assertEquals(new BigDecimal("1150.00"), cuentaMock.getSaldo());

        // Verifica que se haya guardado la transacción
        verify(transaccionRepository).save(any(Transaccion.class));

        // Verifica que se haya guardado la cuenta con el nuevo saldo
        verify(cuentaRepository).save(cuentaMock);
    }

    @Test //Deposito invalido, deberia lanzar una excepcion
    void realizarDepositoMontoInvalido() {
        BigDecimal montoInvalido = new BigDecimal("50.00");
        Long cuentaId = 1L;

        MontoInvalidoException ex = assertThrows(MontoInvalidoException.class, () -> {
            transaccionService.realizarDeposito(montoInvalido, cuentaId);
        });

        assertTrue(ex.getMessage().contains("El monto debe ser mayor a cien"));
        verifyNoInteractions(cuentaRepository);
        verifyNoInteractions(transaccionRepository);
    }

    @Test
    void realizarDepositoCuentaNoExiste() {
        Long cuentaId = 1L;
        BigDecimal monto = new BigDecimal("150.00");

        when(cuentaRepository.numeroCuenta(cuentaId)).thenReturn(null);

        CuentaNoEncontrada ex = assertThrows(CuentaNoEncontrada.class, () -> {
            transaccionService.realizarDeposito(monto, cuentaId);
        });

        assertEquals("Cuenta no encontrada con numero de cuenta: " + cuentaId, ex.getMessage());
        verify(transaccionRepository, never()).save(any());
    }

    @Test
    void realizarRetiroConSaldoInsuficiente() {
        Long cuentaId = 1L;
        BigDecimal monto = new BigDecimal("500.00");

        Cuenta cuentaMock = new Cuenta();
        cuentaMock.setId(cuentaId);
        cuentaMock.setNumeroCuenta(123456L);
        cuentaMock.setSaldo(new BigDecimal("200.00")); // saldo insuficiente

        when(cuentaRepository.numeroCuenta(cuentaId)).thenReturn(cuentaMock);

        SaldoInsuficienteException ex = assertThrows(SaldoInsuficienteException.class, () -> {
            transaccionService.realizarRetiro(monto, cuentaId);
        });

        assertTrue(ex.getMessage().startsWith("Saldo insuficiente para realizar el retiro"));

        verify(transaccionRepository, never()).save(any());
        verify(cuentaRepository, never()).save(any());
    }

    @Test
    void realizarTransferenciaConSaldoInsuficienteEnCuentaDeOrigen() {
        Long cuentaOrigenId = 1L;
        Long cuentaDestinoId = 2L;
        BigDecimal monto = new BigDecimal("500.00");

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setId(cuentaOrigenId);
        cuentaOrigen.setNumeroCuenta(111111L);
        cuentaOrigen.setSaldo(new BigDecimal("200.00")); // saldo insuficiente

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setId(cuentaDestinoId);
        cuentaDestino.setNumeroCuenta(222222L);
        cuentaDestino.setSaldo(new BigDecimal("300.00"));

        when(cuentaRepository.numeroCuenta(cuentaOrigenId)).thenReturn(cuentaOrigen);
        when(cuentaRepository.numeroCuenta(cuentaDestinoId)).thenReturn(cuentaDestino);

        SaldoInsuficienteException ex = assertThrows(SaldoInsuficienteException.class, () -> {
            transaccionService.realizarTransferencia(monto, cuentaOrigenId, cuentaDestinoId);
        });

        assertTrue(ex.getMessage().contains("Saldo insuficiente"));

        verify(transaccionRepository, never()).save(any());
        verify(cuentaRepository, never()).save(any(Cuenta.class));
    }

    @Test
    void realizarTransferencia_exitosa() {
        Long cuentaOrigenId = 1L;
        Long cuentaDestinoId = 2L;
        BigDecimal monto = new BigDecimal("150.00");

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setId(cuentaOrigenId);
        cuentaOrigen.setNumeroCuenta(111111L);
        cuentaOrigen.setSaldo(new BigDecimal("500.00"));
        Cliente clienteOrigen = new Cliente();
        clienteOrigen.setNombre("Juan Pérez");
        cuentaOrigen.setCliente(clienteOrigen);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setId(cuentaDestinoId);
        cuentaDestino.setNumeroCuenta(222222L);
        cuentaDestino.setSaldo(new BigDecimal("300.00"));
        Cliente clienteDestino = new Cliente();
        clienteDestino.setNombre("María López");
        cuentaDestino.setCliente(clienteDestino);

        when(cuentaRepository.numeroCuenta(cuentaOrigenId)).thenReturn(cuentaOrigen);
        when(cuentaRepository.numeroCuenta(cuentaDestinoId)).thenReturn(cuentaDestino);
        when(transaccionRepository.save(any(Transaccion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        transaccionService.realizarTransferencia(monto, cuentaOrigenId, cuentaDestinoId);

        assertEquals(new BigDecimal("350.00"), cuentaOrigen.getSaldo());
        assertEquals(new BigDecimal("450.00"), cuentaDestino.getSaldo());

        verify(transaccionRepository, times(1)).save(any(Transaccion.class));

        verify(cuentaRepository, times(1)).save(cuentaOrigen);
        verify(cuentaRepository, times(1)).save(cuentaDestino);
    }

    @Test
    void realizarTransferenciaCuentaOrigenInvalida() {
        Long cuentaOrigenId = 1L;
        Long cuentaDestinoId = 2L;
        BigDecimal monto = new BigDecimal("150.00");

        when(cuentaRepository.numeroCuenta(cuentaOrigenId)).thenReturn(null);  // No existe cuenta origen

        CuentaNoEncontrada ex = assertThrows(CuentaNoEncontrada.class, () -> {
            transaccionService.realizarTransferencia(monto, cuentaOrigenId, cuentaDestinoId);
        });

        assertTrue(ex.getMessage().contains("Cuenta no encontrada con numero de cuenta"));
        verify(transaccionRepository, never()).save(any());
        verify(cuentaRepository, never()).save(any());
    }

    @Test //Transferir a la misma cuenta debe lanzar error
    void transferirAMismaCuenta() {
        Long cuentaId = 1L;
        BigDecimal monto = new BigDecimal("100.00");

        Cuenta cuenta = new Cuenta();
        cuenta.setId(cuentaId);
        cuenta.setNumeroCuenta(111111L);
        cuenta.setSaldo(new BigDecimal("500.00"));

        Cliente cliente = new Cliente();
        cliente.setNombre("Juan");
        cuenta.setCliente(cliente);

        when(cuentaRepository.numeroCuenta(cuentaId)).thenReturn(cuenta);

        TransaccionException ex = assertThrows(TransaccionException.class, () -> {
            transaccionService.realizarTransferencia(monto, cuentaId, cuentaId);
        });

        assertEquals("No se puede realizar una transferencia a la misma cuenta.", ex.getMessage());

        verify(transaccionRepository, never()).save(any());
        verify(cuentaRepository, never()).save(any());
    }

    @Test //Tranferir un monto mayor al saldo debe lanzar error.
    void transferirMontoMayorAlSaldo() {
        Long cuentaOrigenId = 1L;
        Long cuentaDestinoId = 2L;
        BigDecimal monto = new BigDecimal("1000.00"); // monto mayor al saldo

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setId(cuentaOrigenId);
        cuentaOrigen.setNumeroCuenta(111111L);
        cuentaOrigen.setSaldo(new BigDecimal("500.00"));

        Cliente clienteOrigen = new Cliente();
        clienteOrigen.setNombre("Juan");
        cuentaOrigen.setCliente(clienteOrigen);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setId(cuentaDestinoId);
        cuentaDestino.setNumeroCuenta(222222L);
        cuentaDestino.setSaldo(new BigDecimal("300.00"));

        Cliente clienteDestino = new Cliente();
        clienteDestino.setNombre("María");
        cuentaDestino.setCliente(clienteDestino);

        when(cuentaRepository.numeroCuenta(cuentaOrigenId)).thenReturn(cuentaOrigen);
        when(cuentaRepository.numeroCuenta(cuentaDestinoId)).thenReturn(cuentaDestino);

        SaldoInsuficienteException ex = assertThrows(SaldoInsuficienteException.class, () -> {
            transaccionService.realizarTransferencia(monto, cuentaOrigenId, cuentaDestinoId);
        });

        assertTrue(ex.getMessage().contains("Saldo insuficiente"));

        verify(transaccionRepository, never()).save(any());
        verify(cuentaRepository, never()).save(any());
    }

    @Test //Transferencia con cuentaDestino invalida, debe lanzar excepcion
    void realizarTransferenciaConCuentaDestinoInvalida() {
        Long cuentaOrigenId = 1L;
        Long cuentaDestinoId = 99L; // cuenta destino inexistente
        BigDecimal monto = new BigDecimal("100.00");

        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setId(cuentaOrigenId);
        cuentaOrigen.setNumeroCuenta(111111L);
        cuentaOrigen.setSaldo(new BigDecimal("500.00"));

        Cliente clienteOrigen = new Cliente();
        clienteOrigen.setNombre("Juan");
        cuentaOrigen.setCliente(clienteOrigen);

        // Cuando se busca la cuenta origen, sí existe
        when(cuentaRepository.numeroCuenta(cuentaOrigenId)).thenReturn(cuentaOrigen);

        // Cuando se busca la cuenta destino, no existe (devuelve null)
        when(cuentaRepository.numeroCuenta(cuentaDestinoId)).thenReturn(null);

        CuentaNoEncontrada ex = assertThrows(CuentaNoEncontrada.class, () -> {
            transaccionService.realizarTransferencia(monto, cuentaOrigenId, cuentaDestinoId);
        });

        assertEquals("Cuenta no encontrada con numero de cuenta: " + cuentaDestinoId, ex.getMessage());

        verify(transaccionRepository, never()).save(any());
        verify(cuentaRepository, never()).save(any());
    }

    @Test
    void realizarRetiroExitoso() {
        Long cuentaId = 1L;
        BigDecimal monto = new BigDecimal("100.00");

        Cuenta cuentaMock = new Cuenta();
        cuentaMock.setId(cuentaId);
        cuentaMock.setNumeroCuenta(123456L);
        cuentaMock.setSaldo(new BigDecimal("500.00")); // saldo suficiente

        when(cuentaRepository.numeroCuenta(cuentaId)).thenReturn(cuentaMock);
        when(transaccionRepository.save(any(Transaccion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        transaccionService.realizarRetiro(monto, cuentaId);

        assertEquals(new BigDecimal("400.00"), cuentaMock.getSaldo());

        verify(transaccionRepository, times(1)).save(any(Transaccion.class));
        verify(cuentaRepository, times(1)).save(cuentaMock);
    }

}
