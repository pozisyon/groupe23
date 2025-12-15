package com.arbre32.api.admin;

import com.arbre32.api.game.dto.GameStateDTO;
import com.arbre32.api.game.dto.GameStateDTO;

import java.util.List;
import java.util.Map;

public class AdminDTOs {
    public class AdminPlayerSummaryDTO {
        public String id;
        public String name;
        public int score;
        public int roundsWon;
    }

    public class AdminGameSummaryDTO {
        public String gameId;
        public String status; // WAITING, IN_PROGRESS, FINISHED
        public int currentRound;
        public int totalRounds;
        public String createdAt;
        public String lastUpdate;
        public List<AdminPlayerSummaryDTO> players;
    }

    public class AdminGameDetailDTO {
        public String gameId;
        public String status;
        public int currentRound;
        public int totalRounds;
        public Map<String, Integer> roundsWon; // {"max":1,"NewUser":2}
        public GameStateDTO state;                   // Ton DTO normal du game
    }
}
