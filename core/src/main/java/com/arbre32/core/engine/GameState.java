package com.arbre32.core.engine;

import com.arbre32.core.model.Card;
import com.arbre32.core.model.Player;
import com.arbre32.core.tree.GameTree;
import com.arbre32.core.tree.TreeNode;

import java.util.Optional;

public class GameState {

    private final String id;
    private final Player p1;
    private final Player p2;
    private final GameTree<Card> tree;

    private int round = 1;
    private String turnPlayerId = null; // Fix : plus "J1" par défaut
    private boolean rootLocked = true;
    private int turnIndex = 0;

    // Joueurs humains
    private String humanP1 = null;
    private String humanP2 = null;

    public GameState(String id, Player p1, Player p2, GameTree<Card> tree) {
        this.id = id;
        this.p1 = p1;
        this.p2 = p2;
        this.tree = tree;
    }

    public String id() { return id; }
    public Player p1() { return p1; }
    public Player p2() { return p2; }
    public GameTree<Card> tree() { return tree; }

    public int round() { return round; }
    public void nextRound() { round++; }

    public String turnPlayerId() { return turnPlayerId; }
    public void setTurnPlayer(String pid) { this.turnPlayerId = pid; }

    public void swapTurn() {
        if (turnPlayerId == null) return;

        if (turnPlayerId.equals(humanP1)) turnPlayerId = humanP2;
        else turnPlayerId = humanP1;
    }

    public boolean rootLocked() { return rootLocked; }
    public void setRootLocked(boolean v) { this.rootLocked = v; }

    public int turnIndex() { return turnIndex; }
    public void incrementTurnIndex() { turnIndex++; }

    public Optional<TreeNode<Card>> findById(String cardId){
        return tree.find(n -> n.value().id().equals(cardId));
    }

    public int maxDepth() { return tree.maxDepth(); }

    // Humains
    public String humanP1() { return humanP1; }
    public String humanP2() { return humanP2; }

    public void setHumanP1(String h) { this.humanP1 = h; }
    public void setHumanP2(String h) { this.humanP2 = h; }
}
