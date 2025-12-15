package com.arbre32.core.rules;

import com.arbre32.core.model.Card;
import com.arbre32.core.tree.TreeNode;

public class DefaultRuleEngine implements RuleEngine {

    @Override
    public boolean isDepthAllowed(TreeNode<Card> node, int turnIndex, int maxDepth) {
        int requiredDepth = Math.max(0, maxDepth - turnIndex);
        // ✅ seule la profondeur exacte est autorisée
        return node.depth() == requiredDepth;
    }

    @Override
    public boolean isNodeTakable(TreeNode<Card> node, boolean childrenWereCollectedThisTurn) {
        // Pour l’instant : toute carte non removed, le reste se gère dans GameEngine.
        return !node.value().isRemoved();
    }
}
