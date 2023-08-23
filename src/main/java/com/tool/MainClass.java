package com.tool;

import com.tool.Trees.Tree;
import com.tool.Trees.TreeFactory;
import com.tool.similarity.AllAttributesDiceSorensenSimilarity;
import com.tool.similarity.AllAttributesJaccardSimilarity;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.ExactMatchingStructureElementSimilarity;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import it.uniroma2.sag.kelp.kernel.DirectKernel;
import it.uniroma2.sag.kelp.kernel.tree.SmoothedPartialTreeKernel;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

import static com.tool.Utils.getAttributes;
import static com.tool.Utils.getTag;

public class

MainClass {

    private static int id = 0;

    public static void main(String args[]){

        /*
        *
        * 1)Probabilmente l'indice di jaccard è già un kernel
        * 2)Vedere se con una similarità predefinita si hanno valori simili - > (Danno valori molto molto vicini)
        * 3)Vedere se il kernel ottenuto dipende dalla lunghezza del dom: anche se due dom sono identici, se sono più lunghi hanno un kernel di valore
        * più alto
        * 4)Vedere come funziona un tree kernel in generale
        * 5)Vedere se il kernel è la somma delle similitudini delle coppie di nodi
        *
        *
        * */

        float LAMBDA = 0.4f;
        float MU = 0.4f;
        float terminalFactor = 1;

        //potrei ignorare i nodi che hanno uno score al di sotto del 0.2 (?)
        float similarityThreshold = 0.01f;
        StructureElementSimilarityI jaccardSimilarity = new AllAttributesJaccardSimilarity();
        StructureElementSimilarityI DiceSorensen = new AllAttributesDiceSorensenSimilarity();
        StructureElementSimilarityI predSimilarity = new ExactMatchingStructureElementSimilarity();
        String representationIdentifier = null;

        DirectKernel<TreeRepresentation> smoothedPartialTreeKernel = new SmoothedPartialTreeKernel(LAMBDA,MU,terminalFactor,similarityThreshold,jaccardSimilarity,representationIdentifier);

        Tree treeANoScript = TreeFactory.createTree("src/main/resources/testDOMA.html","noScript");
        Tree treeBNoScript = TreeFactory.createTree("src/main/resources/testDOMA.html","noScript");


        if(treeANoScript == null || treeBNoScript == null){
            System.out.println("Type of tree not defined");
            return;
        }

        //TreeRepresentation kelpTreeA = popolateTree(treeA);
        TreeRepresentation kelpTreeANoScript = popolateTree(treeANoScript,true);
        TreeRepresentation kelpTreeBNoScript = popolateTree(treeBNoScript,true);


        //printTree(kelpTreeANoScript);

        //Popolare queste due rappresentazioni con gli StructureElement
        float kernel = smoothedPartialTreeKernel.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);
        //StaticDeltaMatrix deltaMatrix = (StaticDeltaMatrix) smoothedPartialTreeKernel.getDeltaMatrix();


        System.out.println("Valore kernel: "+kernel);


    }

    public static TreeRepresentation popolateTree(Tree tree, boolean considerChildren){

        TreeNode root = null;
        root = traverseTree(tree.getParsedDOM(),root,0,considerChildren);

        return new TreeRepresentation(root);
    }

    public static TreeNode traverseTree(Element element, TreeNode father, int prof,  boolean considerChildren) {

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
            TreeNode childNode = traverseTree(child,newNode,prof,considerChildren);
            newNodeChildren.add(childNode);
        }
        newNode.setChildren(newNodeChildren);
        content.addAdditionalInformation("children",newNodeChildren);
        content.addAdditionalInformation("considerChildren",considerChildren);

        return newNode;
    }

    private static void printTree(TreeRepresentation kelpTreeANoScript) {
        for(TreeNode n: kelpTreeANoScript.getAllNodes()){
            StructureElement s = n.getContent();
            System.out.println(s.getTextFromData());
        }

        /*for(TreeNode n: kelpTreeANoScript.getAllNodes()){
            StructureElement s = n.getContent();
            Set<String> attributesSx = getAttributes(s.getTextFromData());
            System.out.println(getTag(s.getTextFromData())+" - "+attributesSx);
        }*/
    }

}
