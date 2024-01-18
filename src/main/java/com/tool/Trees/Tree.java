package com.tool.Trees;

import org.jsoup.nodes.Document;

public abstract class Tree {

    protected Document parsedDOM;
    protected String stringDOM;

    public abstract void parseDOM(String stringDOM);

    public Document getParsedDOM() {
        return parsedDOM;
    }

    public abstract void setTreeDOM(String dom);

}
