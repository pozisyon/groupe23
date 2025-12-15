package com.arbre32.core.scoring;

import com.arbre32.core.model.Card;
import java.util.List;

public class DefaultScoringService implements ScoringService {

    /**
     * Score de base = somme des points de chaque carte
     * (les bonus/malus de pouvoir sont gérés dans GameEngine).
     */
    @Override
    public int scoreForCards(List<Card> cards) {
        int sum = 0;
        for (Card c : cards) {
            sum += c.rank().baseScore();
        }
        return sum;
    }

    /** Plus de bonus racine dans les règles → 0. */
    @Override
    public int rootBonus() {
        return 0;
    }
}
