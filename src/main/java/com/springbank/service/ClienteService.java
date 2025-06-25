package com.springbank.service;

import com.springbank.dto.Request.AsignarCuentaDTO;
import com.springbank.dto.Request.AsignarUsuarioDTO;

import com.springbank.entity.Cliente;
import com.springbank.entity.Cuenta;
import com.springbank.entity.Usuario;
import com.springbank.repository.ClienteRepository;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    

    public void agregarUsuarioPorId(Long id, AsignarUsuarioDTO asignarUsuarioDTO) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id)); //Si no encuentra un Cliente con ese id, no existe y lanza una excepción.

        Usuario usuario = usuarioService.crearUsuario(asignarUsuarioDTO);
        cliente.setUsuario(usuario);
        clienteRepository.save(cliente);
    }

    public void agregarCuentaPorId(Long id, AsignarCuentaDTO cuentaDTO){
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Cliente no encontrado con id: "+id));
        
        Cuenta cuenta = cuentaService.crearCuenta(cuentaDTO, cliente);
        
        cuentaService.guardarCuenta(cuenta); //crear en service
        
        List<Cuenta> cuentas = cliente.getCuentas();
        cuentas.add(cuenta);
        cliente.setCuentas(cuentas);
        clienteRepository.save(cliente);
    }
    
}
