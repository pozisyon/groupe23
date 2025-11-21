package com.arbre32.api.game;

import com.arbre32.api.game.dto.GameStateDTO;
import com.arbre32.api.chat.ChatMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService service;
    private final SimpMessagingTemplate messaging;

    public GameController(GameService service, SimpMessagingTemplate messaging) {
        this.service = service;
        this.messaging = messaging;
    }

    // --- DTOs des requêtes ---
    public record CreateRequest(int mode) {}
    public record JoinRequest(String userHandle) {}
    public record PlayRequest(String cardId, String userHandle) {}

    // 1) CRÉER UNE PARTIE
    @PostMapping("/create")
    public Map<String, String> create(@RequestBody CreateRequest req) {
        GameStateDTO dto = service.createGame(req.mode());
        return Map.of("gameId", dto.gameId);
    }

    // 2) OBTENIR L'ÉTAT
    @GetMapping("/{id}/state")
    public ResponseEntity<GameStateDTO> state(@PathVariable String id) {
        GameStateDTO dto = service.getState(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    // 3) REJOINDRE
    @PostMapping("/{id}/join")
    public ResponseEntity<GameStateDTO> join(@PathVariable String id,
                                             @RequestBody JoinRequest req) {
        GameStateDTO dto = service.joinGame(id, req.userHandle());
        if (dto == null) return ResponseEntity.notFound().build();

        // 🔔 Notification dans le chat
        messaging.convertAndSend(
                "/topic/chat/" + id,
                new ChatMessage(
                        "SYSTEM",
                        req.userHandle() + " a rejoint la partie.",
                        System.currentTimeMillis()
                )
        );

        return ResponseEntity.ok(dto);
    }

    // 4) JOUER
    @PostMapping("/{id}/play")
    public ResponseEntity<?> play(@PathVariable String id,
                                  @RequestBody PlayRequest req) {
        GameService.PlayResult res = service.play(id, req.cardId(), req.userHandle());

        if (res.status() == 200) {

            // 🔥 Envoi automatique d'un message dans le chat
            String msg = req.userHandle() + " a joué " + res.playedCard();

            if (!res.takenCards().isEmpty()) {
                msg += " et a ramassé " + res.takenCards().size() + " carte(s).";
            }

            messaging.convertAndSend(
                    "/topic/chat/" + id,
                    new ChatMessage("SYSTEM", msg, System.currentTimeMillis())
            );

            return ResponseEntity.ok(res.state());
        }

        return ResponseEntity
                .status(res.status())
                .body(Map.of("error", res.message()));
    }

    // 5) LISTE DES PARTIES OUVERTES (Lobby)
    @GetMapping("/open")
    public List<Map<String, Object>> open() {
        return service.openGames();
    }
}
