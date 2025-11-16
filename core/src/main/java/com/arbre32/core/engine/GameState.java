package com.arbre32.core.engine;

import com.arbre32.core.model.Card;
import com.arbre32.core.model.Player;
import com.arbre32.core.tree.GameTree;
import com.arbre32.core.tree.TreeNode;

import java.util.*;

public class GameState {
    private final String id;
    private final Player p1;
    private final Player p2;
    private final GameTree<Card> tree;
    private int round = 1;
    private String turnPlayerId;
    private boolean rootLocked = true;
    private int turnIndex = 0;

    public GameState(String id, Player p1, Player p2, GameTree<Card> tree) {
        this.id = id; this.p1 = p1; this.p2 = p2; this.tree = tree; this.turnPlayerId = p1.id();
    }

    public String id(){ return id; }
    public Player p1(){ return p1; }
    public Player p2(){ return p2; }
    public GameTree<Card> tree(){ return tree; }
    public int round(){ return round; }
    public void nextRound(){ round++; }
    public String turnPlayerId(){ return turnPlayerId; }
    public void swapTurn(){ turnPlayerId = turnPlayerId.equals(p1.id()) ? p2.id() : p1.id(); }
    public boolean rootLocked(){ return rootLocked; }
    public void setRootLocked(boolean v){ rootLocked = v; }
    public int turnIndex(){ return turnIndex; }
    public void incrementTurnIndex(){ turnIndex++; }

    public int maxDepth(){ return tree.maxDepth(); }

    public Optional<TreeNode<Card>> findById(String cardId){
        return tree.find(n -> n.value().id().equals(cardId));
    }
}
