package com.tool.representations;

import com.tool.Trees.Tree;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public int getTreeHeight(TreeNode node, int profondità){
        if(node == null){
            return 0;
        }

        int maxProfondità = 0;
        List<Integer> listChildrenProfondità = new ArrayList<>();

        if(!node.hasChildren()){
            return profondità;
        }else {
            profondità++;
            for(TreeNode child: node.getChildren()){
                listChildrenProfondità.add(getTreeHeight(child, profondità));
            }
            for(Integer p: listChildrenProfondità){
                if(p > maxProfondità){
                    maxProfondità = p;
                }
            }return maxProfondità;
        }
    }

    public float getAverageBranchingFactor(TreeNode root, float numNodes){
        if(root == null){
            return 0.0f;
        }

        //Numero di nodi non root / numero di nodi non leaf
        float numNodesNonRoot = numNodes-1;
        float numNodesNonLeaf = getNumNodesNonLeaf(root);

        if(numNodesNonLeaf == 0){
            return 0.0f;
        }
        return numNodesNonRoot/numNodesNonLeaf;
    }

    public float getNumNodesNonLeaf(TreeNode node){
        StructureElement s = node.getContent();
        if(!node.hasChildren()){
            return 0;
        }

        float result = 1;
        for(TreeNode child: node.getChildren()){
            result = result + getNumNodesNonLeaf(child);
        }

        return result;
    }

    public int getTreeDegree(TreeNode node){
        if(!node.hasChildren()){
            return 0;
        }
        int max = node.getChildren().size();

        List<Integer> listNumChildren = new ArrayList<>();
        for(TreeNode child: node.getChildren()){
            listNumChildren.add(getTreeDegree(child));
        }
        for(Integer num: listNumChildren){
            if(num > max){
                max = num;
            }
        }return max;

    }

    public float getDensity(TreeNode root){
        if(root == null){
            return 0;
        }

        int numNodes = getNumNodes(root);
        int height = getTreeHeight(root, 1);
        int maxNodes = (int) Math.pow(height+1,2)-1;

        return (float) numNodes /maxNodes;
    }



    /*Il numero di nodi lo tengo già, quindi probabilmente questa funzione non serve*/
    public int getNumNodes(TreeNode node){
        if(node == null){
            return 0;
        }

        int conter = 1;
        for(TreeNode children: node.getChildren()){
            conter += getNumNodes(children);
        }
        return conter;
    }
}
