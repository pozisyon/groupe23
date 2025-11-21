// src/main/java/com/arbre32/api/game/GameMapper.java
package com.arbre32.api.game;

import com.arbre32.api.game.dto.GameStateDTO;
import com.arbre32.core.engine.GameEngine;
import com.arbre32.core.engine.GameState;
import com.arbre32.core.model.Card;
import com.arbre32.core.model.Player;
import com.arbre32.core.tree.TreeNode;

import java.util.*;

public class GameMapper {

    public static GameStateDTO toDto(GameState st, GameEngine engine) {
        GameStateDTO dto = new GameStateDTO();

        dto.gameId      = st.id();
        dto.rootLocked  = st.rootLocked();
        dto.turnIndex   = st.turnIndex();
        dto.maxDepth    = st.maxDepth();
        dto.currentPlayer = st.currentPlayer();

        // --- joueurs dynamiques ---
        List<GameStateDTO.PlayerDTO> players = new ArrayList<>();
        for (Player p : st.allPlayers()) {
            GameStateDTO.PlayerDTO pd = new GameStateDTO.PlayerDTO();
            pd.id    = p.id();   // == handle
            pd.name  = p.name();
            pd.score = p.score();
            players.add(pd);
        }
        dto.players = players;

        // --- plateau groupé par profondeur ---
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
                boolean playable = engine.isPlayable(st, n, childrenCollected);

                cd.playable = playable;
                cd.locked   = computeLocked(n, st, playable);

                row.add(cd);
            }

            dto.board.add(row);
        }

        return dto;
    }

    private static boolean computeLocked(TreeNode<Card> node, GameState st, boolean playable) {
        if (node.depth() == 0 && st.rootLocked()) {
            return true;
        }
        return !playable;
    }
}
