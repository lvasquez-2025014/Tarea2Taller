package com.ludwingvasquez.kinalapp.config;

import com.ludwingvasquez.kinalapp.entity.Usuario;
import com.ludwingvasquez.kinalapp.service.IUsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InitialSetupService {

    private final IUsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public InitialSetupService(IUsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public boolean requiereConfiguracionInicial() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        return usuarios.isEmpty();
    }

    public void crearPrimerAdmin(String username, String password, String email) {
        if (!requiereConfiguracionInicial()) {
            throw new IllegalStateException("El sistema ya tiene usuarios configurados");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (usuarioService.buscarPorUsername(username).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }


        Usuario admin = new Usuario();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setEmail(email);
        admin.setRol("ADMIN");
        admin.setEstado(1L);

        usuarioService.guardar(admin);
    }
}
