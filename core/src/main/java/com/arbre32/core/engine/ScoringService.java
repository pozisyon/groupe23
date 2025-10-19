package com.arbre32.core.engine;
import com.arbre32.core.model.Card;
import java.util.List;
public class ScoringService {
    public int compute(List<Card> cards){
        int s = 0;
        for(Card c: cards) s += c.value();
        return s;
    }
}
