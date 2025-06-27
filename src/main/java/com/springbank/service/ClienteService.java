package com.springbank.service;

import com.springbank.dto.Request.UsuarioRequestDTO;
import com.springbank.dto.Request.ClienteRequestDTO;
import com.springbank.dto.Request.CuentaRequestDTO;
import com.springbank.dto.Response.ClienteResponseDTO;
import com.springbank.dto.Response.UsuarioResponseDTO;
import com.springbank.entity.Cliente;
import com.springbank.entity.Cuenta;
import com.springbank.entity.Usuario;
import com.springbank.exception.ClienteNoEncontrado;
import com.springbank.exception.DniInvalido;
import com.springbank.exception.EmailInvalido;
import com.springbank.repository.ClienteRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    @Autowired
    ClienteRepository clienteRepository;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    CuentaService cuentaService;

    /*
    INYECCION POR CONSTRUCTOR ES MAS LIMPIO Y TESTEABLE
    private final ClienteRepository clienteRepository;
    private final UsuarioService usuarioService;
    
    @Autowired
    public ClienteService(ClienteRepository clienteRepository, UsuarioService usuarioService) {
        this.clienteRepository = clienteRepository;
        this.usuarioService = usuarioService;
    }
     */
    private void validarDNI(String dni) throws DniInvalido {

        int dniNum;

        try {
            dniNum = Integer.parseInt(dni);
        } catch (NumberFormatException e) {
            throw new DniInvalido("El DNI ingresado no es un número válido.");
        }

        if (dniNum <= 0) {
            throw new DniInvalido("El DNI debe ser un número positivo.");
        }

        if (dni.length() != 8) {
            throw new DniInvalido("El DNI debe tener 8 digitos.");
        }

        if (clienteRepository.findByDni(dni).isPresent()) {
            throw new DniInvalido("El DNI " + dni + " ya se encuentra registrado.");
        }

    }

    private void validarEmail(String email) throws EmailInvalido {
        if (email == null || email.length() < 5) {
            throw new EmailInvalido("El email es demasiado corto.");
        }

        if (!email.contains("@") || !email.contains(".")) {
            throw new EmailInvalido("El email '" + email + "' no tiene un formato válido.");
        }

        if (clienteRepository.findByEmail(email).isPresent()) {
            throw new EmailInvalido("El email '" + email + "' ya se encuentra registrado.");
        }

    }

    @Transactional
    public void crearCliente(ClienteRequestDTO clienteRequestDTO) throws DniInvalido, EmailInvalido {
        validarDNI(clienteRequestDTO.getDni());
        validarEmail(clienteRequestDTO.getEmail());
        Cliente cliente = new Cliente(
                clienteRequestDTO.getNombre(),
                clienteRequestDTO.getApellido(),
                clienteRequestDTO.getEmail(),
                clienteRequestDTO.getDni());

        clienteRepository.save(cliente);
    }

    @Transactional
    public void agregarUsuarioPorId(Long id, UsuarioRequestDTO asignarUsuarioDTO) throws ClienteNoEncontrado {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontrado("Cliente no encontrado con id: " + id)); //Si no encuentra un Cliente con ese id, no existe y lanza una excepción.

        Usuario usuario = usuarioService.crearUsuario(asignarUsuarioDTO);
        cliente.setUsuario(usuario);
        clienteRepository.save(cliente);
    }

    @Transactional
    public void agregarCuentaPorId(Long id, CuentaRequestDTO cuentaDTO) throws ClienteNoEncontrado, Exception {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontrado("Cliente no encontrado con id: " + id));

        Cuenta cuenta = cuentaService.crearCuenta(cuentaDTO);

        List<Cuenta> cuentas = cliente.getCuentas();
        cuentas.add(cuenta);
        cliente.setCuentas(cuentas);
        clienteRepository.save(cliente);
    }

    public ClienteResponseDTO obtenerClientePorDni(String dni) throws ClienteNoEncontrado {
        Cliente cliente = clienteRepository.findByDni(dni)
                .orElseThrow(() -> new ClienteNoEncontrado("Cliente no encontrado con DNI: " + dni));

        return generarClienteResponseDTO(cliente);
    }

    public ClienteResponseDTO obtenerClientePorId(Long id) throws ClienteNoEncontrado {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontrado("Cliente no encontrado con id: " + id));

        return generarClienteResponseDTO(cliente);
    }

    private ClienteResponseDTO generarClienteResponseDTO(Cliente cliente) {

        UsuarioResponseDTO usuarioDTO = null;
        
        if (cliente.getUsuario() != null) {
            Usuario usuarioEntidad = cliente.getUsuario();
            usuarioDTO = new UsuarioResponseDTO(
                    usuarioEntidad.getId(),
                    usuarioEntidad.getUsername(),
                    usuarioEntidad.getRol(),
                    usuarioEntidad.getCliente().getId()
            );
        }
        
        List<Long> cuentasIdList = cliente.getCuentas() //cliente.getCuentas trae objetos Cuenta y debemos sacar los id, por eso utilizamos un stream, map y collect para tener una lista con los ids
                .stream()
                .map(Cuenta::getId)
                .collect(Collectors.toList());

        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                usuarioDTO,
                cuentasIdList);

    }

    public List<ClienteResponseDTO> obtenerTodos() {
        List<Cliente> clientes = clienteRepository.findAll();
        List<ClienteResponseDTO> clientesResponseDTOs = new ArrayList<>();
        for (Cliente cliente : clientes) {
            ClienteResponseDTO clienteResponseDTO = generarClienteResponseDTO(cliente);
            clientesResponseDTOs.add(clienteResponseDTO);
       }
        return clientesResponseDTOs;
    }

}
