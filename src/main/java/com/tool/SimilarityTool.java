package com.tool;

import com.tool.Trees.Tree;
import com.tool.Trees.TreeFactory;
import com.tool.similarity.AllAttributesDiceSorensenSimilarity;
import com.tool.similarity.AllAttributesJaccardSimilarity;
import com.tool.similarity.ChildrenBasedJaccardSimilarity;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.LexicalStructureElementSimilarity;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import it.uniroma2.sag.kelp.kernel.DirectKernel;
import it.uniroma2.sag.kelp.kernel.tree.SmoothedPartialTreeKernel;
import it.uniroma2.sag.kelp.kernel.tree.deltamatrix.DeltaMatrix;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SimilarityTool {

    private static int id = 0;

    private float LAMBDA = 0.4f;
    private float MU = 0.4f;
    private float terminalFactor = 1;
    private float similarityThreshold = 0.01f;
    private String representationIdentifier = null; //????
    private DirectKernel<TreeRepresentation> kernel;
    private NormalizationKernel<TreeRepresentation> kernelNormalized;

    public SimilarityTool(StructureElementSimilarityI similarity, float LAMBDA, float MU, float terminalFactor, float similarityThreshold, String representationIdentifier) {
        this.LAMBDA = LAMBDA;
        this.MU = MU;
        this.terminalFactor = terminalFactor;
        this.similarityThreshold = similarityThreshold;
        this.representationIdentifier = representationIdentifier;

        this.kernel = new SmoothedPartialTreeKernel(LAMBDA,MU,terminalFactor,similarityThreshold,similarity,representationIdentifier);
        this.kernelNormalized = new NormalizationKernel<>(kernel);

    }

    public float computeKernelNotNormalized(String pathHtml1, String pathHtml2,String treeType) throws Exception {
        TreeRepresentation firstTree = popolateTree(TreeFactory.createTree(pathHtml1,treeType));
        TreeRepresentation secondTree = popolateTree(TreeFactory.createTree(pathHtml2,treeType));

        return kernel.kernelComputation(firstTree,secondTree);
    }

    public float computeKernelNormalized(String pathHtml1, String pathHtml2, String treeType) throws Exception {
        TreeRepresentation firstTree = popolateTree(TreeFactory.createTree(pathHtml1,treeType));
        TreeRepresentation secondTree = popolateTree(TreeFactory.createTree(pathHtml2,treeType));

        return kernelNormalized.kernelComputation(firstTree,secondTree);
    }

    public TreeRepresentation popolateTree(Tree tree){

        TreeNode root = null;
        Element rootJsoup = tree.getParsedDOM().select("*").first(); //remove #root element added by Jsoup
        root = traverseTree(rootJsoup.children().first(),root);

        return new TreeRepresentation(root);
    }

    private TreeNode traverseTree(Element element, TreeNode father) {

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

    public void printTree(TreeRepresentation tree) {
        for(TreeNode n: tree.getAllNodes()){
            StructureElement s = n.getContent();
            System.out.println(s.getTextFromData());
        }

        /*for(TreeNode n: kelpTreeANoScript.getAllNodes()){
            StructureElement s = n.getContent();
            Set<String> attributesSx = getAttributes(s.getTextFromData());
            System.out.println(getTag(s.getTextFromData())+" - "+attributesSx);
        }*/
    }

    public DirectKernel<TreeRepresentation> getKernel() {
        return kernel;
    }

    public void setKernel(DirectKernel<TreeRepresentation> kernel) {
        this.kernel = kernel;
    }

    public NormalizationKernel<TreeRepresentation> getKernelNormalized() {
        return kernelNormalized;
    }

    public void setKernelNormalized(NormalizationKernel<TreeRepresentation> kernelNormalized) {
        this.kernelNormalized = kernelNormalized;
    }

    /*
     * Contiene semplicemente il codice che c'era nel vecchio main.
     * Serve per mettere da una parte tutto il codice richiamato per calcolare le similitudini.
     * Verrà rimosso in seguito
     * */
    public void start() throws Exception {

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


        Tree treeANoScript = TreeFactory.createTree("F:\\Università\\Libri_università\\Magistrale\\Tesi_magistrale\\Web_Test_Generation\\Crawls_complete\\GroundTruthModels\\addressbook\\crawl-addressbook-60min\\doms\\index.html","treeForCrawl");
        Tree treeBNoScript = TreeFactory.createTree("F:\\Università\\Libri_università\\Magistrale\\Tesi_magistrale\\Web_Test_Generation\\Crawls_complete\\GroundTruthModels\\addressbook\\crawl-addressbook-60min\\doms\\state10.html","treeForCrawl");

        System.out.println(treeANoScript.getParsedDOM());

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
}
