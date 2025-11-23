package com.arbre32.core.power;
import com.arbre32.core.model.Card;
import com.arbre32.core.tree.TreeNode;
public interface PowerStrategy {
    boolean canTakePowerCardThisTurn(TreeNode<Card> node, boolean childrenWereCollectedThisTurn);
}
