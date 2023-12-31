package com.tool.Trees;

import com.tool.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class FullTree extends Tree{

    public FullTree(String path){
        stringDOM = Utils.getTestDOM(path);
        parseDOM(stringDOM);
    }

    @Override
    public void parseDOM(String stringDOM) {
        parsedDOM = Jsoup.parse(stringDOM);
    }
}
