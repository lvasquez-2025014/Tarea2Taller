package com.ludwingvasquez.kinalapp.controller;

import com.ludwingvasquez.kinalapp.entity.DetalleVenta;
import com.ludwingvasquez.kinalapp.entity.Notificacion;
import com.ludwingvasquez.kinalapp.entity.Producto;
import com.ludwingvasquez.kinalapp.entity.Venta;
import com.ludwingvasquez.kinalapp.service.IProductoService;
import com.ludwingvasquez.kinalapp.service.IClienteService;
import com.ludwingvasquez.kinalapp.service.IVentaService;
import com.ludwingvasquez.kinalapp.service.IUsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final IProductoService productoService;
    private final IClienteService clienteService;
    private final IVentaService ventaService;
    private final IUsuarioService usuarioService;

    public HomeController(IProductoService productoService, IClienteService clienteService,
                          IVentaService ventaService, IUsuarioService usuarioService) {
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.ventaService = ventaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        // Recuperar usuario de la sesión
        String nombreUsuario = (String) session.getAttribute("nombreUsuario");
        String emailUsuario = (String) session.getAttribute("emailUsuario");
        String rolUsuario = (String) session.getAttribute("rolUsuario");
        
        // Si no hay usuario en sesión, redirigir al login
        if (nombreUsuario == null) {
            return "redirect:/login";
        }
        
        // Agregar usuario al modelo
        model.addAttribute("nombreUsuario", nombreUsuario);
        model.addAttribute("emailUsuario", emailUsuario);
        model.addAttribute("rolUsuario", rolUsuario != null ? rolUsuario : "Usuario");
        model.addAttribute("inicialesUsuario", obtenerIniciales(nombreUsuario));
        // Estadísticas generales
        long totalProductos = productoService.listarTodos().size();
        long totalClientes = clienteService.listarTodos().size();
        long totalVentas = ventaService.listarTodos().size();
        long totalUsuarios = usuarioService.listarTodos().size();

        // Calcular ingresos totales sumando los detalles de cada venta
        List<Venta> ventas = ventaService.listarTodos();
        BigDecimal ingresosTotales = BigDecimal.ZERO;
        for (Venta venta : ventas) {
            if (venta.getDetalles() != null) {
                for (DetalleVenta detalle : venta.getDetalles()) {
                    BigDecimal subtotal = BigDecimal.valueOf(detalle.getCantidad())
                            .multiply(BigDecimal.valueOf(detalle.getPrecioUnitario()));
                    ingresosTotales = ingresosTotales.add(subtotal);
                }
            }
        }

        // Productos con stock bajo 
        long productosBajoStock = productoService.listarTodos().stream()
                .filter(p -> p.getStock() < 10)
                .count();

        // Clientes activos
        long clientesActivos = clienteService.listarPorEstado(1).size();

        // Generar notificaciones dinámicas
        List<Notificacion> notificaciones = new ArrayList<>();
        long notifId = 1;

        // Notificaciones de productos con stock bajo
        List<Producto> productosBajo = productoService.listarTodos().stream()
                .filter(p -> p.getStock() < 10)
                .limit(3)
                .collect(Collectors.toList());
        for (Producto p : productosBajo) {
            notificaciones.add(new Notificacion(
                    notifId++, "warning",
                    "Stock bajo: " + p.getNombre_producto(),
                    "Solo quedan " + p.getStock() + " unidades en stock",
                    LocalDateTime.now(), false, "package", "gold"
            ));
        }

        // Notificación de ventas recientes
        if (!ventas.isEmpty()) {
            Venta ultimaVenta = ventas.get(ventas.size() - 1);
            BigDecimal totalVenta = BigDecimal.ZERO;
            if (ultimaVenta.getDetalles() != null) {
                for (DetalleVenta d : ultimaVenta.getDetalles()) {
                    totalVenta = totalVenta.add(BigDecimal.valueOf(d.getCantidad())
                            .multiply(BigDecimal.valueOf(d.getPrecioUnitario())));
                }
            }
            notificaciones.add(new Notificacion(
                    notifId++, "success",
                    "Venta #" + ultimaVenta.getCodigoVenta() + " completada",
                    "Total: Q" + totalVenta,
                    LocalDateTime.now(), false, "dollar-sign", "success"
            ));
        }

        // Notificación de total de ingresos
        if (ingresosTotales.compareTo(BigDecimal.ZERO) > 0) {
            notificaciones.add(new Notificacion(
                    notifId++, "info",
                    "Ingresos totales: Q" + ingresosTotales,
                    "Acumulado de todas las ventas",
                    LocalDateTime.now(), false, "trending-up", "info"
            ));
        }

        // Alerta si no hay clientes
        if (totalClientes == 0) {
            notificaciones.add(new Notificacion(
                    notifId++, "alert",
                    "No hay clientes registrados",
                    "Considere agregar clientes al sistema",
                    LocalDateTime.now(), false, "users", "flame"
            ));
        }

        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("totalVentas", totalVentas);
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("ingresosTotales", ingresosTotales);
        model.addAttribute("productosBajoStock", productosBajoStock);
        model.addAttribute("clientesActivos", clientesActivos);
        model.addAttribute("ventasRecientes", ventas.size() > 5 ? ventas.subList(0, 5) : ventas);
        model.addAttribute("notificaciones", notificaciones);
        model.addAttribute("totalNotificaciones", notificaciones.size());

        return "dashboard";
    }

    @GetMapping("/inicio")
    public String inicio() {
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
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
}
