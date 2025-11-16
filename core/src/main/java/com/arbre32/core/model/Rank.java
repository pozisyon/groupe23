package com.arbre32.core.model;
public enum Rank {
  ACE("A",11,true), KING("K",4,true), QUEEN("Q",3,true), JACK("J",2,true),
  TEN("10",10,false), NINE("9",9,false), EIGHT("8",8,false), SEVEN("7",7,false),
  SIX("6",6,false), FIVE("5",5,false), FOUR("4",4,false), THREE("3",3,false), TWO("2",2,false);
  private final String label; private final int baseScore; private final boolean power;
  Rank(String l,int s,boolean p){label=l;baseScore=s;power=p;}
  public String label(){return label;} public int baseScore(){return baseScore;} public boolean isPower(){return power;}
}
