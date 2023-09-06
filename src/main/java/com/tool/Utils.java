package com.tool;

import com.tool.Trees.Tree;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Utils {

    private static int treeNodeId = 0;

    public static TreeRepresentation popolateTree(Tree tree){

        TreeNode root = null;
        Element rootJsoup = tree.getParsedDOM().select("*").first(); //remove #root element added by Jsoup
        root = traverseTree(rootJsoup.children().first(),root);

        return new TreeRepresentation(root);
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

    public static String getTestDOM(String path){
        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
            in.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        return contentBuilder.toString();
    }

    public static String getId(Set<String> attributes){

        for(String attrValue: attributes){
            String token[] = attrValue.split("=");
            String attr = token[0];
            if(attr.equalsIgnoreCase("id")){
                return token[1];
            }
        }return null;
    }

    public static Set<String> getAttributes(String data){

        Set<String> attrValues = new HashSet<>();
        String token [] = data.split("#");


        for(int i = 1 ; i < token.length; i++){
            if(token[i] != ""){
                attrValues.add(token[i]);
            }
        }
        return attrValues;
    }

    public static Set<String> getChildrenData(String data){
        Set<String> childrenData = new HashSet<>();
        String token[] = data.split("##");

        for(String s: token){
            childrenData = getAttributes(s);
        }
        return childrenData;

    }

    public static String getTag(String data){

        String token [] = data.split("#");

        return token[0];

    }
}
