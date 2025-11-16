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
 * Moteur du jeu Arbre32.
 * - Génère l'arbre caché à partir du deck
 * - Applique les règles de profondeur, cartes à pouvoir, racine verrouillée
 * - Gère les déplacements de sous-arbres sans perdre de cartes
 */
public class GameEngine {

    private final RuleEngine rules = new DefaultRuleEngine();
    private final ScoringService scoring = new DefaultScoringService();
    private final Random rnd = new Random();

    /**
     * Crée une nouvelle partie avec un deck de 32 ou 52 cartes.
     */
    public GameState startNewGame(boolean mode32){
        List<Card> deck = mode32 ? DeckFactory.build32() : DeckFactory.build52();

        // On construit un arbre binaire ou ternaire aléatoire
        TreeNode<Card> root = new TreeNode<>(deck.remove(0));
        Queue<TreeNode<Card>> q = new ArrayDeque<>();
        q.add(root);

        while(!deck.isEmpty()){
            TreeNode<Card> parent = q.peek();
            int children = 1 + rnd.nextInt(2); // 1 ou 2 enfants

            for (int i=0; i<children && !deck.isEmpty(); i++){
                TreeNode<Card> child = new TreeNode<>(deck.remove(0));
                parent.addChild(child);
                q.add(child);
            }
            q.remove();
        }

        GameTree<Card> tree = new GameTree<>(root);
        Player p1 = new Player("J1","Joueur 1");
        Player p2 = new Player("J2","Joueur 2");

        return new GameState(
                UUID.randomUUID().toString().substring(0,8),
                p1,
                p2,
                tree
        );
    }

    /**
     * Vérifie si un coup est jouable selon :
     * - la profondeur autorisée pour ce tour
     * - le statut de la carte (pouvoir / feuilles / racine)
     * - le verrouillage de la racine
     */
    public boolean isPlayable(GameState st, TreeNode<Card> node, boolean childrenCollectedThisTurn){
        int maxDepth = st.maxDepth();
        return rules.isDepthAllowed(node, st.turnIndex(), maxDepth)
                && rules.isNodeTakable(node, childrenCollectedThisTurn)
                && !(st.rootLocked() && node == st.tree().root());
    }

    /**
     * Collecte toutes les cartes d'un sous-arbre (nœud + descendants).
     * Utilisé lorsque l'on prend un nœud non-pouvoir.
     */
    public List<Card> collectSubtree(TreeNode<Card> node){
        List<Card> out = new ArrayList<>();
        Deque<TreeNode<Card>> stack = new ArrayDeque<>();
        stack.push(node);

        while(!stack.isEmpty()){
            TreeNode<Card> n = stack.pop();
            out.add(n.value());
            for (TreeNode<Card> c : n.children()){
                stack.push(c);
            }
        }
        return out;
    }

    /**
     * Supprime proprement un sous-arbre du GameState, en garantissant
     * que l'arbre reste cohérent et que aucune carte ne "disparaît".
     *
     * Cas particulier : si on enlève la racine, on choisit un nouveau root
     * parmi ses enfants et on rattache correctement les autres sous-arbres.
     */
    private void removeSubtree(GameState st, TreeNode<Card> node){

        TreeNode<Card> root = st.tree().root();

        // --- CAS 1 : Suppression du root ---
        if (node == root) {

            List<TreeNode<Card>> children = new ArrayList<>(root.children());

            if (children.isEmpty()) {
                // Arbre vide : on laisse une racine sans enfants
                root._childrenMutable().clear();
                return;
            }

            // On choisit le nouveau root
            TreeNode<Card> newRoot = children.get(0);
            int best = subtreeSize(newRoot);

            for (int i = 1; i < children.size(); i++) {
                int sz = subtreeSize(children.get(i));
                if (sz > best) {
                    best = sz;
                    newRoot = children.get(i);
                }
            }

            // 1️⃣ On détache chaque enfant proprement
            for (TreeNode<Card> c : children) {
                c.detachFromParent();   // maintenant c.parent = null
            }

            // 2️⃣ On rattache tous les autres sous-arbres sous newRoot
            for (TreeNode<Card> c : children) {
                if (c != newRoot) {
                    newRoot.addChild(c);
                }
            }

            // 3️⃣ newRoot devient la racine
            st.tree().setRoot(newRoot);
            return;
        }

        // --- CAS 2 : Suppression d’un nœud interne ou feuille ---
        node.detachFromParent();
    }


    /**
     * Taille d'un sous-arbre (nombre de nœuds).
     */
    private int subtreeSize(TreeNode<Card> node){
        int count = 0;
        Deque<TreeNode<Card>> st = new ArrayDeque<>();
        st.push(node);
        while (!st.isEmpty()){
            TreeNode<Card> n = st.pop();
            count++;
            for (TreeNode<Card> c : n.children()){
                st.push(c);
            }
        }
        return count;
    }

    /**
     * Applique un coup :
     * - Vérifie la jouabilité (carte pouvoir, profondeur, racine…)
     * - Met à jour l'arbre sans perdre de cartes
     * - Calcule les points
     * - Bascule le tour
     */
    public void applyMove(GameState st, Move move) {
        TreeNode<Card> node = st.findById(move.cardId()).orElse(null);
        if (node == null){
            // Carte introuvable : on passe juste le tour
            st.swapTurn();
            return;
        }

        boolean childrenCollectedThisTurn = !node.children().isEmpty();

        // Vérification principale des règles
        if (!isPlayable(st, node, childrenCollectedThisTurn)) {
            st.swapTurn();
            return;
        }

        List<Card> gained;

        // Cas spécial : carte à pouvoir
        if (node.value().isPower()) {
            if (childrenCollectedThisTurn){
                // 1er passage : on ramasse d'abord les enfants, la carte sera jouable au tour suivant
                List<Card> childrenCards = new ArrayList<>();
                for (TreeNode<Card> child : new ArrayList<>(node.children())){
                    // On ramasse les sous-arbres des enfants
                    childrenCards.addAll(collectSubtree(child));
                    removeSubtree(st, child);
                }

                gained = childrenCards;

            } else {
                // 2e passage : la carte à pouvoir n'a plus d'enfants, on la prend seule
                gained = List.of(node.value());
                removeSubtree(st, node);
            }
        } else {
            // Carte normale : on prend tout le sous-arbre
            gained = collectSubtree(node);
            removeSubtree(st, node);
        }

        // Calcul des points
        int pts = scoring.scoreForCards(gained);
        if (node == st.tree().root()){
            pts += scoring.rootBonus();
        }

        if (move.playerId().equals(st.p1().id())){
            st.p1().addCards(gained);
            st.p1().addScore(pts);
        } else {
            st.p2().addCards(gained);
            st.p2().addScore(pts);
        }

        // Changement de joueur et incrément du tour
        st.swapTurn();
        st.incrementTurnIndex();
    }
}
