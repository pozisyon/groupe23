package com.arbre32.core.tree;

import java.util.*;
import java.util.function.Predicate;

public class GameTree<T> {
    private TreeNode<T> root;

    public GameTree(TreeNode<T> root) { this.root = Objects.requireNonNull(root); }

    public TreeNode<T> root(){ return root; }
    public void setRoot(TreeNode<T> r){ this.root = Objects.requireNonNull(r); }

    public List<TreeNode<T>> nodesBreadth(){
        List<TreeNode<T>> list = new ArrayList<>();
        Queue<TreeNode<T>> q = new ArrayDeque<>();
        q.add(root);
        while(!q.isEmpty()){
            TreeNode<T> n = q.remove();
            list.add(n);
            q.addAll(n.children());
        }
        return list;
    }

    public Optional<TreeNode<T>> find(Predicate<TreeNode<T>> pred){
        return nodesBreadth().stream().filter(pred).findFirst();
    }

    public List<TreeNode<T>> leaves(){
        List<TreeNode<T>> out = new ArrayList<>();
        for (TreeNode<T> n : nodesBreadth()) if (n.children().isEmpty()) out.add(n);
        return out;
    }

    public int maxDepth(){
        int m=0;
        for (TreeNode<T> n : nodesBreadth()) m = Math.max(m, n.depth());
        return m;
    }
}
