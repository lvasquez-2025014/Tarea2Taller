package com.ludwingvasquez.kinalapp.config;

import com.ludwingvasquez.kinalapp.entity.Usuario;
import com.ludwingvasquez.kinalapp.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "admin12345";
    public static final String ADMIN_EMAIL = "admin@kinalapp.com";
    public static final String ADMIN_ROL = "ADMIN";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        usuarioRepository.findByUsername(ADMIN_USERNAME).ifPresentOrElse(
            existente -> {
                boolean cambio = false;
                if (existente.getRol() == null || !ADMIN_ROL.equalsIgnoreCase(existente.getRol())) {
                    existente.setRol(ADMIN_ROL);
                    cambio = true;
                }
                if (existente.getEstado() == null || existente.getEstado() != 1L) {
                    existente.setEstado(1L);
                    cambio = true;
                }
                if (existente.getPassword() == null
                        || !passwordEncoder.matches(ADMIN_PASSWORD, existente.getPassword())) {
                    existente.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
                    cambio = true;
                }
                if (cambio) {
                    usuarioRepository.save(existente);
                }
            },
            () -> {
                Usuario admin = new Usuario();
                admin.setUsername(ADMIN_USERNAME);
                admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
                admin.setEmail(ADMIN_EMAIL);
                admin.setRol(ADMIN_ROL);
                admin.setEstado(1L);
                usuarioRepository.save(admin);
            }
        );
    }
}
