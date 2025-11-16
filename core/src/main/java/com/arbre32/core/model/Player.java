package com.arbre32.core.model;
import java.util.*; public class Player {
  private final String id; private final String name; private final List<Card> collected = new ArrayList<>(); private int score=0;
  public Player(String id,String name){this.id=id;this.name=name;}
  public String id(){return id;} public String name(){return name;}
  public void addCards(Collection<Card> cs){ collected.addAll(cs); }
  public List<Card> collected(){ return Collections.unmodifiableList(collected); }
  public int score(){ return score; } public void addScore(int d){ score+=d; }
}
