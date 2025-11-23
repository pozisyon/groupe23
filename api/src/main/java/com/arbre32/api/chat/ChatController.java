package com.arbre32.api.chat;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController @RequestMapping("/api/chat")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody Map<String, String> req) {
        String gameId = req.get("gameId");
        String from = req.get("from");
        String message = req.get("message");

        if (gameId == null || from == null || message == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Champs requis"));
        }

        // Message broadcasté en WebSocket
        Map<String, String> payload = Map.of(
                "gameId", gameId,
                "from", from,
                "message", message
        );
        messagingTemplate.convertAndSend("/topic/chat/" + gameId, payload);

        return ResponseEntity.ok(Map.of("ok", true));
    }
}
