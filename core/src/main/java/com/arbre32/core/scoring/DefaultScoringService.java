package com.arbre32.core.scoring;
import com.arbre32.core.model.Card;
import java.util.List;
public class DefaultScoringService implements ScoringService {
    @Override public int scoreForCards(List<Card> cards) {
        int sum=0; for (Card c : cards) sum += c.rank().baseScore() + c.suit().weight(); return sum;
    }
    @Override public int rootBonus(){ return 10; }
}
