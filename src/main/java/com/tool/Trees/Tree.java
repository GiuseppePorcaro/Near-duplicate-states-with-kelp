package com.tool.Trees;

import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import org.jsoup.nodes.Document;

public abstract class Tree {

    protected Document parsedDOM;
    protected String stringDOM;

    public abstract void parseDOM(String stringDOM);

    public Document getParsedDOM() {
        return parsedDOM;
    }

}
