package com.ludwingvasquez.kinalapp.controller;

import com.ludwingvasquez.kinalapp.entity.Usuario;
import com.ludwingvasquez.kinalapp.service.IUsuarioService;
import com.ludwingvasquez.kinalapp.service.IVentaService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final IUsuarioService usuarioService;
    private final IVentaService ventaService;

    public UsuarioController(IUsuarioService usuarioService, IVentaService ventaService) {
        this.usuarioService = usuarioService;
        this.ventaService = ventaService;
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

    private boolean esAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        if (!esAdmin()) {
            return "acceso-denegado";
        }
        List<Usuario> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        return "usuarios/dashboard";
    }

    @GetMapping
    public String listar(Model model) {
        if (!esAdmin()) {
            return "acceso-denegado";
        }
        List<Usuario> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        if (!esAdmin()) {
            return "acceso-denegado";
        }
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("editar", false);
        return "usuarios/formulario";
    }

    @GetMapping("/{codigo_usuario}")
    public String buscarPorId(@PathVariable Long codigo_usuario, Model model) {
        if (!esAdmin()) {
            return "acceso-denegado";
        }
        Usuario usuario = usuarioService.buscarPorId(codigo_usuario).orElse(null);
        model.addAttribute("usuario", usuario);
        return "usuarios/detalle";
    }

    @PostMapping
    public String guardar(@ModelAttribute Usuario usuario) {
        if (!esAdmin()) {
            return "acceso-denegado";
        }
        try {
            usuarioService.guardar(usuario);
            return "redirect:/usuarios";
        } catch (IllegalArgumentException e) {
            return "redirect:/usuarios/nuevo?error=" + e.getMessage();
        }
    }

    @GetMapping("/editar/{codigo_usuario}")
    public String editar(@PathVariable Long codigo_usuario, Model model) {
        if (!esAdmin()) {
            return "acceso-denegado";
        }
        Usuario usuario = usuarioService.buscarPorId(codigo_usuario).orElse(null);
        model.addAttribute("usuario", usuario);
        model.addAttribute("editar", true);
        return "usuarios/formulario";
    }

    @PostMapping("/actualizar/{codigo_usuario}")
    public String actualizar(@PathVariable Long codigo_usuario, @ModelAttribute Usuario usuario) {
        if (!esAdmin()) {
            return "acceso-denegado";
        }
        try {
            usuarioService.actualizar(codigo_usuario, usuario);
            return "redirect:/usuarios";
        } catch (RuntimeException e) {
            return "redirect:/usuarios/editar/" + codigo_usuario + "?error=" + e.getMessage();
        }
    }

    @GetMapping("/eliminar/{codigo_usuario}")
    public String eliminar(@PathVariable Long codigo_usuario) {
        if (!esAdmin()) {
            return "acceso-denegado";
        }
        try {
            if (!usuarioService.buscarPorId(codigo_usuario).isPresent()) {
                return "redirect:/usuarios?error=Usuario no encontrado";
            }
            // Verificar si el usuario tiene ventas asociadas
            long ventasAsociadas = ventaService.contarVentasPorUsuario(codigo_usuario);
            if (ventasAsociadas > 0) {
                return "redirect:/usuarios?error=No se puede eliminar el usuario porque tiene " + ventasAsociadas + " venta(s) asociada(s)";
            }
            usuarioService.eliminar(codigo_usuario);
            return "redirect:/usuarios";
        } catch (RuntimeException e) {
            return "redirect:/usuarios?error=" + e.getMessage();
        }
    }
}