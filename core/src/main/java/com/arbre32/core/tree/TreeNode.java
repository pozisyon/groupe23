package com.arbre32.core.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TreeNode<T> {
    private final T value;
    private TreeNode<T> parent;
    private final List<TreeNode<T>> children = new ArrayList<>();

    public TreeNode(T value) { this.value = Objects.requireNonNull(value); }

    public T value(){ return value; }
    public TreeNode<T> parent(){ return parent; }
    public List<TreeNode<T>> children(){ return Collections.unmodifiableList(children); }

    public void addChild(TreeNode<T> child){
        child.parent = this;
        children.add(child);
    }

    public List<TreeNode<T>> _childrenMutable(){ return children; }

    public boolean isLeaf(){ return children.isEmpty(); }

    public int depth(){
        int d=0; TreeNode<T> p = parent;
        while(p!=null){ d++; p = p.parent; }
        return d;
    }

    public void detachFromParent(){
        if (parent != null){
            parent.children.remove(this);
            parent = null;
        }
    }
}
