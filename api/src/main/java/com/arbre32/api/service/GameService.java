package com.arbre32.api.service;
import com.arbre32.core.engine.*;
import com.arbre32.core.model.Board;
import com.arbre32.core.model.Card;
import com.arbre32.core.player.Player;
import com.arbre32.core.factory.DeckFactory;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
@Service
public class GameService {
    private Game game;
    public String startNewGame(){
        var players = List.of(new Player("Alice"), new Player("Bob"));
        var rules = new RuleEngine();
        var scoring = new ScoringService();
        List<Card> deck = DeckFactory.createDeck(32);
        Board board = new Board();
        this.game = new Game(board, players, rules, scoring);
        GameContext.getInstance().setState(new PlayingState());
        return "Game started with 2 players";
    }
    public Game getGame(){ return game; }
    public List<List<String>> currentBoardAsGrid(){
        List<Card> deck = DeckFactory.createDeck(32);
        List<List<String>> grid = new ArrayList<>();
        int idx = 0;
        for(int r=0;r<4;r++){
            List<String> row = new ArrayList<>();
            for(int c=0;c<8;c++){
                Card card = deck.get(idx++);
                row.add(card.toString());
            }
            grid.add(row);
        }
        return grid;
    }
}
