package com.springbank;

import com.springbank.dto.Request.UsuarioRequestDTO;
import com.springbank.dto.Response.UsuarioResponseDTO;
import com.springbank.entity.Cliente;
import com.springbank.entity.Usuario;
import com.springbank.enums.RolUsuario;
import com.springbank.exception.UsuarioException;
import com.springbank.repository.ClienteRepository;
import com.springbank.repository.UsuarioRepository;
import com.springbank.service.UsuarioService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @DisplayName("Verificamos que el metodo 'crearUsuario' funcione correctamente con datos de prueba")
    @Test
    public void UsuarioServiceTest() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(passwordEncoder.encode("123jb123")).thenReturn("encoded123");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioRequestDTO dto = new UsuarioRequestDTO("JusBieber", "123jb123", RolUsuario.CLIENTE, 1L);

        // Act
        Usuario result = usuarioService.crearUsuario(dto);

        // Assert
        assertNotNull(result);
        assertEquals("JusBieber", result.getUsername());
        assertEquals("encoded123", result.getPassword());
        assertEquals(RolUsuario.CLIENTE, result.getRol());
        assertEquals(cliente, result.getCliente());
        assertEquals(result, cliente.getUsuario());
    }

    @DisplayName("Buscamos un usuario por su username")
    @Test
    public void buscarPorUsernameTest() {

        Cliente cliente = new Cliente();
        cliente.setId(4L);
        Usuario usuario = new Usuario("admin", "contraseña", cliente);
        usuario.setId(7L);
        usuario.setRol(RolUsuario.ADMIN);

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));

        // Act
        Usuario result = usuarioService.buscarPorUsername("admin");

        // Assert
        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals(RolUsuario.ADMIN, result.getRol());
        assertEquals(4L, result.getCliente().getId());
    }

    @DisplayName("Buscamos un usuarioResponseDTO por su username")
    @Test
    public void buscarPorUsernameDTOTest() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(4L);

        Usuario usuario = new Usuario();
        usuario.setId(4L);
        usuario.setUsername("admin");
        usuario.setRol(RolUsuario.ADMIN);
        usuario.setCliente(cliente);

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));

        // Act
        UsuarioResponseDTO result = usuarioService.buscarPorUsernameDTO("admin");

        // Assert
        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals(RolUsuario.ADMIN, result.getRol());
        assertEquals(4L, result.getClienteId());
    }

    @DisplayName("Verificamos que traerUsuarios() devuelva una lista de usuarios correctamente")
    @Test
    public void traerUsuariosTest() {
        // Arrange
        Cliente cliente1 = new Cliente();
        cliente1.setId(1L);

        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);

        Usuario usuario1 = new Usuario("user1", "pass1", cliente1);
        usuario1.setId(1L);
        usuario1.setRol(RolUsuario.CLIENTE);

        Usuario usuario2 = new Usuario("user2", "pass2", cliente2);
        usuario2.setId(2L);
        usuario2.setRol(RolUsuario.ADMIN);

        List<Usuario> usuariosFicticios = List.of(usuario1, usuario2);

        when(usuarioRepository.findAll()).thenReturn(usuariosFicticios);

        // Act
        List<Usuario> result = usuarioService.traerUsuarios();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        verify(usuarioRepository, times(1)).findAll();
    }

    @DisplayName("Verificamos que traerTodos() devuelva una lista de UsuarioResponseDTO correctamente")
    @Test
    public void traerTodosUsuariosExitosoTest() {
        // Arrange
        Cliente cliente1 = new Cliente();
        cliente1.setId(1L);

        Usuario usuario1 = new Usuario("user1", "pass1", cliente1);
        usuario1.setId(1L);
        usuario1.setRol(RolUsuario.CLIENTE);

        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);

        Usuario usuario2 = new Usuario("admin", "pass2", cliente2);
        usuario2.setId(2L);
        usuario2.setRol(RolUsuario.ADMIN);

        List<Usuario> usuarios = List.of(usuario1, usuario2);

        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // Act
        List<UsuarioResponseDTO> result = usuarioService.traerTodos();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        UsuarioResponseDTO r1 = result.get(0);
        assertEquals(1L, r1.getId());
        assertEquals("user1", r1.getUsername());
        assertEquals(RolUsuario.CLIENTE, r1.getRol());
        assertEquals(1L, r1.getClienteId());

        UsuarioResponseDTO r2 = result.get(1);
        assertEquals(2L, r2.getId());
        assertEquals("admin", r2.getUsername());
        assertEquals(RolUsuario.ADMIN, r2.getRol());
        assertEquals(2L, r2.getClienteId());
    }

    @DisplayName("traerTodos(): debe lanzar UsuarioException si no hay usuarios")
    @Test
    public void traerTodosUsuariosVacioTest() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        UsuarioException ex = assertThrows(UsuarioException.class, () -> {
            usuarioService.traerTodos();
        });

        assertEquals("No existen usuarios.", ex.getMessage());
    }
}
