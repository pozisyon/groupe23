package com.arbre32.core.power;
import com.arbre32.core.model.Rank;
public enum PowerType { ACE, KING, QUEEN, JACK, NONE;
  public static PowerType of(Rank r){
      return switch(r){ case ACE->ACE;
          case KING->KING;
          case QUEEN->QUEEN;
          case JACK->JACK; default->NONE;
      };
  }
}
