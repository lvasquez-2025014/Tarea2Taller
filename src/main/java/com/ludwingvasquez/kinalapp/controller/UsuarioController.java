package com.ludwingvasquez.kinalapp.controller;

import com.ludwingvasquez.kinalapp.entity.Usuario;
import com.ludwingvasquez.kinalapp.service.IUsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final IUsuarioService usuarioService;

    public UsuarioController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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
        List<Usuario> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        return "usuarios/dashboard";
    }

    @GetMapping
    public String listar(Model model) {
        List<Usuario> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("editar", false);
        return "usuarios/formulario";
    }

    @GetMapping("/{codigo_usuario}")
    public String buscarPorId(@PathVariable Long codigo_usuario, Model model) {
        Usuario usuario = usuarioService.buscarPorId(codigo_usuario).orElse(null);
        model.addAttribute("usuario", usuario);
        return "usuarios/detalle";
    }

    @PostMapping
    public String guardar(@ModelAttribute Usuario usuario) {
        try {
            usuarioService.guardar(usuario);
            return "redirect:/usuarios";
        } catch (IllegalArgumentException e) {
            return "redirect:/usuarios/nuevo?error=" + e.getMessage();
        }
    }

    @GetMapping("/editar/{codigo_usuario}")
    public String editar(@PathVariable Long codigo_usuario, Model model) {
        Usuario usuario = usuarioService.buscarPorId(codigo_usuario).orElse(null);
        model.addAttribute("usuario", usuario);
        model.addAttribute("editar", true);
        return "usuarios/formulario";
    }

    @PostMapping("/actualizar/{codigo_usuario}")
    public String actualizar(@PathVariable Long codigo_usuario, @ModelAttribute Usuario usuario) {
        try {
            usuarioService.actualizar(codigo_usuario, usuario);
            return "redirect:/usuarios";
        } catch (RuntimeException e) {
            return "redirect:/usuarios/editar/" + codigo_usuario + "?error=" + e.getMessage();
        }
    }

    @GetMapping("/eliminar/{codigo_usuario}")
    public String eliminar(@PathVariable Long codigo_usuario) {
        try {
            usuarioService.eliminar(codigo_usuario);
            return "redirect:/usuarios";
        } catch (RuntimeException e) {
            return "redirect:/usuarios?error=" + e.getMessage();
        }
    }
}