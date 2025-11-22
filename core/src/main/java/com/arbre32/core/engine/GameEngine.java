package com.arbre32.core.engine;

import com.arbre32.core.factory.DeckFactory;
import com.arbre32.core.model.Card;
import com.arbre32.core.model.Move;
import com.arbre32.core.model.Player;
import com.arbre32.core.rules.DefaultRuleEngine;
import com.arbre32.core.rules.RuleEngine;
import com.arbre32.core.scoring.DefaultScoringService;
import com.arbre32.core.scoring.ScoringService;
import com.arbre32.core.tree.GameTree;
import com.arbre32.core.tree.TreeNode;

import java.util.*;

/**
 * Moteur du jeu Arbre32 (version joueurs dynamiques).
 */
public class GameEngine {

    private final RuleEngine rules = new DefaultRuleEngine();
    private final ScoringService scoring = new DefaultScoringService();
    private final Random rnd = new Random();

    /**
     * Crée une nouvelle partie avec un deck de 32 ou 52 cartes.
     * ⚠️ Aucun joueur n’est ajouté ici : ils arrivent via join().
     */
    public GameState startNewGame(boolean mode32) {
        List<Card> deck = mode32 ? DeckFactory.build32() : DeckFactory.build52();

        // Construction de l’arbre aléatoire
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

        GameTree<Card> tree = new GameTree<>(root);
        return new GameState(
                UUID.randomUUID().toString().substring(0, 8),
                tree
        );
    }

    /**
     * Vérifie si un coup est jouable selon :
     * - la profondeur autorisée pour ce tour
     * - le statut de la carte (pouvoir / feuilles / racine)
     * - le verrouillage de la racine
     */
    public boolean isPlayable(GameState st, TreeNode<Card> node, boolean childrenCollectedThisTurn) {
        int maxDepth = st.maxDepth();
        return rules.isDepthAllowed(node, st.turnIndex(), maxDepth)
                && rules.isNodeTakable(node, childrenCollectedThisTurn)
                && !(st.rootLocked() && node == st.tree().root());
    }

    /**
     * Collecte toutes les cartes d'un sous-arbre (nœud + descendants).
     */
    public List<Card> collectSubtree(TreeNode<Card> node) {
        List<Card> out = new ArrayList<>();
        Deque<TreeNode<Card>> stack = new ArrayDeque<>();
        stack.push(node);

        while (!stack.isEmpty()) {
            TreeNode<Card> n = stack.pop();
            out.add(n.value());
            for (TreeNode<Card> c : n.children()) {
                stack.push(c);
            }
        }
        return out;
    }

    /**
     * Supprime proprement un sous-arbre du GameState.
     */
    private void removeSubtree(GameState st, TreeNode<Card> node) {
        TreeNode<Card> root = st.tree().root();

        // --- CAS 1 : Suppression du root ---
        if (node == root) {
            List<TreeNode<Card>> children = new ArrayList<>(root.children());

            if (children.isEmpty()) {
                // Arbre vide : root sans enfants
                root._childrenMutable().clear();
                return;
            }

            // Choisir le nouveau root : le sous-arbre le plus gros
            TreeNode<Card> newRoot = children.get(0);
            int best = subtreeSize(newRoot);

            for (int i = 1; i < children.size(); i++) {
                int sz = subtreeSize(children.get(i));
                if (sz > best) {
                    best = sz;
                    newRoot = children.get(i);
                }
            }

            // Détacher tous les enfants du root actuel
            for (TreeNode<Card> c : children) {
                c.detachFromParent();
            }

            // Rattacher les autres sous-arbres sous newRoot
            for (TreeNode<Card> c : children) {
                if (c != newRoot) {
                    newRoot.addChild(c);
                }
            }

            // Définir la nouvelle racine
            st.tree().setRoot(newRoot);
            return;
        }

        // --- CAS 2 : nœud interne ou feuille ---
        node.detachFromParent();
    }

    private int subtreeSize(TreeNode<Card> node) {
        int count = 0;
        Deque<TreeNode<Card>> st = new ArrayDeque<>();
        st.push(node);
        while (!st.isEmpty()) {
            TreeNode<Card> n = st.pop();
            count++;
            for (TreeNode<Card> c : n.children()) {
                st.push(c);
            }
        }
        return count;
    }

    /**
     * Applique un coup pour un joueur dynamique.
     */
    /**
     * Applique un coup pour un joueur dynamique et retourne le résultat du coup.
     */
    public MoveResult applyMove(GameState st, Move move) {

        Player player = st.getPlayer(move.playerId());
        if (player == null) {
            return new MoveResult(null, List.of(), 0);
        }

        TreeNode<Card> node = st.findById(move.cardId()).orElse(null);
        if (node == null) {
            st.advanceTurn();
            st.incrementTurnIndex();
            return new MoveResult(null, List.of(), 0);
        }

        boolean childrenCollectedThisTurn = !node.children().isEmpty();

        if (!isPlayable(st, node, childrenCollectedThisTurn)) {
            st.advanceTurn();
            st.incrementTurnIndex();
            return new MoveResult(null, List.of(), 0);
        }

        // ----------- COLLECTER LES CARTES -------------
        List<Card> gained;

        if (node.value().isPower()) {

            if (childrenCollectedThisTurn) {
                // Premier passage : enfants uniquement
                List<Card> childrenCards = new ArrayList<>();
                for (TreeNode<Card> child : new ArrayList<>(node.children())) {
                    childrenCards.addAll(collectSubtree(child));
                    removeSubtree(st, child);
                }
                gained = childrenCards;

            } else {
                // Deuxième passage : carte elle-même
                gained = List.of(node.value());
                removeSubtree(st, node);
            }

        } else {
            // Carte normale → sous-arbre complet
            gained = collectSubtree(node);
            removeSubtree(st, node);
        }

        // ----------- POINTS -------------
        int pts = scoring.scoreForCards(gained);
        if (node == st.tree().root()) {
            pts += scoring.rootBonus();
        }

        // ----------- ATTRIBUTION -------------
        player.addCards(gained);
        player.addScore(pts);

        // ----------- CHANGEMENT DE JOUEUR -------------
        st.advanceTurn();
        st.incrementTurnIndex();

        // ----------- Retour du résultat -------------
        return new MoveResult(
                node.value(),   // carte jouée
                gained,         // toutes les cartes ramassées
                pts             // points gagnés
        );
    }

    public static class MoveResult {
        public final Card playedCard;
        public final List<Card> takenCards;
        public final int pointsGained;

        public MoveResult(Card played, List<Card> taken, int points) {
            this.playedCard = played;
            this.takenCards = taken;
            this.pointsGained = points;
        }

        public Card getPlayedCard() {
            return playedCard;
        }

        public List<Card> getTakenCards() {
            return takenCards;
        }

        public int getPointsGained() {
            return pointsGained;
        }
    }

}