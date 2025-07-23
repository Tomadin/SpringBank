package com.springbank.service;

import com.springbank.dto.Request.CuentaRequestDTO;
import com.springbank.dto.Response.ClienteResponseDTO;
import com.springbank.dto.Response.CuentaResponseDTO;
import com.springbank.dto.Response.SaldoResponseDTO;
import com.springbank.dto.Response.TransaccionResponseDTO;
import com.springbank.entity.Cliente;
import com.springbank.entity.Cuenta;
import com.springbank.enums.TipoCuenta;
import com.springbank.exception.ClienteNoEncontrado;
import com.springbank.exception.CuentaInvalida;
import com.springbank.exception.CuentaNoEncontrada;
import com.springbank.exception.TipoCuentaException;
import com.springbank.repository.ClienteRepository;
import com.springbank.repository.CuentaRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private TransaccionService transaccionService;

    @Transactional
    public Cuenta crearCuenta(CuentaRequestDTO cuentaDTO) {

        if (cuentaDTO.getTipoCuenta() == null) {
            throw new TipoCuentaException("El tipo de cuenta es invalido o nulo.");
        }

        Cliente cliente = traerCliente(cuentaDTO.getClienteId());

        validarCuenta(cuentaDTO, cliente);

        if (!cuentaDTO.getTipoCuenta().equals(TipoCuenta.AHORRO) && !cuentaDTO.getTipoCuenta().equals(TipoCuenta.CORRIENTE)) {
            throw new TipoCuentaException("El tipo de cuenta ingresado [" + cuentaDTO.getTipoCuenta() + "], es invalido");
        }

        Cuenta cuenta = new Cuenta(
                cuentaDTO.getTipoCuenta(),
                cliente,
                LocalDateTime.now()
        );

        cuenta.setNumeroCuenta(generarNumeroCuentaUnico(cliente));

        return cuentaRepository.save(cuenta); //Guardamos y devolvemos la cuenta
    }

    private Cliente traerCliente(Long id) throws ClienteNoEncontrado {
        Cliente cliente = (Cliente) clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontrado("No se encontró cuenta con el id " + id));
        return cliente;
    }

    private void validarCuenta(CuentaRequestDTO cuentaDTO, Cliente cliente) {
        for (Cuenta cuenta : cliente.getCuentas()) {
            if (cuenta.getTipoCuenta().equals(cuentaDTO.getTipoCuenta())) {
                throw new CuentaInvalida("El cliente ya posee una cuenta del tipo: " + cuentaDTO.getTipoCuenta());
            }
        }
    }

    public SaldoResponseDTO obtenerSaldo(Long numeroCuenta) {
        Cuenta cuenta = cuentaRepository.numeroCuenta(numeroCuenta);
        if (cuenta == null) {
            throw new CuentaNoEncontrada("No se encontró cuenta con número: " + numeroCuenta);
        }

        SaldoResponseDTO saldoResponseDTO = new SaldoResponseDTO(cuenta.getNumeroCuenta(), cuenta.getSaldo());

        return saldoResponseDTO;
    }

    private Long generarNumeroCuentaUnico(Cliente cliente) {
        long base = cliente.getId() * 1_000_000L;
        long sufijo = generarNumeroAleatorio(6); // 6 dígitos
        return base + sufijo;
    }

    private long generarNumeroAleatorio(int digitos) {
        long min = (long) Math.pow(10, digitos - 1);
        long max = (long) Math.pow(10, digitos) - 1;
        return min + (long) (Math.random() * (max - min + 1));
    }

    public List<TransaccionResponseDTO> obtenerTransaccionesPorNumeroCuenta(Long numeroCuenta) { //llamar al service de transferencias y traer las que coincidan con el numeroCuenta 
        return transaccionService.obtenerTransaccionesPorNumeroCuenta(numeroCuenta);
    }

    public List<CuentaResponseDTO> obtenerTodos() {
        List<Cuenta> cuentas = cuentaRepository.findAll();
        List<CuentaResponseDTO> cuentasResponseDTO = new ArrayList<>();

        if (cuentas.isEmpty()) {
            throw new CuentaNoEncontrada("No hay cuentas en el sistema. ");
        }
        for (Cuenta cuenta : cuentas) {
            CuentaResponseDTO cuentaResponse = new CuentaResponseDTO(cuenta.getId(), cuenta.getNumeroCuenta(),
                    cuenta.getTipoCuenta(), cuenta.getSaldo(),
                    cuenta.getCliente().getId(), cuenta.getFechaApertura(),
                    cuenta.getVersion());

            cuentasResponseDTO.add(cuentaResponse);
        }
        return cuentasResponseDTO;
    }

    public CuentaResponseDTO buscarPorNumeroCuenta(Long numeroCuenta) {
        Cuenta cuenta = cuentaRepository.numeroCuenta(numeroCuenta);
        if (cuenta == null) {
            throw new CuentaNoEncontrada("No se encontró cuenta con número: " + numeroCuenta);
        }
        CuentaResponseDTO cuentaResponse = new CuentaResponseDTO(cuenta.getId(), cuenta.getNumeroCuenta(),
                cuenta.getTipoCuenta(), cuenta.getSaldo(),
                cuenta.getCliente().getId(), cuenta.getFechaApertura(),
                cuenta.getVersion());

        return cuentaResponse;
    }

    public List<CuentaResponseDTO> obtenerCuentaConUsername(String username) {
        List<Cuenta> cuentas = cuentaRepository.findByUsername(username);
        List<CuentaResponseDTO> cuentasResponseDTO = new ArrayList<>();
        if (cuentas.isEmpty()) {
            throw new CuentaNoEncontrada("No se encontró ninguna cuenta que tenga un Usuario con username: " + username);
        }
        for (Cuenta cuenta : cuentas) {
            CuentaResponseDTO cuentaResponse = new CuentaResponseDTO(cuenta.getId(), cuenta.getNumeroCuenta(),
                    cuenta.getTipoCuenta(), cuenta.getSaldo(),
                    cuenta.getCliente().getId(), cuenta.getFechaApertura(),
                    cuenta.getVersion());
            cuentasResponseDTO.add(cuentaResponse);
        }
        return cuentasResponseDTO;
    }

    public boolean esPropietarioOAdmin(Long clienteId, String username, boolean esAdmin) {
        if (esAdmin) {
            return true;
        }
        Cliente cliente = (Cliente) clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNoEncontrado("No se encontró cuenta con el id " + clienteId));

        return cliente.getUsuario().getUsername().equals(username);
    }

}
