package com.arbre32.api.game;

import com.arbre32.api.game.dto.GameStateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class GameServiceTest {

    private GameService gameService;
    private SimpMessagingTemplate broker;

    @BeforeEach
    void setup() {
        // 🔥 On mock le broker WebSocket
        broker = Mockito.mock(SimpMessagingTemplate.class);

        // 🔥 Injection manuelle (test unitaire)
        gameService = new GameService(broker);
    }

    @Test
    void shouldCreateGame() {
        GameStateDTO dto = gameService.createGame(1);

        assertThat(dto).isNotNull();
        assertThat(dto.gameId).isNotBlank();
        assertThat(dto.gameOver).isFalse();
        assertThat(dto.round).isEqualTo(1);
    }


    //verification envoi websocket
  /*  @Test
    void shouldNotifyPlayersWhenGameCreated() {
        gameService.createGame(1);

        Mockito.verify(broker, Mockito.atLeastOnce())
                .convertAndSend(Mockito.anyString(), Optional.ofNullable(Mockito.any()));
    }
*/
    @Test
    void shouldJoinGame() {
        var game = gameService.createGame(1);
        var updated = gameService.joinGame(game.gameId, "Alice");

        assertThat(updated.players).hasSize(1);
        assertThat(updated.players.get(0).name).isEqualTo("Alice");
    }

    @Test
    void shouldReturnNullWhenJoiningUnknownGame() {
        var result = gameService.joinGame("unknown", "Bob");

        assertThat(result).isNull();
    }


    @Test
    void shouldIgnoreWhenSamePlayerJoinsTwice() {
        var game = gameService.createGame(1);

        gameService.joinGame(game.gameId, "Alice");
        var result = gameService.joinGame(game.gameId, "Alice");

        assertThat(result.players).hasSize(1);
        assertThat(result.players.get(0).name).isEqualTo("Alice");
    }


    @Test
    void shouldReturnGameState() {
        var game = gameService.createGame(1);

        var state = gameService.getState(game.gameId);

        assertThat(state).isNotNull();
        assertThat(state.gameId).isEqualTo(game.gameId);
    }

    @Test
    void shouldReturnNullForUnknownGame() {
        assertThat(gameService.getState("unknown")).isNull();
    }

   /* @Test
    void shouldPlayCardSuccessfully() {
        var game = gameService.createGame(1);
        gameService.joinGame(game.gameId, "Alice");

        var result = gameService.play(game.gameId, "8♦", "Alice");

        assertThat(result.status()).isEqualTo(200);
        assertThat(result.state()).isNotNull();
    }

    @Test
    void shouldFailWhenPlayerNotInGame() {
        var game = gameService.createGame(1);

        var res = gameService.play(game.gameId, "8♦", "Bob");

        assertThat(res.status()).isEqualTo(403);
    }

    @Test
    void shouldFailWhenCardNotPlayable() {
        var game = gameService.createGame(1);
        gameService.joinGame(game.gameId, "Alice");

        var res = gameService.play(game.gameId, "INVALID", "Alice");

        assertThat(res.status()).isEqualTo(400);
    }

    @Test
    void shouldEndRoundWhenConditionsMet() {
        var game = gameService.createGame(1);
        gameService.joinGame(game.gameId, "Alice");

        // simulation logique
        // jouer jusqu'à fin manche

        var state = gameService.getState(game.gameId);

        assertThat(state.round).isGreaterThan(1);
    }

    @Test
    void shouldEndGameWhenBestOfThreeReached() {
        var game = gameService.createGame(1);

        // simuler 3 manches gagnées

        var state = gameService.getState(game.gameId);

        assertThat(state.gameOver).isTrue();
        assertThat(state.winner).isNotNull();
    }

    @Test
    void shouldNotifyPlayersOnGameUpdate() {
        gameService.createGame(1);

        Mockito.verify(broker, Mockito.atLeastOnce())
                .convertAndSend(Mockito.anyString(), Optional.ofNullable(Mockito.any()));
    }
*/











}
