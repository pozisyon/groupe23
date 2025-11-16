package com.arbre32.core.scoring;
import com.arbre32.core.model.Card; import java.util.List;
public interface ScoringService { int scoreForCards(List<Card> cards); int rootBonus(); }
