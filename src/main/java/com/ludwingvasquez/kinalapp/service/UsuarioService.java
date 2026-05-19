package com.ludwingvasquez.kinalapp.service;

import com.ludwingvasquez.kinalapp.entity.Usuario;
import com.ludwingvasquez.kinalapp.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class    UsuarioService implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long codigoUsuario) {
        return usuarioRepository.findById(codigoUsuario);
    }

    @Override
    @Transactional
    public Usuario guardar(Usuario usuario) {
        if (usuario.getEstado() == null) {
            usuario.setEstado(1L);
        }
        validarUsuario(usuario);
        
        if (!isBCryptHash(usuario.getPassword())) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario actualizar(Long codigoUsuario, Usuario usuario) {
        Usuario existente = usuarioRepository.findById(codigoUsuario)
                .orElseThrow(() -> new RuntimeException("El usuario no se encontró con el código: " + codigoUsuario));

        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            usuario.setPassword(existente.getPassword());
        }

        usuario.setCodigoUsuario(codigoUsuario);
        validarUsuario(usuario);

        if (!isBCryptHash(usuario.getPassword())) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void eliminar(Long codigoUsuario) {
        if (!usuarioRepository.existsById(codigoUsuario)) {
            throw new RuntimeException("No se puede eliminar: el usuario " + codigoUsuario + " no existe.");
        }
        usuarioRepository.deleteById(codigoUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorCodigo(Long codigoUsuario) {
        return usuarioRepository.existsById(codigoUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    private void validarUsuario(Usuario usuario) {
        Optional.ofNullable(usuario.getUsername())
                .filter(un -> !un.trim().isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("El nombre de usuario es obligatorio"));

        Optional.ofNullable(usuario.getPassword())
                .filter(pass -> pass.trim().length() >= 8)
                .orElseThrow(() -> new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres"));

        Optional.ofNullable(usuario.getEmail())
                .filter(email -> email.contains("@") && email.contains("."))
                .orElseThrow(() -> new IllegalArgumentException("El formato del correo electrónico es inválido"));

        Optional.ofNullable(usuario.getEstado())
                .orElseThrow(() -> new IllegalArgumentException("El estado del usuario es un campo obligatorio"));
    }

    private boolean isBCryptHash(String password) {
        if (password == null) return false;
        return password.length() == 60 && (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }
}