/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.springbank;

import com.springbank.dto.Request.ClienteRequestDTO;
import com.springbank.dto.Request.UsuarioRequestDTO;
import com.springbank.dto.Response.ClienteResponseDTO;
import com.springbank.entity.Cliente;
import com.springbank.entity.Usuario;
import com.springbank.enums.RolUsuario;
import com.springbank.exception.ClienteNoEncontrado;
import com.springbank.exception.DniInvalido;
import com.springbank.exception.EmailInvalido;
import com.springbank.repository.ClienteRepository;
import com.springbank.service.ClienteService;
import com.springbank.service.CuentaService;
import com.springbank.service.UsuarioService;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private CuentaService cuentaService;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    public ClienteServiceTest() {
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

    @Test //DNI invalido, debe lanzar exception
    void crearClienteDniInvalido() {
        ClienteRequestDTO dto = new ClienteRequestDTO("Juan", "Perez", "juan@mail.com", "123"); // DNI corto

        // Mock: dni repetido
        when(clienteRepository.findByDni("12345678")).thenReturn(Optional.of(new Cliente()));

        // Test DNI formato corto
        DniInvalido ex1 = assertThrows(DniInvalido.class, () -> {
            clienteService.crearCliente(new ClienteRequestDTO("Ana", "Lopez", "ana@mail.com", "123"));
        });
        assertTrue(ex1.getMessage().contains("8 digitos"));

        // Test DNI repetido
        assertThrows(DniInvalido.class, () -> {
            clienteService.crearCliente(new ClienteRequestDTO("Ana", "Lopez", "ana@mail.com", "12345678"));
        });

        // Test DNI no numérico
        assertThrows(DniInvalido.class, () -> {
            clienteService.crearCliente(new ClienteRequestDTO("Ana", "Lopez", "ana@mail.com", "abcdefg1"));
        });
    }

    @Test
    void crearClienteEmailDemasiadoCorto() {

        ClienteRequestDTO dto = new ClienteRequestDTO("Ana", "Lopez", "a@b", "12345678");
        EmailInvalido ex = assertThrows(EmailInvalido.class, () -> clienteService.crearCliente(dto));
        assertTrue(ex.getMessage().contains("demasiado corto"));
    }

    @Test
    void crearClienteEmailFormatoInvalido() {

        ClienteRequestDTO dto = new ClienteRequestDTO("Ana", "Lopez", "analopezmailcom", "12345678");
        EmailInvalido ex = assertThrows(EmailInvalido.class, () -> clienteService.crearCliente(dto));
        assertTrue(ex.getMessage().contains("no tiene un formato válido"));
    }

    @Test
    void debugPasoAPaso() {
        // Configuramos los datos
        String dni = "87654321";
        String email = "ana@mail.com";

        System.out.println("=== CONFIGURANDO MOCKS ===");

        // Mock para DNI (debe retornar empty para que no falle)
        when(clienteRepository.findByDni(dni)).thenReturn(Optional.empty());
        System.out.println("Mock DNI configurado: " + clienteRepository.findByDni(dni));

        // Mock para Email (debe retornar present para que falle con EmailInvalido)
        Cliente clienteMock = mock(Cliente.class);
        when(clienteRepository.findByEmail(email)).thenReturn(Optional.of(clienteMock));
        System.out.println("Mock Email configurado: " + clienteRepository.findByEmail(email));

        ClienteRequestDTO dto = new ClienteRequestDTO("Ana", "Lopez", email, dni);
        System.out.println("DTO creado: " + dto);

        System.out.println("=== EJECUTANDO MÉTODO ===");

        try {
            clienteService.crearCliente(dto);
            System.out.println("❌ ERROR: El método NO lanzó ninguna excepción");
            fail("Se esperaba que se lanzara EmailInvalido");
        } catch (EmailInvalido e) {
            System.out.println("✅ SUCCESS: Se lanzó EmailInvalido: " + e.getMessage());
        } catch (DniInvalido e) {
            System.out.println("❌ ERROR: Se lanzó DniInvalido en lugar de EmailInvalido: " + e.getMessage());
            fail("Se esperaba EmailInvalido pero se obtuvo DniInvalido");
        } catch (Exception e) {
            System.out.println("❌ ERROR: Se lanzó excepción inesperada: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            fail("Excepción inesperada: " + e.getClass().getSimpleName());
        }
    }

    @Test
    void crearClienteEmailDuplicado() {
        // Arrange
        String dni = "87654321";
        String email = "ana@mail.com";

        // Simulamos que el email ya está registrado
        Cliente clienteExistente = new Cliente("Otro", "Cliente", email, "12345678");
        when(clienteRepository.findByEmail(email)).thenReturn(Optional.of(clienteExistente));

        ClienteRequestDTO dto = new ClienteRequestDTO("Ana", "Lopez", email, dni);

        // Act & Assert
        EmailInvalido ex = assertThrows(EmailInvalido.class, () -> {
            clienteService.crearCliente(dto);
        });

        assertTrue(ex.getMessage().contains("ya se encuentra registrado"));

        // Verificamos que se llamaron los métodos correctos
        verify(clienteRepository).findByDni(dni);
        verify(clienteRepository).findByEmail(email);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void crearClienteEmailConEspaciosDebeFallar() {
        ClienteRequestDTO dto = new ClienteRequestDTO("Ana", "Lopez", " ana@mail.com ", "12345678");

        when(clienteRepository.findByDni("12345678")).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.empty()); // <- así funciona

        EmailInvalido ex = assertThrows(EmailInvalido.class, () -> clienteService.crearCliente(dto));
        assertTrue(ex.getMessage().contains("no debe contener espacios"));
    }

    @Test
    void crearClienteExitoso() throws DniInvalido, EmailInvalido {
        ClienteRequestDTO dto = new ClienteRequestDTO("Juan", "Perez", "juan@mail.com", "12345678");

        when(clienteRepository.findByDni(dto.getDni())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));

        clienteService.crearCliente(dto);

        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test //El dni no existe, debe lanzar exception
    void obtenerClientePorDniInvalido() {
        String dni = "99999999";
        when(clienteRepository.findByDni(dni)).thenReturn(Optional.empty());

        ClienteNoEncontrado ex = assertThrows(ClienteNoEncontrado.class, () -> {
            clienteService.obtenerClientePorDni(dni);
        });

        assertTrue(ex.getMessage().contains("Cliente no encontrado con DNI"));
    }

    @Test
    void agregarUsuarioPorIdClienteExistente() throws ClienteNoEncontrado {
        Long clienteId = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        UsuarioRequestDTO usuarioDTO = new UsuarioRequestDTO("usuario", "pass", RolUsuario.CLIENTE, clienteId);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(usuarioService.crearUsuario(usuarioDTO)).thenReturn(new Usuario());

        clienteService.agregarUsuarioPorId(clienteId, usuarioDTO);

        verify(clienteRepository).save(cliente);
    }

    @Test //Cliente no existe, debe lanzar exception
    void agregarUsuarioPorIdClienteNoExiste() {
        Long clienteId = 1L;
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        assertThrows(ClienteNoEncontrado.class, () -> {
            clienteService.agregarUsuarioPorId(clienteId, new UsuarioRequestDTO("usuario", "pass", RolUsuario.CLIENTE, clienteId));
        });
    }

    @Test //Debe devolver un CLiente
    void obtenerClientePorIdExistente() throws ClienteNoEncontrado {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Juan");
        cliente.setApellido("Perez");
        cliente.setCuentas(new ArrayList<>());

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        ClienteResponseDTO dto = clienteService.obtenerClientePorId(1L);

        assertEquals("Juan", dto.getNombre());
        assertEquals("Perez", dto.getApellido());
    }

    @Test //El id del cliente no existe, debe lanzar una exception
    void obtenerClientePorIdNoExiste() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ClienteNoEncontrado.class, () -> clienteService.obtenerClientePorId(1L));
    }
}
