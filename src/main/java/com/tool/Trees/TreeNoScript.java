package com.tool.Trees;

import com.tool.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;


public class TreeNoScript extends Tree{

    public TreeNoScript(String path){
        stringDOM = Utils.getTestDOM(path);
        parseDOM(stringDOM);

    }

    @Override
    public void parseDOM(String stringDOM) {
        parsedDOM = Jsoup.parse(stringDOM);

        for(Element tag: parsedDOM.select("script")){
            tag.remove();
        }
    }
}
