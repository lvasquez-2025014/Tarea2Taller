package com.ludwingvasquez.kinalapp.controller;

import com.ludwingvasquez.kinalapp.entity.Usuario;
import com.ludwingvasquez.kinalapp.service.IUsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    private final IUsuarioService usuarioService;

    public LoginController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String raiz() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String username, 
                              @RequestParam String password, 
                              Model model,
                              HttpSession session,
                              RedirectAttributes redirectAttrs) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorUsername(username);
        
        if (usuarioOpt.isPresent() && usuarioOpt.get().getPassword().equals(password)) {
            Usuario usuario = usuarioOpt.get();
            session.setAttribute("usuarioActual", usuario);
            session.setAttribute("nombreUsuario", usuario.getUsername());
            session.setAttribute("emailUsuario", usuario.getEmail());
            session.setAttribute("rolUsuario", usuario.getRol());
            // Mensajes de bienvenida
            redirectAttrs.addFlashAttribute("toastBienvenida", "BIENVENIDO A KINAL APP, " + usuario.getUsername().toUpperCase());
            redirectAttrs.addFlashAttribute("toastNovedades", "Puedes revisar las nuevas novedades en el dashboard");
            redirectAttrs.addFlashAttribute("toastTipo", "success");
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "login";
        }
    }

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String email,
                           Model model,
                           RedirectAttributes redirectAttrs) {
        
        // Verifica si el usuario ya existe
        Optional<Usuario> existente = usuarioService.buscarPorUsername(username);
        if (existente.isPresent()) {
            model.addAttribute("error", "El usuario ya existe en la base de datos");
            return "registro";
        }
        
        // Validaciones
        if (password.length() < 8) {
            model.addAttribute("error", "La contraseña debe tener al menos 8 caracteres");
            return "registro";
        }
        
        if (!email.contains("@") || !email.contains(".")) {
            model.addAttribute("error", "El email debe ser válido (contener @ y .)");
            return "registro";
        }
        
        // Guardar en base de datos
        try {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setUsername(username);
            nuevoUsuario.setPassword(password);
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setRol("USER");
            nuevoUsuario.setEstado(1L);
            usuarioService.guardar(nuevoUsuario);
            
            // Mensaje de éxito con flash attribute
            redirectAttrs.addFlashAttribute("toastExito", "¡FELICIDADES! TE HAS REGISTRADO EXITOSAMENTE");
            redirectAttrs.addFlashAttribute("toastTipo", "registro");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar: " + e.getMessage());
            return "registro";
        }   
    }
}
