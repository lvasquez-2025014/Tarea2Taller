package com.ludwingvasquez.kinalapp.controller;

import com.ludwingvasquez.kinalapp.entity.Usuario;
import com.ludwingvasquez.kinalapp.service.IUsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class LoginController {

    private final IUsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public LoginController(IUsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String raiz() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model,
                        @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return "redirect:/dashboard";
        }
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("mensaje", "Has cerrado sesión exitosamente");
        }
        return "login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String email,
                           Model model,
                           RedirectAttributes redirectAttrs) {

        Optional<Usuario> existente = usuarioService.buscarPorUsername(username);
        if (existente.isPresent()) {
            model.addAttribute("error", "El usuario ya existe en la base de datos");
            return "registro";
        }
        if (password.length() < 8) {
            model.addAttribute("error", "La contraseña debe tener al menos 8 caracteres");
            return "registro";
        }
        if (!email.contains("@") || !email.contains(".")) {
            model.addAttribute("error", "El email debe ser válido (contener @ y .)");
            return "registro";
        }

        try {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setUsername(username);
            nuevoUsuario.setPassword(passwordEncoder.encode(password));
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setRol("USER");
            nuevoUsuario.setEstado(1L);
            usuarioService.guardar(nuevoUsuario);

            redirectAttrs.addFlashAttribute("toastExito", "¡FELICIDADES! TE HAS REGISTRADO EXITOSAMENTE");
            redirectAttrs.addFlashAttribute("toastTipo", "registro");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar: " + e.getMessage());
            return "registro";
        }
    }
}
