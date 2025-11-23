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
import java.util.List;

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
        // 1) PLAYER MAPPING
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
        // 2) BOARD MAPPING (par profondeur)
        // -------------------------------
        GameTree<Card> tree = st.tree();

        int depthMax = tree.maxDepth();
        List<List<CardDTO>> levels = new ArrayList<>();
        for (int d = 0; d <= depthMax; d++) {
            levels.add(new ArrayList<>());
        }

        for (TreeNode<Card> node : tree.nodesBreadth()) {
            Card c = node.value();

            // ✅ Ne jamais afficher les cartes déjà ramassées
            if (c.isRemoved()) {
                continue;
            }

            CardDTO cd = new CardDTO();
            cd.id = c.id();
            cd.value = c.rank().name();      // ou c.rank().symbol() selon ta classe
            cd.suit = c.suit().symbol();
            cd.power = c.isPower();
            cd.depth = node.depth();

            boolean childrenCollectedThisTurn = !node.children().isEmpty();
            boolean playable = engine.isPlayable(st, node, childrenCollectedThisTurn);
            cd.playable = playable;

            // locked = inverse de playable (et racine verrouillée)
            cd.locked = computeLocked(node, st, playable);

            levels.get(cd.depth).add(cd);
        }

        dto.board = levels;

        // -------------------------------
        // 3) GAME OVER ?
        // -------------------------------
        boolean playableExists = engine.hasAnyPlayableMove(st);
        dto.gameOver = !playableExists;

        // -------------------------------
        // 4) WINNER ?
        // -------------------------------
        dto.winner = null;

        if (dto.gameOver && !st.allPlayers().isEmpty()) {
            Player winner = st.allPlayers()
                    .stream()
                    .max(Comparator.comparingInt(Player::score))
                    .orElse(null);

            if (winner != null) {
                dto.winner = winner.name();
            }
        }

        return dto;
    }

    /**
     * locked = true si :
     *  - c'est la racine et rootLocked = true
     *  - OU la carte n'est pas jouable
     */
    private static boolean computeLocked(TreeNode<Card> node, GameState st, boolean playable) {
        if (node.depth() == 0 && st.rootLocked()) {
            return true;
        }
        return !playable;
    }
}
