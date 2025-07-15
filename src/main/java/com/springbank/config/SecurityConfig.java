package com.springbank.config;

import com.springbank.entity.Token;
import com.springbank.entity.Usuario;
import com.springbank.exception.TokenInvalidoException;
import com.springbank.exception.UsernameNoEncontradoException;
import com.springbank.repository.TokenRepository;
import com.springbank.repository.UsuarioRepository;
import com.springbank.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration //Anotacion que nos dice que vamos a proporcionar un bean de configuracion, en esta caso la seguridad
@EnableMethodSecurity
public class SecurityConfig {

    private final UsuarioRepository usuarioRepository;
    private final TokenRepository tokenRepository;
    private JwtService jwtService;

    @Autowired
    public SecurityConfig(UsuarioRepository usuarioRepository, TokenRepository tokenRepository, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, tokenRepository, usuarioRepository, userDetailsService());
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            final Usuario user = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNoEncontradoException("Usuario no encontrado."));
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRol().name())
                    .build();
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth
                        -> auth.requestMatchers(
                        "/api/v1/auth/**",
                        "/api/test/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                ).permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session
                .sessionCreationPolicy(STATELESS)
                ).authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .logout(logout
                        -> logout.logoutUrl("/api/v1/auth/logout")
                        .addLogoutHandler((request, response, authentication) -> {
                            final var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                            logout(authHeader);
                        })
                        .logoutSuccessHandler((request, response, authentication)
                                -> SecurityContextHolder.clearContext())
                );

        return http.build();
    }

    private void logout(final String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new TokenInvalidoException("Token inválido (logout).");
        }

        final String jwtToken = token.substring(7);
        final Token foundToken = tokenRepository.findByToken(jwtToken);

        if (foundToken == null) {
            throw new TokenInvalidoException("Token no encontrado en la base de datos.");
        }

        foundToken.setExpired(true);
        foundToken.setRevoked(true);
        tokenRepository.save(foundToken);
    }

}
