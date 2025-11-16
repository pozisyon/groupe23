package com.arbre32.core;

import com.arbre32.core.engine.GameEngine;
import com.arbre32.core.engine.GameState;
import com.arbre32.core.model.Card;
import com.arbre32.core.model.Move;
import com.arbre32.core.tree.TreeNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de non-régression :
 * Vérifie que sur plusieurs parties simulées, aucune carte ne disparaît.
 */
public class GameEngineConservationTest {

    private final GameEngine engine = new GameEngine();
    private final Random rnd = new Random();

    @Test
    void testTotalCardsConserved_onManyGames_32() {
        int games = 100; // tu peux monter à 500 si tu veux

        for (int g = 0; g < games; g++) {
            GameState st = engine.startNewGame(true); // mode 32 cartes

            int initialCount = 32;
            int guard = 200; // limite sécurité pour éviter boucle infinie

            while (guard-- > 0) {
                // On cherche une carte jouable
                TreeNode<Card> playable = st.tree().nodesBreadth().stream()
                        .filter(n -> engine.isPlayable(st, n, !n.children().isEmpty()))
                        .findAny()
                        .orElse(null);

                if (playable == null) {
                    // plus de coups jouables
                    break;
                }

                // On joue un coup aléatoire sur une carte jouable
                String currentPlayer = st.turnPlayerId();
                engine.applyMove(st, new Move(currentPlayer, playable.value().id()));
            }

            // À la fin : cartes collectées + cartes restantes dans l'arbre == 32
            int collectedP1 = st.p1().collected().size();
            int collectedP2 = st.p2().collected().size();
            int remaining = st.tree().nodesBreadth().size();

            int total = collectedP1 + collectedP2 + remaining;

            assertEquals(initialCount, total,
                    "Incohérence dans la partie " + g +
                            " : total=" + total +
                            " (p1=" + collectedP1 +
                            ", p2=" + collectedP2 +
                            ", rest=" + remaining + ")");
        }
    }
}
