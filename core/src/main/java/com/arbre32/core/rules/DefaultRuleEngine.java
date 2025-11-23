package com.arbre32.core.rules;
import com.arbre32.core.model.Card;
import com.arbre32.core.power.DefaultPowerStrategy;
import com.arbre32.core.power.PowerStrategy;
import com.arbre32.core.tree.TreeNode;

public class DefaultRuleEngine implements RuleEngine {
    private final PowerStrategy power = new DefaultPowerStrategy();
    @Override
    public boolean isDepthAllowed(TreeNode<Card> node, int turnIndex, int maxDepth) {
        int required = Math.max(0, maxDepth - turnIndex);
        return node.depth() >= required;
    }
    @Override
    public boolean isNodeTakable(TreeNode<Card> node, boolean childrenWereCollectedThisTurn) {
        if (node.value().isPower()) return power.canTakePowerCardThisTurn(node, childrenWereCollectedThisTurn);
        return true;
    }
}
