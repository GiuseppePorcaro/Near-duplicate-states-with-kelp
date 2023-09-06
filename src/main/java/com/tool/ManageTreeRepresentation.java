package com.tool;

import com.tool.Trees.Tree;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageTreeRepresentation {

    private static int treeNodeId = 0;

    public static TreeRepresentation popolateTree(Tree tree){

        TreeNode root = null;
        Element rootJsoup = tree.getParsedDOM().select("*").first(); //remove #root element added by Jsoup
        root = traverseTree(rootJsoup.children().first(),root);

        TreeRepresentation newTree = new DomRepresentation(root);

        return newTree;
    }

    private static TreeNode traverseTree(Element element, TreeNode father) {

        Map<String, String> nodeAttributes = new HashMap<>();
        for(Attribute attribute: element.attributes()){
            nodeAttributes.put(attribute.getKey(), attribute.getValue());
        }
        StructureElement content = new HTMLStructureElement(element.tagName(),nodeAttributes);

        //System.out.println(element.tagName()+" - "+nodeAttributes);

        TreeNode newNode = new TreeNode(treeNodeId,content,father);
        treeNodeId++;

        ArrayList<TreeNode> newNodeChildren = new ArrayList<>();
        Elements children = element.children();
        for (Element child : children) {
            TreeNode childNode = traverseTree(child,newNode);
            newNodeChildren.add(childNode);
        }
        newNode.setChildren(newNodeChildren);
        content.addAdditionalInformation("children",newNodeChildren);
        content.addAdditionalInformation("father",father);

        return newNode;
    }

    public static void printTree(TreeRepresentation tree) {
        for(TreeNode n: tree.getAllNodes()){
            StructureElement s = n.getContent();
            System.out.println(s.getTextFromData());
        }
    }

    public static void printTreeDepthFirst(TreeNode root, int profondità){
        System.out.println(root.getContent().getTextFromData()+" - "+profondità);

        profondità++;
        for(TreeNode child: root.getChildren()){
            printTreeDepthFirst(child, profondità);
        }
    }
}
