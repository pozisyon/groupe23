package com.arbre32.core.player;
import com.arbre32.core.model.Card;
import java.util.ArrayList;
import java.util.List;
public class Player {
    private final String name;
    private int score = 0;
    private final List<Card> collected = new ArrayList<>();
    public Player(String name){ this.name = name; }
    public String name(){ return name; }
    public int score(){ return score; }
    public List<Card> collected(){ return collected; }
    public void addScore(int s){ score += s; }
    public void collect(Card c){ collected.add(c); }
}
