package com.ludwingvasquez.kinalapp.controller;

import com.ludwingvasquez.kinalapp.entity.Producto;
import com.ludwingvasquez.kinalapp.service.IDetalleVentaService;
import com.ludwingvasquez.kinalapp.service.IProductoService;
import com.ludwingvasquez.kinalapp.service.IUsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final IProductoService productoService;
    private final IUsuarioService usuarioService;
    private final IDetalleVentaService detalleVentaService;

    public ProductoController(IProductoService productoService, IUsuarioService usuarioService, IDetalleVentaService detalleVentaService) {
        this.productoService = productoService;
        this.usuarioService = usuarioService;
        this.detalleVentaService = detalleVentaService;
    }

    @ModelAttribute
    public void agregarUsuarioAlModelo(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            String nombreUsuario = auth.getName();
            String rolUsuario = auth.getAuthorities().stream()
                    .findFirst()
                    .map(a -> a.getAuthority().replace("ROLE_", ""))
                    .orElse("Usuario");
            
            String emailUsuario = usuarioService.buscarPorUsername(nombreUsuario)
                    .map(u -> u.getEmail())
                    .orElse("");
            
            model.addAttribute("nombreUsuario", nombreUsuario);
            model.addAttribute("emailUsuario", emailUsuario);
            model.addAttribute("rolUsuario", rolUsuario);
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
            if (!productoService.buscarPorCodigo(id).isPresent()) {
                return "redirect:/productos?error=Producto no encontrado";
            }
            // Verificar si el producto tiene ventas asociadas
            long ventasAsociadas = detalleVentaService.contarDetallesPorProducto(id);
            if (ventasAsociadas > 0) {
                return "redirect:/productos?error=No se puede eliminar el producto porque tiene " + ventasAsociadas + " venta(s) asociada(s)";
            }
            productoService.eliminar(id);
            return "redirect:/productos";
        } catch (RuntimeException e) {
            return "redirect:/productos?error=" + e.getMessage();
        }
    }

    @GetMapping("/exportar/csv")
    public void exportarCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=productos.csv");
        
        List<Producto> productos = productoService.listarTodos();
        
        PrintWriter writer = response.getWriter();
        writer.println("Codigo,Nombre,Precio,Stock,Estado");
        
        for (Producto p : productos) {
            String estado = p.getStock() > 0 ? "Disponible" : "Agotado";
            writer.printf("%d,\"%s\",%.2f,%d,%s%n",
                p.getCodigo_producto(),
                p.getNombre_producto().replace("\"", "\"\""),
                p.getPrecio(),
                p.getStock(),
                estado
            );
        }
        writer.flush();
    }
}