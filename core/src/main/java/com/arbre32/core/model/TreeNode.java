package com.arbre32.core.model;
import java.util.ArrayList;
import java.util.List;
public class TreeNode {
    private final Card card;
    private TreeNode parent;
    private final List<TreeNode> children = new ArrayList<>();
    public TreeNode(Card card){ this.card = card; }
    public Card card(){ return card; }
    public TreeNode parent(){ return parent; }
    public List<TreeNode> children(){ return children; }
    public void addChild(TreeNode node){
        node.parent = this;
        children.add(node);
    }
    public boolean isLeaf(){ return children.isEmpty(); }
    public boolean isRoot(){ return parent == null; }
}
