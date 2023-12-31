package com.tool.Trees;

import com.tool.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class TreeForCrawl extends Tree {

    public TreeForCrawl(String path){
        stringDOM = Utils.getTestDOM(path);
        parseDOM(stringDOM);

    }

    @Override
    public void parseDOM(String stringDOM) {
        parsedDOM = Jsoup.parse(stringDOM);

        for(Element tag: parsedDOM.select("script")){
            tag.remove();
        }

        removeComments(parsedDOM);

    }

    private void removeComments(Node node) {
        for (int i = 0; i < node.childNodes().size();) {
            Node child = node.childNode(i);
            if (child.nodeName().equals("#comment")) child.remove();
            else {
                removeComments(child);
                i++;
            }
        }
    }


}
