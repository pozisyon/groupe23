package com.arbre32.core.engine;
import com.arbre32.core.player.Player;
public interface GameState {
    void handle(Game game, Player actor);
    String name();
}
