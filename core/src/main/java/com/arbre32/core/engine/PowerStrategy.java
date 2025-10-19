package com.arbre32.core.engine;
import com.arbre32.core.model.Card;
import com.arbre32.core.player.Player;
public interface PowerStrategy {
    void apply(GameContext ctx, Player player, Card card);
}
