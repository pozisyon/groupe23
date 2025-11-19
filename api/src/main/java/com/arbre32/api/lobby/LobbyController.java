package com.arbre32.api.lobby;
import com.arbre32.api.game.GameService;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/lobby")
public class LobbyController {
  private final GameService service;
  public LobbyController(GameService s){ this.service=s; }
  @GetMapping("/open")
  public List<Map<String,Object>> open(){ return service.openGames(); }
}
