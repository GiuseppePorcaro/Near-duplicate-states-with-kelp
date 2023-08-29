package com.tool;

import com.tool.Trees.Tree;
import com.tool.Trees.TreeFactory;
import com.tool.similarity.AllAttributesDiceSorensenSimilarity;
import com.tool.similarity.AllAttributesJaccardSimilarity;
import com.tool.similarity.ChildrenBasedJaccardSimilarity;
import it.uniroma2.sag.kelp.data.representation.structure.CompositionalStructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.PosStructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.SyntacticStructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.*;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import it.uniroma2.sag.kelp.kernel.DirectKernel;
import it.uniroma2.sag.kelp.kernel.tree.SmoothedPartialTreeKernel;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

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
        StructureElementSimilarityI kelpStandardSimilarity = new LexicalStructureElementSimilarity();

        /*DirectKernel<TreeRepresentation> kernelAttr = new SmoothedPartialTreeKernel(LAMBDA,MU,terminalFactor,similarityThreshold,jaccardSimilarity,representationIdentifier);
        DirectKernel<TreeRepresentation> kernelChildre = new SmoothedPartialTreeKernel(LAMBDA,MU,terminalFactor,similarityThreshold,childrenBasedJaccardSimilarity,representationIdentifier);

        NormalizationKernel<TreeRepresentation> kernelOnAttributes = new NormalizationKernel<>(new SmoothedPartialTreeKernel(LAMBDA,MU,terminalFactor,similarityThreshold,jaccardSimilarity,representationIdentifier));
        NormalizationKernel<TreeRepresentation> kernelOnChildren = new NormalizationKernel<>(new SmoothedPartialTreeKernel(LAMBDA,MU,terminalFactor,similarityThreshold,kelpStandardSimilarity,representationIdentifier));
*/

        DirectKernel<TreeRepresentation> kernelAttributeNotNormalized = new SmoothedPartialTreeKernel(LAMBDA,MU,terminalFactor,similarityThreshold,jaccardSimilarity,representationIdentifier);
        DirectKernel<TreeRepresentation> kernelStandardNotNormalized = new SmoothedPartialTreeKernel(LAMBDA,MU,terminalFactor,similarityThreshold,kelpStandardSimilarity,representationIdentifier);

        NormalizationKernel<TreeRepresentation> kernelAttributeNormalized = new NormalizationKernel<>(new SmoothedPartialTreeKernel(LAMBDA,MU,terminalFactor,similarityThreshold,jaccardSimilarity,representationIdentifier));
        NormalizationKernel<TreeRepresentation> kernelStandardNormalized = new NormalizationKernel<>(new SmoothedPartialTreeKernel(LAMBDA,MU,terminalFactor,similarityThreshold,kelpStandardSimilarity,representationIdentifier));


        Tree treeANoScript = TreeFactory.createTree("src/main/resources/testDOMA.html","noScript");
        Tree treeBNoScript = TreeFactory.createTree("src/main/resources/testDOMB.html","noScript");

        if(treeANoScript == null || treeBNoScript == null){
            System.out.println("Type of tree not defined");
            return;
        }
        TreeRepresentation kelpTreeANoScript = popolateTree(treeANoScript);
        TreeRepresentation kelpTreeBNoScript = popolateTree(treeBNoScript);


        float kernelAttrNotNormalized = kernelAttributeNotNormalized.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);
        float kernelStandarNotNormalized = kernelStandardNotNormalized.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);
        float kernelAttrNormalized = kernelAttributeNormalized.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);
        float kernelStandarNormalized = kernelStandardNormalized.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);
        /*float kernelOnAttributes = smoothedPartialTreeKernelOnNodeAttributes.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);
        float kernelOnChildren = smoothedPartialTreeKernelOnNodeChildren.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);*/


        //System.out.println("Kernel normalized: ("+kernelAttributes+","+kernelChildren+")\nKernel not normalized: ("+kernelAttri+","+kernelChildr+")");

        System.out.println("Kernel not normalized: \n\tOn attributes: "+kernelAttrNotNormalized+"\n\tStandard: "+kernelStandarNotNormalized);
        System.out.println("Kernel normalized: \n\tOn attributes: "+kernelAttrNormalized+"\n\tStandard: "+kernelStandarNormalized);
    }

    public static TreeRepresentation popolateTree(Tree tree){

        TreeNode root = null;
        Element rootJsoup = tree.getParsedDOM().select("*").first();
        root = traverseTree(rootJsoup.children().first(),root);

        return new TreeRepresentation(root);
    }

    public static TreeNode traverseTree(Element element, TreeNode father) {

        Map<String, String> nodeAttributes = new HashMap<>();
        for(Attribute attribute: element.attributes()){
            nodeAttributes.put(attribute.getKey(), attribute.getValue());
        }
        StructureElement content = new HTMLStructureElement(element.tagName(),nodeAttributes);

        //System.out.println(element.tagName()+" - "+nodeAttributes);

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
