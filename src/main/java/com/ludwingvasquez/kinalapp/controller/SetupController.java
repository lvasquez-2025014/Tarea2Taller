package com.ludwingvasquez.kinalapp.controller;

import com.ludwingvasquez.kinalapp.config.InitialSetupService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SetupController {

    private final InitialSetupService initialSetupService;

    public SetupController(InitialSetupService initialSetupService) {
        this.initialSetupService = initialSetupService;
    }

    /**
     * Muestra la página de configuración inicial solo si no hay usuarios.
     */
    @GetMapping("/setup")
    public String mostrarSetup(Model model) {
        if (!initialSetupService.requiereConfiguracionInicial()) {
            return "redirect:/login";
        }
        return "setup";
    }

    /**
     * Procesa la creación del primer administrador.
     */
    @PostMapping("/setup")
    public String procesarSetup(@RequestParam String username,
                                  @RequestParam String password,
                                  @RequestParam String email,
                                  Model model,
                                  RedirectAttributes redirectAttrs) {
        if (!initialSetupService.requiereConfiguracionInicial()) {
            return "redirect:/login";
        }

        if (username.length() < 3) {
            model.addAttribute("error", "El nombre de usuario debe tener al menos 3 caracteres");
            return "setup";
        }

        if (password.length() < 8) {
            model.addAttribute("error", "La contraseña debe tener al menos 8 caracteres");
            return "setup";
        }

        if (!email.contains("@") || !email.contains(".")) {
            model.addAttribute("error", "El email debe ser válido");
            return "setup";
        }

        try {
            initialSetupService.crearPrimerAdmin(username, password, email);
            redirectAttrs.addFlashAttribute("mensaje", "Configuración inicial completada. Ahora puedes iniciar sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear el administrador: " + e.getMessage());
            return "setup";
        }
    }
}
