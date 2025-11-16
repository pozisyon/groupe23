package com.arbre32.core.factory;

import com.arbre32.core.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DeckFactory {
    private DeckFactory(){}

    public static List<Card> build32(){
        List<Card> deck = new ArrayList<>();
        Rank[] ranks = { Rank.ACE, Rank.KING, Rank.QUEEN, Rank.JACK, Rank.TEN, Rank.NINE, Rank.EIGHT, Rank.SEVEN };
        for (Suit s : Suit.values()){
            for (Rank r : ranks){
                deck.add(new Card(r, s));
            }
        }
        Collections.shuffle(deck);
        return deck;
    }

    public static List<Card> build52(){
        List<Card> deck = new ArrayList<>();
        for (Suit s : Suit.values()){
            for (Rank r : Rank.values()){
                deck.add(new Card(r, s));
            }
        }
        Collections.shuffle(deck);
        return deck;
    }
}
