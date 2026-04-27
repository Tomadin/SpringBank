package com.springbank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping("/")
    public String root() { return "redirect:/login"; }

    @GetMapping("/login")
    public String login() { return "forward:/login.html"; }

    @GetMapping("/registro")
    public String registro() { return "forward:/registro.html"; }

    @GetMapping("/dashboard")
    public String dashboard() { return "forward:/dashboard-cliente.html"; }

    @GetMapping("/transacciones")
    public String transacciones() { return "forward:/transacciones.html"; }

    @GetMapping("/movimientos")
    public String movimientos() { return "forward:/movimientos.html"; }

    @GetMapping("/nueva-cuenta")
    public String nuevaCuenta() { return "forward:/nueva-cuenta.html"; }

    @GetMapping("/perfil")
    public String perfil() { return "forward:/perfil.html"; }

    @GetMapping("/dashboard-admin")
    public String dashboardAdmin() { return "forward:/dashboard-admin.html"; }

    @GetMapping("/admin-clientes")
    public String adminClientes() { return "forward:/admin-clientes.html"; }

    @GetMapping("/admin-transacciones")
    public String adminTransacciones() { return "forward:/admin-transacciones.html"; }

    @GetMapping("/admin-cuentas")
    public String adminCuentas() { return "forward:/admin-cuentas.html"; }

    @GetMapping("/admin-usuarios")
    public String adminUsuarios() { return "forward:/admin-usuarios.html"; }
}
