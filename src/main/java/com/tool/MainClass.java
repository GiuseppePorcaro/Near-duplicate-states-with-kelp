package com.tool;

import com.tool.Trees.FullTree;
import com.tool.Trees.Tree;
import com.tool.Trees.TreeFactory;
import com.tool.Trees.TreeNoScript;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import it.uniroma2.sag.kelp.kernel.DirectKernel;
import it.uniroma2.sag.kelp.kernel.tree.SmoothedPartialTreeKernel;
import it.uniroma2.sag.kelp.kernel.tree.deltamatrix.DeltaMatrix;
import it.uniroma2.sag.kelp.kernel.tree.deltamatrix.StaticDeltaMatrix;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class

MainClass {

    private static int id = 0;

    public static void main(String args[]){

        /*
        *1)Ottenere rappresentazione di un DOM
        *2)Capire quale funzione dello smoothedpartialTreeKernel usare per valutare la similitudine tra due alberi
        *3)Creare la funzione di similitudine
        *
        * */

        float LAMBDA = 0.4f;
        float MU = 0.4f;
        float terminalFactor = 1;

        //potrei ignorare i nodi che hanno uno score al di sotto del 0.2 (?)
        float similarityThreshold = 0.01f;
        StructureElementSimilarityI nodeSimilarity = new AllAttributesJaccardSimilarity();
        String representationIdentifier = null;

        DirectKernel<TreeRepresentation> smoothedPartialTreeKernel = new SmoothedPartialTreeKernel(LAMBDA,MU,terminalFactor,similarityThreshold,nodeSimilarity,representationIdentifier);

        Tree treeANoScript = TreeFactory.createTree("src/main/resources/testDOMC.html","noScript");
        Tree treeBNoScript = TreeFactory.createTree("src/main/resources/testDOMB.html","noScript");
        Tree treeCNoScript = TreeFactory.createTree("src/main/resources/testDOMA.html","noScript");

        if(treeANoScript == null || treeCNoScript == null){
            System.out.println("Type of tree not defined");
            return;
        }

        //TreeRepresentation kelpTreeA = popolateTree(treeA);
        TreeRepresentation kelpTreeANoScript = popolateTree(treeANoScript);
        TreeRepresentation kelpTreeBNoScript = popolateTree(treeCNoScript);


        //printTree(kelpTreeANoScript);

        //Popolare queste due rappresentazioni con gli StructureElement
        float kernel = smoothedPartialTreeKernel.kernelComputation(kelpTreeANoScript,kelpTreeANoScript);
        //StaticDeltaMatrix deltaMatrix = (StaticDeltaMatrix) smoothedPartialTreeKernel.getDeltaMatrix();


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
