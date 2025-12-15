package com.arbre32.api.game.dto;

import java.util.List;
import java.util.Map;

public class GameStateDTO {

    public String gameId;
    public boolean rootLocked;
    public int turnIndex;
    public int maxDepth;
    public String currentPlayer;

    public List<PlayerDTO> players;
    public List<List<CardDTO>> board;

    public boolean gameOver;
    public String winner; // gagnant du match (si gameOver == true)

    // ----- Best-of-3 -----
    public int round;                      // manche actuelle (1,2,3)
    public Map<String, Integer> roundsWon; // id joueur -> manches gagnées

    public static class PlayerDTO {
        public String id;
        public String name;
        public int score;
    }

    public static class CardDTO {
        public String id;
        public String value;
        public String suit;
        public boolean power;
        public int depth;
        public boolean playable;
        public boolean locked;
    }
}
