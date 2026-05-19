package com.ludwingvasquez.kinalapp.controller;

import com.ludwingvasquez.kinalapp.entity.Venta;
import com.ludwingvasquez.kinalapp.entity.DetalleVenta;
import com.ludwingvasquez.kinalapp.entity.Producto;
import com.ludwingvasquez.kinalapp.service.IClienteService;
import com.ludwingvasquez.kinalapp.service.IUsuarioService;
import com.ludwingvasquez.kinalapp.service.IVentaService;
import com.ludwingvasquez.kinalapp.service.IProductoService;
import com.ludwingvasquez.kinalapp.service.IDetalleVentaService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final IVentaService ventaService;
    private final IUsuarioService usuarioService;
    private final IClienteService clienteService;
    private final IProductoService productoService;
    private final IDetalleVentaService detalleVentaService;

    public VentaController(IVentaService ventaService, IUsuarioService usuarioService, IClienteService clienteService, IProductoService productoService, IDetalleVentaService detalleVentaService) {
        this.ventaService = ventaService;
        this.usuarioService = usuarioService;
        this.clienteService = clienteService;
        this.productoService = productoService;
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
    public String dashboard(Model model, 
                           @RequestParam(name = "periodo", required = false, defaultValue = "semana") String periodo,
                           @RequestParam(name = "tendencia", required = false, defaultValue = "mes") String tendencia) {
        List<Venta> ventas = ventaService.listarTodos();
        
        LocalDate hoy = LocalDate.now();
        double ventasHoy = ventas.stream()
                .filter(v -> v.getFechaVenta() != null && v.getFechaVenta().isEqual(hoy))
                .mapToDouble(Venta::getTotal)
                .sum();
        long ventasHoyCount = ventas.stream()
                .filter(v -> v.getFechaVenta() != null && v.getFechaVenta().isEqual(hoy))
                .count();

        double ventasMes = ventas.stream()
                .filter(v -> v.getFechaVenta() != null && v.getFechaVenta().getMonth() == hoy.getMonth() && v.getFechaVenta().getYear() == hoy.getYear())
                .mapToDouble(Venta::getTotal)
                .sum();
        long ventasMesCount = ventas.stream()
                .filter(v -> v.getFechaVenta() != null && v.getFechaVenta().getMonth() == hoy.getMonth() && v.getFechaVenta().getYear() == hoy.getYear())
                .count();

        double promedioVenta = ventas.isEmpty() ? 0.0 : ventas.stream()
                .mapToDouble(Venta::getTotal)
                .average()
                .orElse(0.0);

        long productosVendidos = ventas.stream()
                .filter(v -> v.getDetalles() != null)
                .flatMap(v -> v.getDetalles().stream())
                .filter(d -> d.getEstado() == null || d.getEstado() == 1L)
                .mapToLong(d -> d.getCantidad() != null ? d.getCantidad() : 0L)
                .sum();

        long clientesUnicos = ventas.stream()
                .filter(v -> v.getCliente() != null)
                .map(v -> v.getCliente().getDPICliente())
                .distinct()
                .count();

        long ventasActivas = ventas.stream()
                .filter(v -> v.getEstado() != null && v.getEstado() == 1L)
                .count();

        model.addAttribute("ventas", ventas);
        model.addAttribute("totalVentas", ventas.size());
        model.addAttribute("ventasHoy", ventasHoy);
        model.addAttribute("ventasHoyCount", ventasHoyCount);
        model.addAttribute("ventasMes", ventasMes);
        model.addAttribute("ventasMesCount", ventasMesCount);
        model.addAttribute("promedioVenta", promedioVenta);
        model.addAttribute("productosVendidos", productosVendidos);
        model.addAttribute("clientesUnicos", clientesUnicos);
        model.addAttribute("ventasActivas", ventasActivas);

        model.addAttribute("periodoSeleccionado", periodo);
        model.addAttribute("tendenciaSeleccionada", tendencia);
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
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("productos", productoService.listarTodos());
        return "ventas/formulario";
    }

    @GetMapping("/{codigoVenta}")
    public String buscarCV(@PathVariable Long codigoVenta, Model model) {
        Venta venta = ventaService.buscarCV(codigoVenta).orElse(null);
        model.addAttribute("venta", venta);
        return "ventas/detalle";
    }

    @PostMapping
    public String guardar(@ModelAttribute Venta venta,
                          @RequestParam(value = "productoId", required = false) List<Integer> productoIds,
                          @RequestParam(value = "cantidad", required = false) List<Integer> cantidades,
                          @RequestParam(value = "precioUnitario", required = false) List<Double> preciosUnitarios) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                String nombreUsuario = auth.getName();
                usuarioService.buscarPorUsername(nombreUsuario).ifPresent(venta::setUsuario);
            }
            ventaService.guardar(venta);

            if (productoIds != null && !productoIds.isEmpty()) {
                for (int i = 0; i < productoIds.size(); i++) {
                    Integer prodId = productoIds.get(i);
                    if (prodId != null) {
                        DetalleVenta detalle = new DetalleVenta();
                        detalle.setVenta(venta);
                        productoService.buscarPorCodigo(prodId).ifPresent(detalle::setProducto);
                        
                        Integer cant = (cantidades != null && cantidades.size() > i) ? cantidades.get(i) : 1;
                        Double precio = (preciosUnitarios != null && preciosUnitarios.size() > i) ? preciosUnitarios.get(i) : 0.0;
                        
                        detalle.setCantidad(cant);
                        detalle.setPrecioUnitario(precio);
                        detalle.setEstado(1L);
                        detalleVentaService.guardar(detalle);
                    }
                }
            }

            if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
                return "redirect:/dashboard?success=true";
            }
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
        model.addAttribute("clientes", clienteService.listarTodos());
        return "ventas/formulario";
    }

    @PostMapping("/actualizar/{codigoVenta}")
    public String actualizar(@PathVariable Long codigoVenta, @ModelAttribute Venta venta) {
        try {
            Venta ventaOriginal = ventaService.buscarCV(codigoVenta).orElse(null);
            if (ventaOriginal != null) {
                venta.setUsuario(ventaOriginal.getUsuario());
                if (venta.getFechaVenta() == null) {
                    venta.setFechaVenta(ventaOriginal.getFechaVenta());
                }
                if (venta.getEstado() == null) {
                    venta.setEstado(ventaOriginal.getEstado());
                }
            }
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