package com.arbre32.core.model;
public enum Suit { SPADES("♠",3), HEARTS("♥",2), DIAMONDS("♦",1), CLUBS("♣",0);
  private final String symbol; private final int weight;
  Suit(String s,int w){symbol=s;weight=w;} public String symbol(){return symbol;} public int weight(){return weight;} }
