package com.arbre32.core.power;
import com.arbre32.core.model.Card;
import com.arbre32.core.tree.TreeNode;
public class DefaultPowerStrategy implements PowerStrategy {
    @Override
    public boolean canTakePowerCardThisTurn(TreeNode<Card> node, boolean childrenWereCollectedThisTurn) {
        if (!node.children().isEmpty() && childrenWereCollectedThisTurn) return false;
        return true;
    }
}
