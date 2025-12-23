package com.arbre32.api.game;

import com.arbre32.api.game.dto.GameStateDTO;
import com.arbre32.api.game.dto.GameStateDTO.PlayerDTO;
import com.arbre32.api.security.JwtAuthFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
@AutoConfigureMockMvc(addFilters = false) // 🔥 pas de JWT
class GameControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    GameService gameService;

    @MockitoBean
    JwtAuthFilter jwtAuthFilter;

    // --------------------
    // CREATE GAME
    // --------------------
    @Test
    void shouldCreateGame() throws Exception {
        GameStateDTO dto = new GameStateDTO();
        dto.gameId = "game-123";

        when(gameService.createGame(anyInt())).thenReturn(dto);

        mockMvc.perform(post("/api/game/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            { "mode": 1 }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value("game-123"));
    }


    // --------------------
    // GET GAME STATE
    // --------------------
    @Test
    void shouldReturnGameState() throws Exception {
        GameStateDTO dto = new GameStateDTO();
        dto.gameId = "game-123";
        dto.currentPlayer = "Alice";

        when(gameService.getState("game-123")).thenReturn(dto);

        mockMvc.perform(get("/api/game/game-123/state"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value("game-123"))
                .andExpect(jsonPath("$.currentPlayer").value("Alice"));
    }

    // --------------------
    // GAME NOT FOUND
    // --------------------
    @Test
    void shouldReturn404IfGameNotFound() throws Exception {
        when(gameService.getState("unknown")).thenReturn(null);

        mockMvc.perform(get("/api/game/unknown/state"))
                .andExpect(status().isNotFound());
    }

    // --------------------
    // JOIN GAME
    // --------------------
    @Test
    void shouldJoinGame() throws Exception {
        GameStateDTO dto = new GameStateDTO();
        dto.gameId = "game-123";

        PlayerDTO p = new PlayerDTO();
        p.id = "p1";
        p.name = "Alice";
        p.score = 0;

        dto.players = List.of(p);

        when(gameService.joinGame("game-123", "Alice"))
                .thenReturn(dto);

        mockMvc.perform(post("/api/game/game-123/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "userHandle": "Alice" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.players[0].name").value("Alice"));
    }

    // --------------------
    // PLAY CARD (ERROR)
    // --------------------
    @Test
    void shouldReturnErrorWhenPlayFails() throws Exception {
        GameService.PlayResult error = GameService.PlayResult.error(400, "Coup invalide");

        when(gameService.play(any(), any(), any()))
                .thenReturn(error);

        mockMvc.perform(post("/api/game/game-123/play")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "cardId": "8♦-123",
                                  "userHandle": "Alice"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Coup invalide"));
    }
}
