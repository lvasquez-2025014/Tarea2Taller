package com.ludwingvasquez.kinalapp.config;

import com.ludwingvasquez.kinalapp.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos públicos
                .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()
                // Rutas públicas
                .requestMatchers("/", "/login", "/registro", "/setup", "/error", "/acceso-denegado").permitAll()
                // Dashboard principal: accesible para todos los roles
                .requestMatchers("/dashboard").hasAnyRole("ADMIN", "USER", "CLIENTE", "RECURSOS_HUMANOS")
                
                // ============================================================
                // 1. ADMIN: ACCESO TOTAL A TODO (CRUD completo)
                // ============================================================
                // Usuarios: ADMIN tiene control total
                .requestMatchers("/usuarios/editar/**", "/usuarios/eliminar/**", "/usuarios/actualizar/**").hasRole("ADMIN")
                // Clientes: ADMIN tiene control total
                .requestMatchers("/clientes/editar/**", "/clientes/eliminar/**", "/clientes/actualizar/**").hasRole("ADMIN")
                // Productos: ADMIN tiene control total
                .requestMatchers("/productos/editar/**", "/productos/eliminar/**").hasRole("ADMIN")
                // Ventas: ADMIN puede eliminar y editar
                .requestMatchers("/ventas/editar/**", "/ventas/eliminar/**").hasRole("ADMIN")
                
                // ============================================================
                // 2. USER: Ver TODO excepto gestión de usuarios
                //    Puede: ver/crear clientes, ver/crear productos, ver/crear ventas
                //    NO puede: ver ni gestionar usuarios (solo ADMIN)
                // ============================================================
                // Usuarios: SOLO ADMIN puede ver y gestionar (USER no tiene acceso)
                .requestMatchers("/usuarios", "/usuarios/dashboard", "/usuarios/nuevo", "/usuarios/**").hasRole("ADMIN")
                // Clientes: USER puede ver y crear (editar solo si no es sensible)
                .requestMatchers("/clientes", "/clientes/dashboard", "/clientes/nuevo", "/clientes/**").hasAnyRole("ADMIN", "USER", "RECURSOS_HUMANOS")
                // Productos: USER puede ver y crear
                .requestMatchers("/productos", "/productos/dashboard", "/productos/nuevo", "/productos/**").hasAnyRole("ADMIN", "USER")
                // Ventas: USER puede ver y crear
                .requestMatchers("/ventas", "/ventas/dashboard", "/ventas/nueva", "/ventas/guardar", "/ventas/**").hasAnyRole("ADMIN", "USER")
                
                // ============================================================
                // 3. CLIENTE: Solo puede ver productos y crear ventas (compras)
                // ============================================================
                .requestMatchers("/productos", "/productos/dashboard", "/productos/detalle/**").hasAnyRole("ADMIN", "USER", "CLIENTE")
                .requestMatchers("/ventas/nueva", "/ventas/guardar", "/ventas/mis-ventas/**").hasAnyRole("ADMIN", "USER", "CLIENTE")
                
                // ============================================================
                // 4. RECURSOS_HUMANOS: Gestiona clientes y puede ver dashboard
                // ============================================================
                // Ya cubierto arriba con hasAnyRole en clientes
                
                // Cualquier otra solicitud requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler((request, response, authentication) -> {
                    System.out.println("=== LOGIN EXITOSO: Redirigiendo a /dashboard ===");
                    response.sendRedirect("/dashboard");
                })
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
                .logoutRequestMatcher(request -> request.getRequestURI().equals("/logout")) //Permitir GET y POST
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .expiredUrl("/login?expired=true")
            )
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/acceso-denegado")
            )
            .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
