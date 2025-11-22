package com.arbre32.api.game;

import com.arbre32.api.game.dto.GameStateDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService service;

    public GameController(GameService service) {
        this.service = service;
    }

    public record CreateRequest(int mode) {}
    public record JoinRequest(String userHandle) {}
    public record PlayRequest(String cardId, String userHandle) {}

    // CREATE
    @PostMapping("/create")
    public Map<String, String> create(@RequestBody CreateRequest req) {
        GameStateDTO dto = service.createGame(req.mode());
        return Map.of("gameId", dto.gameId);
    }

    // GET STATE
    @GetMapping("/{id}/state")
    public ResponseEntity<GameStateDTO> state(@PathVariable String id) {
        GameStateDTO dto = service.getState(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    // JOIN
    @PostMapping("/{id}/join")
    public ResponseEntity<GameStateDTO> join(@PathVariable String id,
                                             @RequestBody JoinRequest req) {
        GameStateDTO dto = service.joinGame(id, req.userHandle());
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    // PLAY
    @PostMapping("/{id}/play")
    public ResponseEntity<?> play(@PathVariable String id,
                                  @RequestBody PlayRequest req) {

        var res = service.play(id, req.cardId(), req.userHandle());

        if (res.status() == 200) {
            // On retourne l'état du jeu mis à jour (GameStateDTO)
            return ResponseEntity.ok(res.state());
        }

        return ResponseEntity
                .status(res.status())
                .body(Map.of("error", res.message()));
    }

    // OPEN GAMES FOR LOBBY
    @GetMapping("/open")
    public List<Map<String, Object>> open() {
        return service.openGames();
    }
}
