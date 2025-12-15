package com.arbre32.core.engine;

import com.arbre32.core.model.Card;
import com.arbre32.core.model.Player;
import com.arbre32.core.tree.GameTree;
import com.arbre32.core.tree.TreeNode;

import java.time.Instant;
import java.util.*;

/**
 * Représente l'état d'une partie Arbre32.
 * Gère :
 *  - l'arbre de cartes
 *  - les joueurs dynamiques
 *  - le verrouillage de la racine
 *  - l'index de tour
 *  - le best-of-3 (manches)
 */
public class GameState {

    private final String id;
    private final GameTree<Card> tree;

    // NEW: horodatage de création de la partie (pour l'admin dashboard)
    private final Instant createdAt = Instant.now();

    private boolean rootLocked = true;
    private int turnIndex = 0;

    // ----- JOUEURS DYNAMIQUES -----
    private final Map<String, Player> players = new LinkedHashMap<>();
    private final List<String> turnOrder = new ArrayList<>();
    private int currentPlayerIndex = 0;

    // Cartes retirées (par id)
    private final Set<String> removedCards = new HashSet<>();

    // Fin de match (pas de manche)
    private boolean gameOver = false;

    // ----- Best-of-3 -----
    private int round = 1; // manche en cours (1, 2 ou 3)
    private final Map<String, Integer> roundsWon = new HashMap<>();
    private String matchWinnerId;

    public GameState(String id, GameTree<Card> tree) {
        this.id = id;
        this.tree = tree;
    }

    // ----- IDENTITÉ & ARBRE -----

    public String id() {
        return id;
    }

    public GameTree<Card> tree() {
        return tree;
    }

    public int maxDepth() {
        return tree.maxDepth();
    }

    // ----- ADMIN SUPPORT -----

    public Instant getCreatedAt() {
        return createdAt;
    }

    public int getCurrentRound() {
        return round;
    }

    public int getTotalRounds() {
        return 3; // Best-of-3 → toujours 3 manches max
    }

    // ----- RACINE -----

    public boolean rootLocked() {
        return rootLocked;
    }

    public void setRootLocked(boolean v) {
        this.rootLocked = v;
    }

    // ----- TOURS -----

    public int turnIndex() {
        return turnIndex;
    }

    public void incrementTurnIndex() {
        this.turnIndex++;
    }

    public String currentPlayer() {
        if (turnOrder.isEmpty()) return null;
        return turnOrder.get(currentPlayerIndex);
    }

    public void advanceTurn() {
        if (turnOrder.isEmpty()) return;
        currentPlayerIndex = (currentPlayerIndex + 1) % turnOrder.size();
    }

    // ----- GESTION DES JOUEURS -----

    public boolean addPlayer(String handle) {
        if (players.containsKey(handle)) return false;
        players.put(handle, new Player(handle, handle));
        turnOrder.add(handle);
        return true;
    }

    public Collection<Player> allPlayers() {
        return players.values();
    }

    public Player getPlayer(String handle) {
        return players.get(handle);
    }

    // ----- RECHERCHE DE CARTE -----

    public Optional<TreeNode<Card>> findById(String cardId) {
        return tree.find(n ->
                n.value().id().equals(cardId)
                        && !n.value().isRemoved()
        );
    }

    // ----- CARTES RETIRÉES -----

    public void markCardsRemoved(Collection<Card> cards) {
        for (Card c : cards) {
            removedCards.add(c.id());
        }
    }

    public boolean isCardRemoved(String cardId) {
        return removedCards.contains(cardId);
    }

    // ----- FIN DE MATCH -----

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean v) {
        this.gameOver = v;
    }

    public String getMatchWinnerId() {
        return matchWinnerId;
    }

    public void setMatchWinnerId(String matchWinnerId) {
        this.matchWinnerId = matchWinnerId;
    }

    // ----- BEST OF 3 -----

    public int getRound() {
        return round;
    }

    public void incrementRound() {
        this.round++;
    }

    public Map<String, Integer> getRoundsWon() {
        return Collections.unmodifiableMap(roundsWon);
    }

    public int getRoundsWonFor(String playerId) {
        return roundsWon.getOrDefault(playerId, 0);
    }

    public void addRoundWin(String playerId) {
        roundsWon.put(playerId, getRoundsWonFor(playerId) + 1);
    }

    public void resetForNewRound(TreeNode<Card> newRoot) {
        this.tree.setRoot(newRoot);

        this.rootLocked = true;
        this.turnIndex = 0;
        this.gameOver = false;
        this.removedCards.clear();

        this.currentPlayerIndex = 0;

        for (Player p : players.values()) {
            p.resetForNewRound();
        }
    }
}
