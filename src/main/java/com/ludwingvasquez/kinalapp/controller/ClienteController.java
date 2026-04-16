package com.ludwingvasquez.kinalapp.controller;

import com.ludwingvasquez.kinalapp.entity.Cliente;
import com.ludwingvasquez.kinalapp.service.IClienteService;
import com.ludwingvasquez.kinalapp.service.IVentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final IClienteService clienteService;
    private final IVentaService ventaService;

    public ClienteController(IClienteService clienteService, IVentaService ventaService) {
        this.clienteService = clienteService;
        this.ventaService = ventaService;
    }

    @ModelAttribute
    public void agregarUsuarioAlModelo(Model model, HttpSession session) {
        String nombreUsuario = (String) session.getAttribute("nombreUsuario");
        String emailUsuario = (String) session.getAttribute("emailUsuario");
        String rolUsuario = (String) session.getAttribute("rolUsuario");
        
        if (nombreUsuario != null) {
            model.addAttribute("nombreUsuario", nombreUsuario);
            model.addAttribute("emailUsuario", emailUsuario);
            model.addAttribute("rolUsuario", rolUsuario != null ? rolUsuario : "Usuario");
            model.addAttribute("inicialesUsuario", obtenerIniciales(nombreUsuario));
        }
    }
    
    private String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            return "U";
        }
        String[] partes = nombre.split("[.\\s@]+");
        if (partes.length >= 2) {
            return (partes[0].substring(0, 1) + partes[partes.length - 1].substring(0, 1)).toUpperCase();
        }
        return nombre.substring(0, Math.min(2, nombre.length())).toUpperCase();
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Cliente> clientes = clienteService.listarTodos();
        
        // Filtrar clientes activos 
        long clientesActivos = clientes.stream()
            .filter(c -> c.getEstado() != null && c.getEstado() == 1)
            .count();
        
        // Calcular nuevos clientes del mes 
        long nuevosClientesMes = clientesActivos;
        
        // Estadísticas de ventas reales desde el servicio
        int totalVentasClientes = ventaService.contarTotalVentas();
        double ingresosClientes = ventaService.calcularIngresosTotales();
        
        model.addAttribute("clientes", clientes);
        model.addAttribute("totalClientes", clientes.size());
        model.addAttribute("clientesActivos", (int) clientesActivos);
        model.addAttribute("nuevosClientesMes", (int) nuevosClientesMes);
        model.addAttribute("totalVentasClientes", totalVentasClientes);
        model.addAttribute("ingresosClientes", ingresosClientes);
        
        // Últimos 3 clientes agregados 
        List<Cliente> ultimosClientes = clientes.stream()
            .filter(c -> c.getNombreCliente() != null && !c.getNombreCliente().isEmpty())
            .limit(3)
            .toList();
        model.addAttribute("ultimosClientes", ultimosClientes);
        
        return "clientes/dashboard";
    }

    @GetMapping
    public String listar(Model model) {
        List<Cliente> clientes = clienteService.listarTodos();
        model.addAttribute("clientes", clientes);
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("editar", false);
        return "clientes/formulario";
    }

    @GetMapping("/{dpi}")
    public String buscarPorDPI(@PathVariable String dpi, Model model) {
        Cliente cliente = clienteService.buscarPorDPI(dpi).orElse(null);
        model.addAttribute("cliente", cliente);
        return "clientes/detalle";
    }

    @PostMapping
    public String guardar(@ModelAttribute Cliente cliente) {
        try {
            clienteService.guardar(cliente);
            return "redirect:/clientes";
        } catch (IllegalArgumentException e) {
            return "redirect:/clientes/nuevo?error=" + e.getMessage();
        }
    }

    @GetMapping("/editar/{dpi}")
    public String editar(@PathVariable String dpi, Model model) {
        Cliente cliente = clienteService.buscarPorDPI(dpi).orElse(null);
        model.addAttribute("cliente", cliente);
        model.addAttribute("editar", true);
        return "clientes/formulario";
    }

    @PostMapping("/actualizar/{dpi}")
    public String actualizar(@PathVariable String dpi, @ModelAttribute Cliente cliente) {
        try {
            if (!clienteService.existePorDPI(dpi)) {
                return "redirect:/clientes?error=No encontrado";
            }
            clienteService.actualizar(dpi, cliente);
            return "redirect:/clientes";
        } catch (IllegalArgumentException e) {
            return "redirect:/clientes/editar/" + dpi + "?error=" + e.getMessage();
        } catch (RuntimeException e) {
            return "redirect:/clientes?error=" + e.getMessage();
        }
    }

    @GetMapping("/eliminar/{dpi}")
    public String eliminar(@PathVariable String dpi) {
        try {
            if (!clienteService.existePorDPI(dpi)) {
                return "redirect:/clientes?error=No encontrado";
            }
            clienteService.eliminar(dpi);
            return "redirect:/clientes";
        } catch (RuntimeException e) {
            return "redirect:/clientes?error=" + e.getMessage();
        }
    }

    @GetMapping("/activos")
    public String listarActivos(Model model) {
        List<Cliente> activos = clienteService.listarPorEstado(1);
        model.addAttribute("clientes", activos);
        model.addAttribute("filtro", "Activos");
        return "clientes/lista";
    }

    @GetMapping("/inactivos")
    public String listarInactivos(Model model) {
        List<Cliente> inactivos = clienteService.listarPorEstado(0);
        model.addAttribute("clientes", inactivos);
        model.addAttribute("filtro", "Inactivos");
        return "clientes/lista";
    }
}