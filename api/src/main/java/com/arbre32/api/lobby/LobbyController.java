package com.arbre32.api.lobby;

import com.arbre32.api.game.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lobby")

public class LobbyController {

    private final GameService service;

    public LobbyController(GameService service) {
        this.service = service;
    }

    @GetMapping("/open")
    public List<Map<String, Object>> openGames() {
        return service.openGames();
    }
}
