package com.ludwingvasquez.kinalapp.controller;

import com.ludwingvasquez.kinalapp.entity.DetalleVenta;
import com.ludwingvasquez.kinalapp.service.IDetalleVentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/detalles_ventas")
public class DetalleVentaController {

    private final IDetalleVentaService detalleVentaService;

    public DetalleVentaController(IDetalleVentaService detalleVentaService) {
        this.detalleVentaService = detalleVentaService;
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

    @GetMapping
    public String listar(Model model) {
        List<DetalleVenta> detalles = detalleVentaService.listarTodos();
        model.addAttribute("detalles", detalles);
        return "detalles_ventas/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("detalle", new DetalleVenta());
        model.addAttribute("editar", false);
        return "detalles_ventas/formulario";
    }

    @GetMapping("/{codigoDetalle}")
    public String buscarPorId(@PathVariable Integer codigoDetalle, Model model) {
        DetalleVenta detalle = detalleVentaService.buscarPorId(codigoDetalle).orElse(null);
        model.addAttribute("detalle", detalle);
        return "detalles_ventas/detalle";
    }

    @GetMapping("/venta/{codigoVenta}")
    public String listarPorVenta(@PathVariable Integer codigoVenta, Model model) {
        List<DetalleVenta> detalles = detalleVentaService.listarPorVenta(codigoVenta);
        model.addAttribute("detalles", detalles);
        model.addAttribute("codigoVenta", codigoVenta);
        return "detalles_ventas/lista";
    }

    @PostMapping
    public String guardar(@ModelAttribute DetalleVenta detalle) {
        try {
            detalleVentaService.guardar(detalle);
            return "redirect:/detalles_ventas";
        } catch (RuntimeException e) {
            return "redirect:/detalles_ventas/nuevo?error=" + e.getMessage();
        }
    }

    @GetMapping("/editar/{codigoDetalle}")
    public String editar(@PathVariable Integer codigoDetalle, Model model) {
        DetalleVenta detalle = detalleVentaService.buscarPorId(codigoDetalle).orElse(null);
        model.addAttribute("detalle", detalle);
        model.addAttribute("editar", true);
        return "detalles_ventas/formulario";
    }

    @PostMapping("/actualizar/{codigoDetalle}")
    public String actualizar(@PathVariable Integer codigoDetalle, @ModelAttribute DetalleVenta detalle) {
        try {
            detalleVentaService.guardar(detalle);
            return "redirect:/detalles_ventas";
        } catch (RuntimeException e) {
            return "redirect:/detalles_ventas/editar/" + codigoDetalle + "?error=" + e.getMessage();
        }
    }

    @GetMapping("/eliminar/{codigoDetalle}")
    public String eliminar(@PathVariable Integer codigoDetalle) {
        try {
            detalleVentaService.eliminar(codigoDetalle);
            return "redirect:/detalles_ventas";
        } catch (Exception e) {
            return "redirect:/detalles_ventas?error=No se pudo eliminar el detalle";
        }
    }
}