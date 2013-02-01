/*
 * Copyright 2004-2011 H2 Group. Multiple-Licensed under the H2 License,
 * Version 1.0, and under the Eclipse Public License, Version 1.0
 * (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package com.codefollower.yourbase.dbobject.index;

import com.codefollower.yourbase.result.Row;
import com.codefollower.yourbase.result.SearchRow;

/**
 * The cursor implementation for a tree index.
 */
public class TreeCursor implements Cursor {
    private final TreeIndex tree;
    private TreeNode node;
    private boolean beforeFirst;
    private final SearchRow first, last;

    TreeCursor(TreeIndex tree, TreeNode node, SearchRow first, SearchRow last) {
        this.tree = tree;
        this.node = node;
        this.first = first;
        this.last = last;
        beforeFirst = true;
    }

    public Row get() {
        return node == null ? null : node.row;
    }

    public SearchRow getSearchRow() {
        return get();
    }

    public boolean next() {
        if (beforeFirst) {
            beforeFirst = false;
            if (node == null) {
                return false;
            }
            if (first != null && tree.compareRows(node.row, first) < 0) {
                node = next(node);
            }
        } else {
            node = next(node);
        }
        if (node != null && last != null) {
            if (tree.compareRows(node.row, last) > 0) {
                node = null;
            }
        }
        return node != null;
    }

    public boolean previous() {
        node = previous(node);
        return node != null;
    }

    /**
     * Get the next node if there is one.
     *
     * @param x the node
     * @return the next node or null
     */
    private static TreeNode next(TreeNode x) {
        if (x == null) {
            return null;
        }
        TreeNode r = x.right;
        if (r != null) {
            x = r;
            TreeNode l = x.left;
            while (l != null) {
                x = l;
                l = x.left;
            }
            return x;
        }
        TreeNode ch = x;
        x = x.parent;
        while (x != null && ch == x.right) {
            ch = x;
            x = x.parent;
        }
        return x;
    }


    /**
     * Get the previous node if there is one.
     *
     * @param x the node
     * @return the previous node or null
     */
    private static TreeNode previous(TreeNode x) {
        if (x == null) {
            return null;
        }
        TreeNode l = x.left;
        if (l != null) {
            x = l;
            TreeNode r = x.right;
            while (r != null) {
                x = r;
                r = x.right;
            }
            return x;
        }
        TreeNode ch = x;
        x = x.parent;
        while (x != null && ch == x.left) {
            ch = x;
            x = x.parent;
        }
        return x;
    }

}