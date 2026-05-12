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
                .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()
                .requestMatchers("/", "/login", "/registro", "/setup", "/error", "/acceso-denegado").permitAll()
                .requestMatchers("/dashboard").hasAnyRole("ADMIN", "USER", "CLIENTE", "RECURSOS_HUMANOS")
                
                .requestMatchers("/usuarios/editar/**", "/usuarios/eliminar/**", "/usuarios/actualizar/**").hasRole("ADMIN")
                .requestMatchers("/clientes/editar/**", "/clientes/eliminar/**", "/clientes/actualizar/**").hasRole("ADMIN")
                .requestMatchers("/productos/editar/**", "/productos/eliminar/**").hasRole("ADMIN")
                .requestMatchers("/ventas/editar/**", "/ventas/eliminar/**").hasRole("ADMIN")

                .requestMatchers("/usuarios", "/usuarios/dashboard", "/usuarios/nuevo", "/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/clientes", "/clientes/dashboard", "/clientes/nuevo", "/clientes/**").hasAnyRole("ADMIN", "USER", "RECURSOS_HUMANOS")
                .requestMatchers("/productos", "/productos/dashboard", "/productos/nuevo", "/productos/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/ventas", "/ventas/dashboard", "/ventas/nueva", "/ventas/guardar", "/ventas/**").hasAnyRole("ADMIN", "USER")
                
                .requestMatchers("/productos", "/productos/dashboard", "/productos/detalle/**").hasAnyRole("ADMIN", "USER", "CLIENTE")
                .requestMatchers("/ventas/nueva", "/ventas/guardar", "/ventas/mis-ventas/**").hasAnyRole("ADMIN", "USER", "CLIENTE")
                
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler((request, response, authentication) -> {
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
