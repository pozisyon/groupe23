package com.arbre32.api.game;

import com.arbre32.api.game.dto.CreateGameRequest;
import com.arbre32.api.game.dto.PlayRequest;
import com.arbre32.api.user.dto.JoinRequest;
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

    @PostMapping("/create")
    public Map<String,String> create(@RequestBody CreateGameRequest req) {
        int mode = (req != null && req.mode == 52) ? 52 : 32;
        String id = service.create(mode);
        return Map.of("gameId", id);
    }

    @GetMapping("/{id}/state")
    public ResponseEntity<?> state(@PathVariable String id) {
        var dto = service.state(id);
        return dto != null ? ResponseEntity.ok(dto)
                : ResponseEntity.status(404).body(Map.of("error","Game not found"));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<?> join(@PathVariable String id, @RequestBody JoinRequest req) {
        var dto = service.join(id, req.playerId());
        return dto != null ? ResponseEntity.ok(dto)
                : ResponseEntity.status(404).body(Map.of("error","Game not found"));
    }

    @PostMapping("/{id}/play")
    public ResponseEntity<?> play(@PathVariable String id, @RequestBody PlayRequest req) {
        var res = service.play(id, req.cardId(), req.playerId());
        return (res.status() == 200)
                ? ResponseEntity.ok(res.state())
                : ResponseEntity.status(res.status()).body(Map.of("error", res.message()));
    }

    @GetMapping("/open")
    public List<Map<String,Object>> open() {
        return service.openGames();
    }
}
