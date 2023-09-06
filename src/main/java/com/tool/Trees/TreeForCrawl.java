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

        for(Element tag: parsedDOM.select("style")){
            tag.remove();
        }

        /*Tag che sicuramente sono inutili nella similarità tra due html*/
        for(Element tag: parsedDOM.select("meta")){
            tag.remove();
        }

        for(Element tag: parsedDOM.select("br")){
            tag.remove();
        }

        for(Element tag: parsedDOM.select("hr")){
            tag.remove();
        }

        for(Element tag: parsedDOM.select("link")){
            tag.remove();
        }

        /*Tag che forse non incidono abbastanza nella similitudine(?) */
        for(Element tag: parsedDOM.select("tr")){
            tag.remove();
        }
        for(Element tag: parsedDOM.select("td")){
            tag.remove();
        }
        for(Element tag: parsedDOM.select("ul")){
            tag.remove();
        }
        for(Element tag: parsedDOM.select("li")){
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
