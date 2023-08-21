package com.tool.Trees;

public abstract class TreeFactory {

    public static Tree createTree(String path, String typeOfTree){
        switch (typeOfTree.toLowerCase()){
            case "all":
                return new FullTree(path);
            case "noscript":
                return new TreeNoScript(path);
            default:
                return null;
        }
    }
}
