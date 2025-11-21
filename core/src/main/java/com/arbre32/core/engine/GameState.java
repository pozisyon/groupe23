package com.arbre32.core.engine;

import com.arbre32.core.model.Card;
import com.arbre32.core.model.Player;
import com.arbre32.core.tree.GameTree;
import com.arbre32.core.tree.TreeNode;

import java.util.*;

public class GameState {

    private final String id;
    private final GameTree<Card> tree;

    private boolean rootLocked = true;
    private int turnIndex = 0;

    // ----- NOUVEAU : joueurs dynamiques -----
    private final Map<String, Player> players = new LinkedHashMap<>();
    private final List<String> turnOrder = new ArrayList<>();
    private int currentPlayerIndex = 0;

    public GameState(String id, GameTree<Card> tree){
        this.id = id;
        this.tree = tree;
    }

    public String id(){ return id; }
    public GameTree<Card> tree(){ return tree; }

    public boolean rootLocked(){ return rootLocked; }
    public void setRootLocked(boolean v){ rootLocked = v; }

    public int turnIndex(){ return turnIndex; }
    public void incrementTurnIndex(){ turnIndex++; }

    // ----- GESTION DES JOUEURS -----

    public boolean addPlayer(String handle){
        if (players.containsKey(handle)) return false;

        players.put(handle, new Player(handle, handle));
        turnOrder.add(handle);
        return true;
    }

    public String currentPlayer(){
        if (turnOrder.isEmpty()) return null;
        return turnOrder.get(currentPlayerIndex);
    }

    public void advanceTurn(){
        if (turnOrder.isEmpty()) return;
        currentPlayerIndex = (currentPlayerIndex + 1) % turnOrder.size();
    }

    public Collection<Player> allPlayers(){
        return players.values();
    }

    public Player getPlayer(String handle){
        return players.get(handle);
    }

    // ---- UTILITAIRE ----

    public Optional<TreeNode<Card>> findById(String cardId){
        return tree.find(n -> n.value().id().equals(cardId));
    }

    public int maxDepth(){
        return tree.maxDepth();
    }
}
