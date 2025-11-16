package com.arbre32.core.rules;
import com.arbre32.core.model.Card; import com.arbre32.core.tree.TreeNode;
public interface RuleEngine {
  boolean isDepthAllowed(TreeNode<Card> node, int turnIndex, int maxDepth);
  boolean isNodeTakable(TreeNode<Card> node, boolean childrenWereCollectedThisTurn);
}
