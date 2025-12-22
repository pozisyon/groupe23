package com.arbre32.api.game;

import com.arbre32.api.security.JwtService;
import com.arbre32.api.user.AuthController;
import com.arbre32.api.user.PlayerAccount;
import com.arbre32.api.user.PlayerAccountRepository;
import com.arbre32.api.user.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // 🔥 désactive Spring Security
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // 🔹 Dépendances mockées du AuthController
    @MockitoBean
    private PlayerAccountRepository repo;

    @MockitoBean
    private PasswordEncoder enc;

    @MockitoBean
    private JwtService jwt;

    // =====================================================
    // REGISTER — SUCCESS
    // =====================================================
    @Test
    void shouldRegisterUserSuccessfully() throws Exception {

        when(repo.findByEmail("roro@test.com"))
                .thenReturn(Optional.empty());
        when(repo.findByHandle("Roro"))
                .thenReturn(Optional.empty());
        when(enc.encode("123456"))
                .thenReturn("hashed-password");
        when(jwt.generate(any(), any(), any()))
                .thenReturn("fake-jwt-token");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "email": "roro@test.com",
                  "handle": "Roro",
                  "password": "123456"
                }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.user.handle").value("Roro"))
                .andExpect(jsonPath("$.user.email").value("roro@test.com"))
                .andExpect(jsonPath("$.user.role").value("USER"));
    }

    // =====================================================
    // REGISTER — CHAMPS MANQUANTS
    // =====================================================
    @Test
    void shouldFailRegisterWhenFieldsMissing() throws Exception {

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "email": "roro@test.com"
                }
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Champs requis"));
    }

    // =====================================================
    // REGISTER — EMAIL DÉJÀ UTILISÉ
    // =====================================================
    @Test
    void shouldFailRegisterWhenEmailAlreadyUsed() throws Exception {

        when(repo.findByEmail("roro@test.com"))
                .thenReturn(Optional.of(new PlayerAccount()));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "email": "roro@test.com",
                  "handle": "Roro",
                  "password": "123456"
                }
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email déjà utilisé"));
    }

    // =====================================================
    // REGISTER — HANDLE DÉJÀ UTILISÉ
    // =====================================================
    @Test
    void shouldFailRegisterWhenHandleAlreadyUsed() throws Exception {

        when(repo.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(repo.findByHandle("Roro"))
                .thenReturn(Optional.of(new PlayerAccount()));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "email": "roro@test.com",
                  "handle": "Roro",
                  "password": "123456"
                }
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Handle déjà utilisé"));
    }

    // =====================================================
    // LOGIN — SUCCESS
    // =====================================================
    @Test
    void shouldLoginSuccessfully() throws Exception {

        PlayerAccount p = new PlayerAccount();
        p.setId(UUID.randomUUID());
        p.setHandle("Roro");
        p.setEmail("roro@test.com");
        p.setPasswordHash("hashed-password");
        p.setRole("USER");

        when(repo.findByEmail("roro@test.com"))
                .thenReturn(Optional.of(p));
        when(enc.matches("123456", "hashed-password"))
                .thenReturn(true);
        when(jwt.generate(any(), any(), any()))
                .thenReturn("fake-jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "email": "roro@test.com",
                  "password": "123456"
                }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.user.handle").value("Roro"))
                .andExpect(jsonPath("$.user.role").value("USER"));
    }

    // =====================================================
    // LOGIN — IDENTIFIANTS INVALIDES
    // =====================================================
    @Test
    void shouldFailLoginWhenCredentialsInvalid() throws Exception {

        when(repo.findByEmail("roro@test.com"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "email": "roro@test.com",
                  "password": "wrong"
                }
                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Identifiants invalides"));
    }

    // =====================================================
    // SECURE TEST ENDPOINT
    // =====================================================
    @Test
    void shouldAccessSecureTestEndpoint() throws Exception {

        mockMvc.perform(get("/auth/secure/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK Secure"));
    }
}
