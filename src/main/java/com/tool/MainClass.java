package com.tool;

import com.tool.Trees.FullTree;
import com.tool.Trees.Tree;
import com.tool.Trees.TreeNoScript;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import it.uniroma2.sag.kelp.kernel.tree.SmoothedPartialTreeKernel;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class MainClass {

    private static int id = 0;

    public static void main(String args[]){

        /*
        *1)Ottenere rappresentazione di un DOM
        *2)Capire quale funzione dello smoothedpartialTreeKernel usare per valutare la similitudine tra due alberi
        *3)Creare la funzione di similitudine
        *
        * */

        float LAMBDA = 0;
        float MU = 0;
        float terminalFactor = 0;
        float similarityThreshold = 0;
        StructureElementSimilarityI nodeSimilarity = new AllAttributesJaccardSimilarity();
        String representationIdentifier = null;

        SmoothedPartialTreeKernel smoothedPartialTreeKernel = new SmoothedPartialTreeKernel(LAMBDA,MU,terminalFactor,similarityThreshold,nodeSimilarity,representationIdentifier);

        Tree treeA = new FullTree("src/main/resources/testDOMA.html");
        Tree treeANoScript = new TreeNoScript("src/main/resources/testDOMA.html");
        Tree treeBNoScript = new TreeNoScript("src/main/resources/testDOMB.html");

        //TreeRepresentation kelpTreeA = popolateTree(treeA);
        TreeRepresentation kelpTreeANoScript = popolateTree(treeANoScript);
        TreeRepresentation kelpTreeBNoScript = popolateTree(treeBNoScript);


        //Set<String> attributeSd = getAttributes(sd.getTextFromData());

        /*for(TreeNode n: kelpTreeANoScript.getAllNodes()){
            StructureElement s = n.getContent();
            Set<String> attributesSx = getAttributes(s.getTextFromData());
            System.out.println(getTag(s.getTextFromData())+" - "+attributesSx);
        }*/

        //printTree(kelpTreeANoScript);

        //Popolare queste due rappresentazioni con gli StructureElement
        float kernel = smoothedPartialTreeKernel.kernelComputation(kelpTreeBNoScript,kelpTreeANoScript);

        System.out.println("Valore kernel: "+kernel);
    }

    public static TreeRepresentation popolateTree(Tree tree){

        TreeNode root = null;
        root = traverseTree(tree.getParsedDOM(),root,0);

        return new TreeRepresentation(root);
    }

    public static TreeNode traverseTree(Element element, TreeNode father, int prof) {

        Map<String, String> nodeAttributes = new HashMap<>();
        for(Attribute attribute: element.attributes()){
            nodeAttributes.put(attribute.getKey(), attribute.getValue());
        }
        StructureElement content = new MyStructureElement(element.tagName(),nodeAttributes);

        //System.out.println(element.tagName()+" - "+nodeAttributes+"\t\t\t\tprof: "+prof);
        prof++;

        TreeNode newNode = new TreeNode(id,content,father);
        id++;

        ArrayList<TreeNode> newNodeChildren = new ArrayList<>();
        Elements children = element.children();
        for (Element child : children) {
            TreeNode childNode = traverseTree(child,newNode,prof);
            newNodeChildren.add(childNode);
        }
        newNode.setChildren(newNodeChildren);
        return newNode;
    }

    private static Set<String> getAttributes(String data){

        Set<String> attrValues = new HashSet<>();
        String token [] = data.split("#");


        for(int i = 1 ; i < token.length; i++){
            if(token[i] != ""){
                attrValues.add(token[i]);
            }
        }
        return attrValues;
    }

    private static String getTag(String data){

        String token [] = data.split("#");

        return token[0];

    }

    private static void printTree(TreeRepresentation kelpTreeANoScript) {
        for(TreeNode n: kelpTreeANoScript.getAllNodes()){
            StructureElement s = n.getContent();
            System.out.println(s.getTextFromData());
        }
    }

}
