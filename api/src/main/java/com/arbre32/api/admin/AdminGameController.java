package com.arbre32.api.admin;

import com.arbre32.core.engine.GameState;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST réservé à l'administration du jeu.
 * Permet de :
 * - Créer / supprimer des parties
 * - Lister toutes les parties (actives ou terminées)
 * - Forcer la fin d'une partie
 * - Passer à la manche suivante (BO3)
 * - Réinitialiser une partie complète
 */
@RestController
@RequestMapping("/api/admin/games")
@CrossOrigin(origins = "*")
public class AdminGameController {

    private final AdminGameService adminService;

    public AdminGameController(AdminGameService adminService) {
        this.adminService = adminService;
    }

    // ---------------------------------------------------------
    // LISTING
    // ---------------------------------------------------------

    /** Toutes les parties */
    @GetMapping
    public ResponseEntity<List<GameState>> listAll() {
        return ResponseEntity.ok(adminService.listGames());
    }

    /** Parties actives */
    @GetMapping("/active")
    public ResponseEntity<List<GameState>> listActive() {
        return ResponseEntity.ok(adminService.listActiveGames());
    }

    /** Parties terminées */
    @GetMapping("/finished")
    public ResponseEntity<List<GameState>> listFinished() {
        return ResponseEntity.ok(adminService.listFinishedGames());
    }

    // ---------------------------------------------------------
    // CREATE / DELETE
    // ---------------------------------------------------------

    /** Créer une nouvelle partie */
    @PostMapping("/create")
    public ResponseEntity<GameState> createGame(@RequestParam(defaultValue = "true") boolean mode32) {
        GameState game = adminService.createGame(mode32);
        return ResponseEntity.ok(game);
    }

    /** Supprimer une partie */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean removed = adminService.deleteGame(id);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // ---------------------------------------------------------
    // GET
    // ---------------------------------------------------------

    /** Récupérer une partie spécifique */
    @GetMapping("/{id}")
    public ResponseEntity<GameState> getOne(@PathVariable String id) {
        GameState game = adminService.getGame(id);
        return game != null ? ResponseEntity.ok(game) : ResponseEntity.notFound().build();
    }

    // ---------------------------------------------------------
    // ADMIN ACTIONS
    // ---------------------------------------------------------

    /** Forcer la fin de la partie */
    @PostMapping("/{id}/force-finish")
    public ResponseEntity<GameState> forceFinish(@PathVariable String id) {
        GameState game = adminService.forceFinish(id);
        return game != null ? ResponseEntity.ok(game) : ResponseEntity.notFound().build();
    }

    /** Passer à la manche suivante (BO3) */
    @PostMapping("/{id}/next-round")
    public ResponseEntity<GameState> nextRound(@PathVariable String id,
                                               @RequestParam(defaultValue = "true") boolean mode32) {
        GameState game = adminService.forceNextRound(id, mode32);
        return game != null ? ResponseEntity.ok(game) : ResponseEntity.notFound().build();
    }

    /** Réinitialiser une partie complète */
    @PostMapping("/{id}/reset")
    public ResponseEntity<GameState> resetMatch(@PathVariable String id,
                                                @RequestParam(defaultValue = "true") boolean mode32) {
        GameState game = adminService.resetMatch(id, mode32);
        return game != null ? ResponseEntity.ok(game) : ResponseEntity.notFound().build();
    }
}
