package com.arbre32.api.game;

import com.arbre32.api.game.dto.GameStateDTO;
import com.arbre32.core.engine.GameEngine;
import com.arbre32.core.engine.GameState;
import com.arbre32.core.model.Card;
import com.arbre32.core.tree.TreeNode;

import java.util.*;

public class GameMapper {

    public static GameStateDTO toDto(GameState st, GameEngine engine) {
        GameStateDTO dto = new GameStateDTO();

        dto.gameId = st.id();
        dto.turnPlayer = st.turnPlayerId();
        dto.turnIndex = st.turnIndex();
        dto.rootLocked = st.rootLocked();
        dto.maxDepth = st.maxDepth();

        // --- SCORE ---
        dto.score = new GameStateDTO.Score();
        dto.score.player1 = st.p1().score();
        dto.score.player2 = st.p2().score();

        // --- BOARD : classé en niveaux (profondeur) ---
        Map<Integer, List<TreeNode<Card>>> grouped = new TreeMap<>();

        for (TreeNode<Card> n : st.tree().nodesBreadth()) {
            int d = n.depth();
            grouped.putIfAbsent(d, new ArrayList<>());
            grouped.get(d).add(n);
        }

        dto.board = new ArrayList<>();

        for (Map.Entry<Integer, List<TreeNode<Card>>> e : grouped.entrySet()) {
            List<GameStateDTO.CardDTO> row = new ArrayList<>();

            for (TreeNode<Card> n : e.getValue()) {
                Card c = n.value();
                GameStateDTO.CardDTO cd = new GameStateDTO.CardDTO();

                cd.id = c.id();
                cd.value = c.rank().label();
                cd.suit = c.suit().symbol();
                cd.power = c.isPower();
                cd.depth = n.depth();

                // locked / playable logic
                cd.locked = computeLocked(n, st);
                cd.playable = engine.isPlayable(st, n, !n.children().isEmpty());

                row.add(cd);
            }

            dto.board.add(row);
        }

        return dto;
    }

    private static boolean computeLocked(TreeNode<Card> node, GameState st) {
        // La racine est verrouillée jusqu'à la fin
        if (node.depth() == 0 && st.rootLocked()) return true;

        // Si la carte est une carte "pouvoir", elle se débloque seulement
        // après que ses enfants aient été ramassés
        if (node.value().isPower() && !node.children().isEmpty()) return true;

        // Feuille → jamais locked
        return false;
    }
}
