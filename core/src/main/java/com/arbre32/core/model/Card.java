package com.arbre32.core.model;
import java.util.Objects; import java.util.UUID;
public final class Card {
  private final String id; private final Rank rank; private final Suit suit;
  public Card(Rank rank, Suit suit){ this.id = rank.label()+suit.symbol()+"-"+UUID.randomUUID().toString().substring(0,8);
    this.rank=Objects.requireNonNull(rank); this.suit=Objects.requireNonNull(suit); }
  public String id(){return id;} public Rank rank(){return rank;} public Suit suit(){return suit;}
  public boolean isPower(){ return rank.isPower(); }
  public String toString(){return rank.label()+suit.symbol();}
}
