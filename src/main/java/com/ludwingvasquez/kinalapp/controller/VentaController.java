package com.ludwingvasquez.kinalapp.controller;

import com.ludwingvasquez.kinalapp.entity.Venta;
import com.ludwingvasquez.kinalapp.service.IVentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final IVentaService ventaService;

    public VentaController(IVentaService ventaService) {
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
        List<Venta> ventas = ventaService.listarTodos();
        model.addAttribute("ventas", ventas);
        model.addAttribute("totalVentas", ventas.size());
        return "ventas/dashboard";
    }

    @GetMapping
    public String listar(Model model) {
        List<Venta> ventas = ventaService.listarTodos();
        model.addAttribute("ventas", ventas);
        return "ventas/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("venta", new Venta());
        model.addAttribute("editar", false);
        return "ventas/formulario";
    }

    @GetMapping("/{codigoVenta}")
    public String buscarCV(@PathVariable Long codigoVenta, Model model) {
        Venta venta = ventaService.buscarCV(codigoVenta).orElse(null);
        model.addAttribute("venta", venta);
        return "ventas/detalle";
    }

    @PostMapping
    public String guardar(@ModelAttribute Venta venta) {
        try {
            ventaService.guardar(venta);
            return "redirect:/ventas";
        } catch (Exception e) {
            return "redirect:/ventas/nuevo?error=" + e.getMessage();
        }
    }

    @GetMapping("/editar/{codigoVenta}")
    public String editar(@PathVariable Long codigoVenta, Model model) {
        Venta venta = ventaService.buscarCV(codigoVenta).orElse(null);
        model.addAttribute("venta", venta);
        model.addAttribute("editar", true);
        return "ventas/formulario";
    }

    @PostMapping("/actualizar/{codigoVenta}")
    public String actualizar(@PathVariable Long codigoVenta, @ModelAttribute Venta venta) {
        try {
            ventaService.actualizar(codigoVenta, venta);
            return "redirect:/ventas";
        } catch (Exception e) {
            return "redirect:/ventas/editar/" + codigoVenta + "?error=" + e.getMessage();
        }
    }

    @GetMapping("/eliminar/{codigoVenta}")
    public String eliminar(@PathVariable Long codigoVenta) {
        try {
            ventaService.eliminar(codigoVenta);
            return "redirect:/ventas";
        } catch (RuntimeException e) {
            return "redirect:/ventas?error=" + e.getMessage();
        }
    }

    @GetMapping("/activas")
    public String listarActivas(Model model) {
        List<Venta> ventasActivas = ventaService.listarActivas();
        model.addAttribute("ventas", ventasActivas);
        model.addAttribute("filtro", "Activas");
        return "ventas/lista";
    }
}