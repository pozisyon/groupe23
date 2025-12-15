package com.arbre32.api.admin;

import com.arbre32.core.engine.GameEngine;
import com.arbre32.core.engine.GameState;
import com.arbre32.core.factory.DeckFactory;
import com.arbre32.core.model.Card;
import com.arbre32.core.tree.TreeNode;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminGameService {

    private final Map<String, GameState> games = new LinkedHashMap<>();
    private final GameEngine engine = new GameEngine();

    // ---------------------------------------------------------
    // CRUD ADMIN
    // ---------------------------------------------------------

    /** Crée une nouvelle partie (sans joueurs). */
    public GameState createGame(boolean mode32) {
        GameState st = engine.startNewGame(mode32);
        games.put(st.id(), st);
        return st;
    }

    /** Liste toutes les parties. */
    public List<GameState> listGames() {
        return new ArrayList<>(games.values());
    }

    /** Liste des parties en cours. */
    public List<GameState> listActiveGames() {
        return games.values()
                .stream()
                .filter(g -> !g.isGameOver())
                .collect(Collectors.toList());
    }

    /** Liste des parties terminées. */
    public List<GameState> listFinishedGames() {
        return games.values()
                .stream()
                .filter(GameState::isGameOver)
                .collect(Collectors.toList());
    }

    /** Récupérer une partie par ID. */
    public GameState getGame(String id) {
        return games.get(id);
    }

    /** Supprimer une partie. */
    public boolean deleteGame(String id) {
        return games.remove(id) != null;
    }

    // ---------------------------------------------------------
    // ACTIONS ADMIN SUR UNE PARTIE
    // ---------------------------------------------------------

    /** Forcer la fin d'une partie immédiatement. */
    public GameState forceFinish(String id) {
        GameState st = games.get(id);
        if (st != null) {
            st.setGameOver(true);
        }
        return st;
    }

    /** Forcer le passage à la manche suivante (BO3). */
    public GameState forceNextRound(String id, boolean mode32) {
        GameState st = games.get(id);
        if (st == null) return null;

        // Incrémente la manche
        st.incrementRound();

        // Si round > 3 → match fini
        if (st.getRound() > 3) {
            st.setGameOver(true);
            return st;
        }

        // Recréer un nouvel arbre pour la nouvelle manche
        List<Card> deck = mode32 ? DeckFactory.build32() : DeckFactory.build52();
        TreeNode<Card> newRoot = buildNewTree(deck);

        st.resetForNewRound(newRoot);
        return st;
    }

    /** Réinitialiser complètement une partie (scores + arbres + manches). */
    public GameState resetMatch(String id, boolean mode32) {
        GameState st = games.get(id);
        if (st == null) return null;

        // remettre à zéro les manches gagnées
        st.getRoundsWon().clear();
        st.setMatchWinnerId(null);

        // nouvelle manche 1
        st.setGameOver(false);
        st.incrementRound(); // on pourrait aussi setter round = 1 avec un setter

        // nouveau deck + arbre
        List<Card> deck = mode32 ? DeckFactory.build32() : DeckFactory.build52();
        TreeNode<Card> newRoot = buildNewTree(deck);

        st.resetForNewRound(newRoot);
        return st;
    }

    // ---------------------------------------------------------
    // INTERNAL UTILITIES
    // ---------------------------------------------------------

    /** Construit un arbre aléatoire (même logique que GameEngine.startNewGame). */
    private TreeNode<Card> buildNewTree(List<Card> deck) {
        Random rnd = new Random();

        TreeNode<Card> root = new TreeNode<>(deck.remove(0));
        Queue<TreeNode<Card>> q = new ArrayDeque<>();
        q.add(root);

        while (!deck.isEmpty()) {
            TreeNode<Card> parent = q.peek();
            int children = 1 + rnd.nextInt(2); // 1 ou 2 enfants

            for (int i = 0; i < children && !deck.isEmpty(); i++) {
                TreeNode<Card> child = new TreeNode<>(deck.remove(0));
                parent.addChild(child);
                q.add(child);
            }
            q.remove();
        }

        return root;
    }
}
