package com.ludwingvasquez.kinalapp.controller;

import com.ludwingvasquez.kinalapp.entity.Producto;
import com.ludwingvasquez.kinalapp.service.IProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final IProductoService productoService;

    public ProductoController(IProductoService productoService) {
        this.productoService = productoService;
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
        List<Producto> productos = productoService.listarTodos();
        model.addAttribute("productos", productos);
        model.addAttribute("totalProductos", productos.size());
        return "productos/dashboard";
    }

    @GetMapping
    public String listar(Model model) {
        List<Producto> productos = productoService.listarTodos();
        model.addAttribute("productos", productos);
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("editar", false);
        return "productos/formulario";
    }

    @GetMapping("/{id}")
    public String buscarPorId(@PathVariable Integer id, Model model) {
        Producto producto = productoService.buscarPorCodigo(id).orElse(null);
        model.addAttribute("producto", producto);
        return "productos/detalle";
    }

    @PostMapping
    public String guardar(@ModelAttribute Producto producto) {
        try {
            productoService.guardar(producto);
            return "redirect:/productos";
        } catch (IllegalArgumentException e) {
            return "redirect:/productos/nuevo?error=" + e.getMessage();
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Producto producto = productoService.buscarPorCodigo(id).orElse(null);
        model.addAttribute("producto", producto);
        model.addAttribute("editar", true);
        return "productos/formulario";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id, @ModelAttribute Producto producto) {
        try {
            productoService.actualizar(id, producto);
            return "redirect:/productos";
        } catch (RuntimeException e) {
            return "redirect:/productos/editar/" + id + "?error=" + e.getMessage();
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        try {
            productoService.eliminar(id);
            return "redirect:/productos";
        } catch (RuntimeException e) {
            return "redirect:/productos?error=" + e.getMessage();
        }
    }
}