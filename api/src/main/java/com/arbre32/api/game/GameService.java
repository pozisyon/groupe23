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

        // Premier joueur : index 0 déjà
        if (added && st.allPlayers().size() == 1) {
            // rien de spécial à faire, currentPlayerIndex = 0 par défaut
        }

        // Deux joueurs ou plus → on débloque la racine
        if (st.allPlayers().size() >= 2) {
            st.setRootLocked(false);
        }

        GameStateDTO dto = GameMapper.toDto(st, engine);
        broker.convertAndSend("/topic/game/" + id, dto);
        return dto;
    }

    // ---------- RÉSULTAT DE PLAY ----------
    public record PlayResult(int status, String message, GameStateDTO state) { }

    // ---------- JOUER UN COUP ----------
    public PlayResult play(String id, String cardId, String userHandle) {

        GameState st = games.get(id);
        if (st == null) {
            return new PlayResult(404, "Game not found", null);
        }

        // doit être son tour
        String current = st.currentPlayer();
        if (current == null || !current.equals(userHandle)) {
            return new PlayResult(403, "Not your turn", null);
        }

        Optional<TreeNode<Card>> nodeOpt = st.findById(cardId);
        if (nodeOpt.isEmpty()) {
            return new PlayResult(404, "Card not found", null);
        }

        TreeNode<Card> node = nodeOpt.get();
        boolean childrenCollected = !node.children().isEmpty();

        if (!engine.isPlayable(st, node, childrenCollected)) {
            return new PlayResult(409, "Illegal move", null);
        }

        // 👉 Appliquer le coup AVEC retour détaillé
        GameEngine.MoveResult result = engine.applyMove(st, new Move(userHandle, cardId));

        // DTO de la nouvelle game state
        GameStateDTO dto = GameMapper.toDto(st, engine);
        broker.convertAndSend("/topic/game/" + id, dto);

        // 👉 Construire le message de chat SI un vrai coup a été joué
        if (result.getPlayedCard() != null) {
            String playedLabel = result.getPlayedCard().toString(); // ex: "Q♠"

            List<String> takenLabels = result.getTakenCards()
                    .stream()
                    .map(Card::toString)  // ex: "8♦", "10♦", ...
                    .toList();

            String takenPart;
            if (takenLabels.isEmpty()) {
                takenPart = "n'a ramassé aucune carte.";
            } else {
                takenPart = "a ramassé " + takenLabels.size()
                        + " carte(s) : " + String.join(", ", takenLabels) + ".";
            }

            String pointsPart = " Points gagnés sur ce coup : "
                    + result.getPointsGained() + ".";

            String content = userHandle + " a joué " + playedLabel + " et " + takenPart + pointsPart;

            // Envoi sur /topic/chat/{gameId}
            broker.convertAndSend(
                    "/topic/chat/" + id,
                    Map.of(
                            "sender", "SYSTEM",
                            "content", content
                    )
            );
        }

        return new PlayResult(200, null, dto);
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
