package com.arbre32.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtAuthFilter jwt,
            CorsConfigurationSource corsConfigurationSource
    ) throws Exception {

        http
                // CSRF inutile avec JWT
                .csrf(csrf -> csrf.disable())

                // CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // Stateless (JWT)
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Autorisations
                .authorizeHttpRequests(auth -> auth

                        // 🔥 ACTUATOR (Prometheus / Health)
                     /*   .requestMatchers(
                                "/actuator/prometheus",
                                "/actuator/health"
                        ).permitAll()*/
                        .requestMatchers("/actuator/prometheus").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        // 🔓 Public
                        .requestMatchers(
                                "/auth/**",
                                "/ws/**",
                                "/app/**",
                                "/topic/**",
                                "/api/lobby/open",
                                "/api/chat/**",
                                "/api/game/**",
                                "/api/games",
                                "/api/admin/**",
                                "/",
                                "/index.html",
                                "/assets/**",
                                "/static/**"
                        ).permitAll()

                        // 🔒 Le reste protégé
                        .anyRequest().authenticated()
                )

                // Filtre JWT
                .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
