package com.tool.Trees;

public abstract class TreeFactory {

    public static Tree createTree(String path, String typeOfTree) throws Exception {
        switch (typeOfTree.toLowerCase()){
            case "all":
                return new FullTree(path);
            case "noscript":
                return new TreeNoScript(path);
            case "treeforcrawl":
                return new TreeForCrawl(path);
            default:
                throw new Exception("TreeType not valid! Choose:[all, noScript,treeForCrawl]");
        }
    }

    public static Tree createTreeDefaultConstructor(String typeOfTree) throws Exception {
        switch (typeOfTree.toLowerCase()){
            case "all":
                return new FullTree();
            case "noscript":
                return new TreeNoScript();
            case "treeforcrawl":
                return new TreeForCrawl();
            default:
                throw new Exception("TreeType not valid! Choose:[all, noScript,treeForCrawl]");
        }
    }
}
