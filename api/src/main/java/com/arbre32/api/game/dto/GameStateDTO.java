// src/main/java/com/arbre32/api/game/dto/GameStateDTO.java
package com.arbre32.api.game.dto;

import java.util.List;

public class GameStateDTO {

    public String gameId;
    public boolean rootLocked;
    public int turnIndex;
    public int maxDepth;

    // 🔥 pour savoir à qui est le tour
    public String currentPlayer;

    // 🔥 liste des joueurs dynamiques
    public List<PlayerDTO> players;

    // plateau
    public List<List<CardDTO>> board;

    public static class PlayerDTO {
        public String id;    // == handle
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
