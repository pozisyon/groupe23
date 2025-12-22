package com.arbre32.api.game;

import com.arbre32.api.game.dto.GameStateDTO;
import com.arbre32.api.security.JwtService;
import com.arbre32.api.user.PlayerAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)          // ✅ UNIQUEMENT ÇA
@AutoConfigureMockMvc(addFilters = false)  // ✅ OK
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService service;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private PlayerAccountRepository playerAccountRepository;

    @Test
    void shouldCreateGame() throws Exception {
        GameStateDTO dto = new GameStateDTO();
        dto.gameId = "game123";

        when(service.createGame(1)).thenReturn(dto);

        mockMvc.perform(post("/api/game/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                { "mode": 1 }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value("game123"));
    }
}
