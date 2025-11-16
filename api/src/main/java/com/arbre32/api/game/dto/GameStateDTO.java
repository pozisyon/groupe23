package com.arbre32.api.game.dto;

import java.util.List;

public class GameStateDTO {

    public String gameId;
    public String turnPlayer;
    public int turnIndex;
    public boolean rootLocked;
    public int maxDepth;

    public Score score;
    public List<List<CardDTO>> board;

    public static class Score {
        public int player1;
        public int player2;
    }

    public static class CardDTO {
        public String id;
        public String value;
        public String suit;
        public boolean power;
        public boolean playable;
        public int depth;
        public Boolean locked;
    }
}
