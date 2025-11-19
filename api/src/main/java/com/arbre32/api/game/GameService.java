package com.arbre32.api.game;

import com.arbre32.api.game.dto.GameStateDTO;
import com.arbre32.core.engine.GameEngine;
import com.arbre32.core.engine.GameState;
import com.arbre32.core.model.Move;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final GameEngine engine = new GameEngine();
    private final Map<String, GameState> games = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate broker;

    public GameService(SimpMessagingTemplate broker) {
        this.broker = broker;
    }

    // CREATE A GAME
    public String create(int mode) {
        GameState st = engine.startNewGame(mode == 32);
        games.put(st.id(), st);
        return st.id();
    }

    // GET STATE
    public GameStateDTO state(String id) {
        GameState st = games.get(id);
        return (st == null) ? null : GameMapper.toDto(st, engine);
    }

    // JOIN GAME (CORRIGÉ)
    public GameStateDTO join(String id, String playerHandle) {
        System.out.println("\n\n--- METHODE JOIN ---\n");

        GameState st = games.get(id);
        if (st == null) return null;

        if (st.humanP1() == null) {
            st.setHumanP1("J1");
            st.setTurnPlayer("J1");
        } else if (st.humanP2() == null) {
            st.setHumanP2("J2");
            st.setRootLocked(false);
        }

        GameStateDTO dto = GameMapper.toDto(st, engine);
        broker.convertAndSend("/topic/game/" + id, dto);

        return dto;
    }

    // PLAY (CORRIGÉ)
    public record PlayResult(int status, String message, GameStateDTO state) {}

    public PlayResult play(String id, String cardId, String playerId) {

        GameState st = games.get(id);
        if (st == null)
            return new PlayResult(404, "Game not found", null);

        // Vérification : c'est bien à ce joueur de jouer

        if (!st.turnPlayerId().equals(playerId)) {
            return new PlayResult(403, "Not your turn", null);
        }

        var node = st.findById(cardId).orElse(null);
        if (node == null)
            return new PlayResult(404, "Card not found", null);

        boolean childrenCollected = !node.children().isEmpty();
        if (!engine.isPlayable(st, node, childrenCollected))
            return new PlayResult(409, "Illegal move", null);

        // Appliquer le coup
        engine.applyMove(st, new Move(playerId, cardId));

        GameStateDTO dto = GameMapper.toDto(st, engine);
        broker.convertAndSend("/topic/game/" + id, dto);

        return new PlayResult(200, null, dto);
    }

    public List<Map<String,Object>> openGames() {
        List<Map<String,Object>> out = new ArrayList<>();
        for (String id : games.keySet()) {
            out.add(Map.of("id", id));
        }
        return out;
    }
}
