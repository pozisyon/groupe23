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

        dto.gameId     = st.id();
        dto.turnPlayer = st.turnPlayerId();
        dto.turnIndex  = st.turnIndex();
        dto.rootLocked = st.rootLocked();
        dto.maxDepth   = st.maxDepth();

        // 🔥 mapping des joueurs humains
        dto.humanP1 = st.humanP1();
        dto.humanP2 = st.humanP2();


        // --- SCORE ---
        dto.score = new GameStateDTO.Score();
        dto.score.player1 = st.p1().score();
        dto.score.player2 = st.p2().score();

        // --- BOARD groupé par profondeur ---
        Map<Integer, List<TreeNode<Card>>> grouped = new TreeMap<>();

        for (TreeNode<Card> n : st.tree().nodesBreadth()) {
            int d = n.depth();
            grouped.computeIfAbsent(d, k -> new ArrayList<>()).add(n);
        }

        dto.board = new ArrayList<>();

        for (Map.Entry<Integer, List<TreeNode<Card>>> e : grouped.entrySet()) {
            List<GameStateDTO.CardDTO> row = new ArrayList<>();

            for (TreeNode<Card> n : e.getValue()) {
                Card c = n.value();
                GameStateDTO.CardDTO cd = new GameStateDTO.CardDTO();

                cd.id    = c.id();
                cd.value = c.rank().label();
                cd.suit  = c.suit().symbol();
                cd.power = c.isPower();
                cd.depth = n.depth();

                boolean childrenCollected = !n.children().isEmpty();

                // 🔥 source unique de vérité
                boolean playable = engine.isPlayable(st, n, childrenCollected);

                cd.playable = playable;
                cd.locked   = computeLocked(n, st, playable);

                row.add(cd);
                System.out.println("CARD " + cd.id + " playable=" + cd.playable + " locked=" + cd.locked);

            }


            dto.board.add(row);
        }

        return dto;
    }

    private static boolean computeLocked(TreeNode<Card> node, GameState st, boolean playable) {
        // racine verrouillée tant que rootLocked = true
        if (node.depth() == 0 && st.rootLocked()) {
            return true;
        }
        // le reste : locked = !playable
        return !playable;
    }
}
