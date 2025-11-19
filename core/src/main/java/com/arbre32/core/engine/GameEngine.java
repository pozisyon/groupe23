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

public class GameEngine {

    private final RuleEngine rules = new DefaultRuleEngine();
    private final ScoringService scoring = new DefaultScoringService();
    private final Random rnd = new Random();

    public GameState startNewGame(boolean mode32){
        List<Card> deck = mode32 ? DeckFactory.build32() : DeckFactory.build52();

        TreeNode<Card> root = new TreeNode<>(deck.remove(0));
        Queue<TreeNode<Card>> q = new ArrayDeque<>();
        q.add(root);

        while(!deck.isEmpty()){
            TreeNode<Card> parent = q.peek();
            int children = 1 + rnd.nextInt(2);

            for (int i=0; i<children && !deck.isEmpty(); i++){
                TreeNode<Card> child = new TreeNode<>(deck.remove(0));
                parent.addChild(child);
                q.add(child);
            }
            q.remove();
        }

        GameTree<Card> tree = new GameTree<>(root);

        Player p1 = new Player("P1","Joueur1");
        Player p2 = new Player("P2","Joueur2");

        return new GameState(
                UUID.randomUUID().toString().substring(0,8),
                p1,
                p2,
                tree
        );
    }

    public boolean isPlayable(GameState st, TreeNode<Card> node, boolean childrenCollected){
        int maxDepth = st.maxDepth();
        return rules.isDepthAllowed(node, st.turnIndex(), maxDepth)
                && rules.isNodeTakable(node, childrenCollected)
                && !(st.rootLocked() && node == st.tree().root());
    }

    private List<Card> collectSubtree(TreeNode<Card> node){
        List<Card> out = new ArrayList<>();
        Deque<TreeNode<Card>> stack = new ArrayDeque<>();
        stack.push(node);

        while(!stack.isEmpty()){
            TreeNode<Card> n = stack.pop();
            out.add(n.value());
            for (TreeNode<Card> c : n.children())
                stack.push(c);
        }
        return out;
    }

    private void removeSubtree(GameState st, TreeNode<Card> node){
        TreeNode<Card> root = st.tree().root();

        if (node == root){

            List<TreeNode<Card>> children = new ArrayList<>(root.children());

            if (children.isEmpty()) {
                root._childrenMutable().clear();
                return;
            }

            TreeNode<Card> newRoot = children.get(0);
            int best = subtreeSize(newRoot);

            for (int i = 1; i < children.size(); i++) {
                int sz = subtreeSize(children.get(i));
                if (sz > best) {
                    best = sz;
                    newRoot = children.get(i);
                }
            }

            for (TreeNode<Card> c : children)
                c.detachFromParent();

            for (TreeNode<Card> c : children)
                if (c != newRoot) newRoot.addChild(c);

            st.tree().setRoot(newRoot);
            return;
        }

        node.detachFromParent();
    }

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

    // APPLY MOVE (CORRIGÉ)
    public void applyMove(GameState st, Move move) {
        TreeNode<Card> node = st.findById(move.cardId()).orElse(null);
        if (node == null){
            st.swapTurn();
            return;
        }

        boolean childrenCollected = !node.children().isEmpty();

        if (!isPlayable(st, node, childrenCollected)) {
            st.swapTurn();
            return;
        }

        List<Card> gained;

        if (node.value().isPower()) {

            if (childrenCollected){
                List<Card> childrenCards = new ArrayList<>();
                for (TreeNode<Card> child : new ArrayList<>(node.children())){
                    childrenCards.addAll(collectSubtree(child));
                    removeSubtree(st, child);
                }
                gained = childrenCards;

            } else {
                gained = List.of(node.value());
                removeSubtree(st, node);
            }

        } else {
            gained = collectSubtree(node);
            removeSubtree(st, node);
        }

        int pts = scoring.scoreForCards(gained);
        if (node == st.tree().root())
            pts += scoring.rootBonus();

        if (move.playerId().equals(st.humanP1())) {
            st.p1().addCards(gained);
            st.p1().addScore(pts);
        } else {
            st.p2().addCards(gained);
            st.p2().addScore(pts);
        }

        st.swapTurn();
        st.incrementTurnIndex();
    }
}
