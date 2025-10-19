package com.arbre32.core.model;
import com.arbre32.core.engine.PowerStrategy;
public class Card {
    private final String rank;   // A,K,Q,J,10,...
    private final String suit;   // ♣,♦,♥,♠
    private final int value;
    private final PowerStrategy power; // nullable
    public Card(String rank, String suit, int value, PowerStrategy power) {
        this.rank = rank; this.suit = suit; this.value = value; this.power = power;
    }
    public String rank(){ return rank; }
    public String suit(){ return suit; }
    public int value(){ return value; }
    public PowerStrategy power(){ return power; }
    @Override public String toString(){ return rank + suit; }
}
