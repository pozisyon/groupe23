package com.arbre32.core.factory;
import com.arbre32.core.model.Card;
import com.arbre32.core.engine.PowerStrategy;
import java.util.*;
public class DeckFactory {
    public static List<Card> createDeck(int size){
        List<Card> deck = new ArrayList<>();
        String[] suits = {"♣","♦","♥","♠"};
        String[] ranks32 = {"7","8","9","10","J","Q","K","A"};
        String[] ranks52 = {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};
        String[] ranks = size==32 ? ranks32 : ranks52;
        for(String s : suits){
            for(String r : ranks){
                int value = mapValue(r);
                PowerStrategy power = null;
                deck.add(new Card(r, s, value, power));
            }
        }
        Collections.shuffle(deck, new Random(42));
        return deck.subList(0, Math.min(size, deck.size()));
    }
    private static int mapValue(String r){
        return switch(r){
            case "A" -> 11;
            case "K" -> 4;
            case "Q" -> 3;
            case "J" -> 2;
            default -> {
                try{ yield Integer.parseInt(r); } catch(Exception e){ yield 0; }
            }
        };
    }
}
