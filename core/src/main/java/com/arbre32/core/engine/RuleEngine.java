package com.arbre32.core.engine;
import com.arbre32.core.model.Board;
import com.arbre32.core.model.TreeNode;
import com.arbre32.core.player.Player;
public class RuleEngine {
    public boolean isValidMove(Board board, TreeNode node){
        return node != null;
    }
    public void applyMove(Board board, Player player, TreeNode node){
        // placeholder
    }
}
