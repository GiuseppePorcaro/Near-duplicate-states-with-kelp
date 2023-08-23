package com.tool;

import com.tool.Trees.Tree;
import com.tool.Trees.TreeFactory;
import com.tool.similarity.AllAttributesDiceSorensenSimilarity;
import com.tool.similarity.AllAttributesJaccardSimilarity;
import com.tool.similarity.ChildrenBasedJaccardSimilarity;
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

        float LAMBDA = 0.4f;
        float MU = 0.4f;
        float terminalFactor = 1;
        float similarityThreshold = 0.01f;
        String representationIdentifier = null; //????

        StructureElementSimilarityI jaccardSimilarity = new AllAttributesJaccardSimilarity();
        StructureElementSimilarityI diceSorensen = new AllAttributesDiceSorensenSimilarity();
        StructureElementSimilarityI childrenBasedJaccardSimilarity = new ChildrenBasedJaccardSimilarity();
        StructureElementSimilarityI kelpStandardSimilarity = new ExactMatchingStructureElementSimilarity();


        DirectKernel<TreeRepresentation> smoothedPartialTreeKernelOnNodeAttributes = new SmoothedPartialTreeKernel(LAMBDA,MU,terminalFactor,similarityThreshold,jaccardSimilarity,representationIdentifier);
        DirectKernel<TreeRepresentation> smoothedPartialTreeKernelOnNodeChildren = new SmoothedPartialTreeKernel(LAMBDA,MU,terminalFactor,similarityThreshold,childrenBasedJaccardSimilaritya,representationIdentifier);



        Tree treeANoScript = TreeFactory.createTree("src/main/resources/testChildrenA.html","noScript");
        Tree treeBNoScript = TreeFactory.createTree("src/main/resources/testChildrenB.html","noScript");

        if(treeANoScript == null || treeBNoScript == null){
            System.out.println("Type of tree not defined");
            return;
        }
        TreeRepresentation kelpTreeANoScript = popolateTree(treeANoScript);
        TreeRepresentation kelpTreeBNoScript = popolateTree(treeBNoScript);

        float kernelOnAttributes = smoothedPartialTreeKernelOnNodeAttributes.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);
        float kernelOnChildren = smoothedPartialTreeKernelOnNodeChildren.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);


        System.out.println("Kernel: ("+kernelOnAttributes+","+kernelOnChildren+")");

    }

    public static TreeRepresentation popolateTree(Tree tree){

        TreeNode root = null;
        root = traverseTree(tree.getParsedDOM(),root);

        return new TreeRepresentation(root);
    }

    public static TreeNode traverseTree(Element element, TreeNode father) {

        Map<String, String> nodeAttributes = new HashMap<>();
        for(Attribute attribute: element.attributes()){
            nodeAttributes.put(attribute.getKey(), attribute.getValue());
        }
        StructureElement content = new MyStructureElement(element.tagName(),nodeAttributes);

        //System.out.println(element.tagName()+" - "+nodeAttributes+"\t\t\t\tprof: "+prof);

        TreeNode newNode = new TreeNode(id,content,father);
        id++;

        ArrayList<TreeNode> newNodeChildren = new ArrayList<>();
        Elements children = element.children();
        for (Element child : children) {
            TreeNode childNode = traverseTree(child,newNode);
            newNodeChildren.add(childNode);
        }
        newNode.setChildren(newNodeChildren);
        content.addAdditionalInformation("children",newNodeChildren);

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
