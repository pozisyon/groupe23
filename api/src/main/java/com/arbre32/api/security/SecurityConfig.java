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
@Configuration @EnableMethodSecurity
public class SecurityConfig {
  @Bean public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwt) throws Exception {
    http.csrf(csrf->csrf.disable())
       .cors(c->{})
       .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/auth/**",
                            "/ws/**",
                            "/app/**",
                            "/topic/**",
                            "/api/lobby/open",
                            "/api/chat/**",
                            "/api/game/*/join"
                    ).permitAll()
                    .anyRequest().authenticated()
            )

            .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
  @Bean public PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }
}
