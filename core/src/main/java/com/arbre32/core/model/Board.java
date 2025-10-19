package com.arbre32.core.model;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
public class Board {
    private TreeNode root;
    public Board(){}
    public Board(TreeNode root){ this.root = root; }
    public TreeNode root(){ return root; }
    public void setRoot(TreeNode root){ this.root = root; }
    public List<TreeNode> leaves(){
        List<TreeNode> out = new ArrayList<>();
        collectLeaves(root, out);
        return out;
    }
    private void collectLeaves(TreeNode n, List<TreeNode> out){
        if(n==null) return;
        if(n.isLeaf()) out.add(n);
        for(TreeNode c : n.children()) collectLeaves(c, out);
    }
    public TreeNode find(Predicate<TreeNode> p){
        return dfs(root, p);
    }
    private TreeNode dfs(TreeNode n, Predicate<TreeNode> p){
        if(n==null) return null;
        if(p.test(n)) return n;
        for(TreeNode c : n.children()){
            TreeNode r = dfs(c, p);
            if(r!=null) return r;
        }
        return null;
    }
}
