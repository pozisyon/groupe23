package com.arbre32.api.lobby;
import com.arbre32.api.game.GameService;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/lobby")
@CrossOrigin(origins="http://localhost:5173", allowCredentials="true")
public class LobbyController {
  private final GameService service;
  public LobbyController(GameService s){ this.service=s; }
  @GetMapping("/open")
  public List<Map<String,Object>> open(){ return service.openGames(); }
}
