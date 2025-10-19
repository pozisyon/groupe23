package com.arbre32.api.controller;
import com.arbre32.api.dto.GameStateDTO;
import com.arbre32.api.service.GameService;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/game")
@CrossOrigin
public class GameController {
    private final GameService service;
    public GameController(GameService service){ this.service = service; }
    @PostMapping("/start")
    public String start(){ return service.startNewGame(); }
    @GetMapping("/state")
    public GameStateDTO state(){
        var game = service.getGame();
        String status = (game == null) ? "NO_GAME" : "RUNNING";
        String currentPlayer = (game == null) ? "-" : game.currentPlayer().name();
        int p1 = (game == null) ? 0 : game.players().get(0).score();
        int p2 = (game == null) ? 0 : game.players().get(1).score();
        var grid = service.currentBoardAsGrid();
        return new GameStateDTO(status, grid, currentPlayer, p1, p2);
    }
}
