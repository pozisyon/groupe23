package com.arbre32.api.game;

import com.arbre32.api.game.dto.GameStateDTO;
import com.arbre32.api.game.dto.GameStateDTO.CardDTO;
import com.arbre32.api.game.dto.GameStateDTO.PlayerDTO;
import com.arbre32.core.engine.GameEngine;
import com.arbre32.core.engine.GameState;
import com.arbre32.core.model.Card;
import com.arbre32.core.model.Player;
import com.arbre32.core.tree.GameTree;
import com.arbre32.core.tree.TreeNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMapper {

    /**
     * Convertit l'état interne du jeu (GameState) en GameStateDTO pour le frontend.
     */
    public static GameStateDTO toDto(GameState st, GameEngine engine) {

        GameStateDTO dto = new GameStateDTO();
        dto.gameId = st.id();
        dto.rootLocked = st.rootLocked();
        dto.turnIndex = st.turnIndex();
        dto.maxDepth = st.maxDepth();
        dto.currentPlayer = st.currentPlayer();

        // -------------------------------
        // 1) PLAYERS
        // -------------------------------
        List<PlayerDTO> plist = new ArrayList<>();
        for (Player p : st.allPlayers()) {
            PlayerDTO pd = new PlayerDTO();
            pd.id = p.id();
            pd.name = p.name();
            pd.score = p.score();
            plist.add(pd);
        }
        dto.players = plist;

        // -------------------------------
        // 2) BOARD (par profondeur)
        // -------------------------------
        GameTree<Card> tree = st.tree();

        int depthMax = tree.maxDepth();
        List<List<CardDTO>> levels = new ArrayList<>();
        for (int d = 0; d <= depthMax; d++) {
            levels.add(new ArrayList<>());
        }

        for (TreeNode<Card> node : tree.nodesBreadth()) {
            Card c = node.value();

            // Ne jamais afficher les cartes déjà ramassées
            if (c.isRemoved()) {
                continue;
            }

            CardDTO cd = new CardDTO();
            cd.id = c.id();
            cd.value = c.rank().name();
            cd.suit = c.suit().symbol();
            cd.power = c.isPower();
            cd.depth = node.depth();

            boolean childrenCollectedThisTurn = !node.children().isEmpty();
            boolean playable = engine.isPlayable(st, node, childrenCollectedThisTurn);

            cd.playable = playable;
            cd.locked = !playable || (node.depth() == 0 && st.rootLocked());

            levels.get(cd.depth).add(cd);
        }

        dto.board = levels;

        // -------------------------------
        // 3) Best-of-3
        // -------------------------------
        dto.round = st.getRound();

        Map<String, Integer> rw = new HashMap<>();
        for (Map.Entry<String, Integer> e : st.getRoundsWon().entrySet()) {
            rw.put(e.getKey(), e.getValue());
        }
        dto.roundsWon = rw;

        // -------------------------------
        // 4) MATCH OVER ?
        // -------------------------------
        dto.gameOver = st.isGameOver();
        dto.winner = null;

        if (dto.gameOver && st.getMatchWinnerId() != null) {
            String winnerId = st.getMatchWinnerId();

            Player winner = st.allPlayers()
                    .stream()
                    .filter(p -> p.id().equals(winnerId))
                    .findFirst()
                    .orElse(null);

            if (winner != null) {
                dto.winner = winner.name();
            } else {
                // fallback : max au score
                winner = st.allPlayers()
                        .stream()
                        .max(Comparator.comparingInt(Player::score))
                        .orElse(null);
                if (winner != null) {
                    dto.winner = winner.name();
                }
            }
        }

        return dto;
    }
}
