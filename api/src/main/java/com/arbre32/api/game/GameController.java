package com.arbre32.api.game;

import com.arbre32.api.game.dto.CreateGameRequest;
import com.arbre32.api.game.dto.GameStateDTO;
import com.arbre32.api.game.dto.PlayRequest;
import com.arbre32.core.engine.GameState;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins="http://localhost:5173", allowCredentials="true")
public class GameController {

    private final GameService service;

    public GameController(GameService service) {
        this.service = service;
    }

    // -----------------------------------------
    // CREATE
    // -----------------------------------------
    @PostMapping("/create")
    public Map<String,String> create(@RequestBody CreateGameRequest req) {

        int mode = (req != null && req.mode == 52) ? 52 : 32;

        String id = service.create(mode);

        return Map.of("gameId", id);
    }

    // -----------------------------------------
    // STATE
    // -----------------------------------------
    @GetMapping("/{id}/state")
    public ResponseEntity<?> state(@PathVariable String id) {
        var dto = service.state(id);
        return (dto != null)
                ? ResponseEntity.ok(dto)
                : ResponseEntity.status(404).body(Map.of("error", "Game not found"));
    }

    // -----------------------------------------
    // PLAY
    // -----------------------------------------
    @PostMapping("/{id}/play")
    public ResponseEntity<?> play(@PathVariable String id, @RequestBody PlayRequest req) {

        if (req == null || req.cardId == null)
            return ResponseEntity.badRequest().body(Map.of("error", "cardId required"));

        var res = service.play(id, req.cardId);

        return (res.status() == 200)
                ? ResponseEntity.ok(res.state())
                : ResponseEntity.status(res.status()).body(Map.of("error", res.message()));
    }

    // -----------------------------------------
    // JOIN
    // -----------------------------------------
    @PostMapping("/{id}/join")
    public ResponseEntity<?> join(@PathVariable String id) {

        var st = service.join(id);
        return (st != null)
                ? ResponseEntity.ok(st)
                : ResponseEntity.status(404).body(Map.of("error", "Game not found"));
    }

    //---------------------------------------------------------------
    // endpoint simulation
    //-------------------------------------------------
    @PostMapping("/{id}/simulate")
    public ResponseEntity<?> simulate(@PathVariable("id") String id) {

        GameStateDTO initial = service.state(id);
        if (initial == null)
            return ResponseEntity.status(404).body(Map.of("error", "Game not found"));

        List<Map<String, Object>> logs = new ArrayList<>();
        logs.add(Map.of("event", "initial_state", "state", initial));

        // recherche de toutes les feuilles jouables
        List<GameStateDTO.CardDTO> playable = initial.board.stream()
                .flatMap(List::stream)
                .filter(c -> c.playable)
                .toList();

        int turn = 0;
        String currentPlayer = initial.turnPlayer;

        while (!playable.isEmpty() && turn < 30) {

            GameStateDTO.CardDTO chosen = playable.get(0);

            var result = service.play(id, chosen.id);

            Map<String, Object> log = new HashMap<>();
            log.put("event", "move");
            log.put("turn", turn);
            log.put("player", currentPlayer);
            log.put("card", chosen);

            if (result.status() != 200) {
                log.put("error", result.message());
                logs.add(log);
                break;
            }

            logs.add(log);
            logs.add(Map.of("event", "state_update", "state", result.state()));

            // mise à jour du joueur courant
            currentPlayer = result.state().turnPlayer;

            // recalcul cartes jouables
            playable = result.state().board.stream()
                    .flatMap(List::stream)
                    .filter(c -> c.playable)
                    .toList();

            turn++;
        }

        logs.add(Map.of("event", "simulation_end"));

        // ======= FIN DE SIMULATION : STATISTIQUES =======

// dernier état du jeu
        GameStateDTO finalState = service.state(id);

// nombre de cartes ramassées
        GameState finalRawState = service.getRawState(id); // méthode ajoutée plus bas

        int cardsP1 = finalRawState.p1().collected().size();
        int cardsP2 = finalRawState.p2().collected().size();

// définition du gagnant
        String winner;
        if (cardsP1 > cardsP2) winner = "J1";
        else if (cardsP2 > cardsP1) winner = "J2";
        else winner = "Égalité";

// ajout au rapport
        logs.add(Map.of(
                "event", "final_result",
                "winner", winner,
                "scoreFinal", Map.of("player1", cardsP1, "player2", cardsP2),
                "cardsCollected", Map.of("player1", cardsP1, "player2", cardsP2),
                "turnsPlayed", turn,
                "reason", playable.isEmpty() ? "Plus de coups jouables" : "Limite de tours atteinte"
        ));


        return ResponseEntity.ok(logs);
    }


}
