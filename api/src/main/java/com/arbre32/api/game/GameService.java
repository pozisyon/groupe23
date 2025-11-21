package com.arbre32.api.game;

import com.arbre32.api.game.dto.GameStateDTO;
import com.arbre32.core.engine.GameEngine;
import com.arbre32.core.engine.GameState;
import com.arbre32.core.model.Card;
import com.arbre32.core.model.Move;
import com.arbre32.core.tree.TreeNode;
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

    // ---------- CRÉER UNE PARTIE ----------
    public GameStateDTO createGame(int mode) {
        GameState st = engine.startNewGame(mode == 32);
        games.put(st.id(), st);
        return GameMapper.toDto(st, engine);
    }

    // ---------- ÉTAT ----------
    public GameStateDTO getState(String id) {
        GameState st = games.get(id);
        return (st == null) ? null : GameMapper.toDto(st, engine);
    }

    // ---------- REJOINDRE ----------
    public GameStateDTO joinGame(String id, String userHandle) {
        GameState st = games.get(id);
        if (st == null) return null;

        boolean added = st.addPlayer(userHandle);

        // assigner le premier joueur au tour
        if (added && st.allPlayers().size() == 1) {
            // premier joueur dans la partie
            // currentPlayerIndex = 0 AUTOMATIQUEMENT
        }

        // si deux joueurs ou plus : on débloque la racine
        if (st.allPlayers().size() >= 2) {
            st.setRootLocked(false);
        }

        GameStateDTO dto = GameMapper.toDto(st, engine);
        broker.convertAndSend("/topic/game/" + id, dto);
        return dto;
    }


    // ---------- RÉSULTAT DE PLAY ----------
    // ---------- RÉSULTAT DE PLAY ----------
    public record PlayResult(
            int status,
            String message,
            GameStateDTO state,
            String playedCard,
            List<String> takenCards
    ) { }


    // ---------- JOUER UN COUP ----------
    public PlayResult play(String id, String cardId, String userHandle) {

        GameState st = games.get(id);
        if (st == null) {
            return new PlayResult(404, "Game not found", null, null, List.of());
        }

        // doit être son tour
        String current = st.currentPlayer();
        if (current == null || !current.equals(userHandle)) {
            return new PlayResult(403, "Not your turn", null, null, List.of());
        }

        Optional<TreeNode<Card>> nodeOpt = st.findById(cardId);
        if (nodeOpt.isEmpty()) {
            return new PlayResult(404, "Card not found", null, null, List.of());
        }

        TreeNode<Card> node = nodeOpt.get();
        boolean childrenCollected = !node.children().isEmpty();

        if (!engine.isPlayable(st, node, childrenCollected)) {
            return new PlayResult(409, "Illegal move", null, null, List.of());
        }

        // 📌 Stocker la carte jouée sous forme lisible
        String played = node.value().toString(); // ex "J♠"

        // Avant le move → détecter les cartes qui seront ramassées
        List<String> taken = new ArrayList<>();
        if (childrenCollected) {
            for (TreeNode<Card> c : node.children()) {
                taken.add(c.value().toString());
            }
        }

        // Appliquer le coup
        engine.applyMove(st, new Move(userHandle, cardId));

        // DTO final
        GameStateDTO dto = GameMapper.toDto(st, engine);
        broker.convertAndSend("/topic/game/" + id, dto);

        return new PlayResult(200, null, dto, played, taken);
    }

    // ---------- PARTIES OUVERTES POUR LE LOBBY ----------
    public List<Map<String, Object>> openGames() {
        List<Map<String, Object>> out = new ArrayList<>();

        for (GameState st : games.values()) {
            Map<String, Object> g = new HashMap<>();
            g.put("id", st.id());

            int p = st.allPlayers().size();
            g.put("players", p);

            if (p < 2)
                g.put("status", "WAITING");
            else if (st.turnIndex() == 0)
                g.put("status", "READY");
            else
                g.put("status", "IN_PROGRESS");

            out.add(g);
        }
        return out;
    }

}
