package com.ludwingvasquez.kinalapp.service;

import com.ludwingvasquez.kinalapp.entity.Usuario;
import com.ludwingvasquez.kinalapp.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("=== AUTENTICACIÓN: Buscando usuario: '" + username + "' ===");
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

        if (usuarioOpt.isEmpty()) {
            System.out.println("=== AUTENTICACIÓN: Usuario no encontrado: '" + username + "' ===");
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar que el usuario esté activo (estado = 1)
        if (usuario.getEstado() == null || usuario.getEstado() != 1) {
            throw new UsernameNotFoundException("Usuario inactivo o no válido: " + username);
        }

        // Obtener el rol del usuario y convertirlo a autoridad de Spring Security
        String rol = usuario.getRol();
        if (rol == null || rol.isEmpty()) {
            rol = "USER"; // Rol por defecto
        }

        // Spring Security requiere que los roles tengan el prefijo "ROLE_"
        Collection<? extends GrantedAuthority> authorities = 
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol.toUpperCase()));

        System.out.println("=== AUTENTICACIÓN: Usuario autenticado exitosamente: '" + username + "' con rol: " + rol + " ===");
        
        return new User(
            usuario.getUsername(),
            usuario.getPassword(),
            true,  // enabled
            true,  // accountNonExpired
            true,  // credentialsNonExpired
            true,  // accountNonLocked
            authorities
        );
    }
}
