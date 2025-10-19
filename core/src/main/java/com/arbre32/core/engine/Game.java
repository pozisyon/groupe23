package com.arbre32.core.engine;
import com.arbre32.core.model.Board;
import com.arbre32.core.player.Player;
import java.util.List;
public class Game {
    private final Board board;
    private final List<Player> players;
    private final RuleEngine rules;
    private final ScoringService scoring;
    private int current = 0;
    public Game(Board board, List<Player> players, RuleEngine rules, ScoringService scoring){
        this.board = board; this.players = players; this.rules = rules; this.scoring = scoring;
    }
    public Player currentPlayer(){ return players.get(current); }
    public void nextPlayer(){ current = (current + 1) % players.size(); }
    public Board board(){ return board; }
    public RuleEngine rules(){ return rules; }
    public ScoringService scoring(){ return scoring; }
    public List<Player> players(){ return players; }
}
