package com.tool.debug;

import com.tool.NormalizationKernel;
import com.tool.SimilarityTool;
import com.tool.Trees.Tree;
import com.tool.Trees.TreeFactory;
import com.tool.representations.ManageTreeRepresentation;
import com.tool.similarity.AllAttributesDiceSorensenSimilarity;
import com.tool.similarity.AllAttributesJaccardSimilarity;
import com.tool.similarity.ChildrenBasedJaccardSimilarity;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.LexicalStructureElementSimilarity;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.kernel.tree.PartialTreeKernel;
import it.uniroma2.sag.kelp.kernel.tree.SmoothedPartialTreeKernel;

import static com.tool.representations.ManageTreeRepresentation.*;

public class DebugClass {

    SmoothedPartialTreeKernel kernelAttributeNotNormalized;
    NormalizationKernel kernelAttributeNormalized;

    SmoothedPartialTreeKernel kernelStandardNotNormalized;
    NormalizationKernel kernelStandardNormalized;
    private String typeTree = "all";
    private SimilarityTool similarityTool;

    PartialTreeKernel partialTreeKernel = new PartialTreeKernel(0.4f,0.4f,1,typeTree);
    NormalizationKernel kernelChildBasedNormalized;
    public DebugClass(){
        StructureElementSimilarityI jaccardSimilarity = new AllAttributesJaccardSimilarity();
        StructureElementSimilarityI diceSorensen = new AllAttributesDiceSorensenSimilarity();
        StructureElementSimilarityI childrenBasedJaccardSimilarity = new ChildrenBasedJaccardSimilarity();
        StructureElementSimilarityI kelpStandardSimilarity = new LexicalStructureElementSimilarity();
        similarityTool = new SimilarityTool(jaccardSimilarity,0.4f,0.8f,1f,0.01f,typeTree);

        kernelAttributeNotNormalized = new SmoothedPartialTreeKernel(0.4f,0.4f,1f,0.01f,jaccardSimilarity,typeTree);
        kernelStandardNotNormalized = new SmoothedPartialTreeKernel(0.4f,0.4f,1,0.01f,kelpStandardSimilarity,typeTree);

        kernelAttributeNormalized = new NormalizationKernel(kernelAttributeNotNormalized);
        kernelStandardNormalized = new NormalizationKernel(kernelStandardNotNormalized);

        kernelChildBasedNormalized = new NormalizationKernel(new SmoothedPartialTreeKernel(0.4f,0.1f,1,0.01f,childrenBasedJaccardSimilarity,typeTree));
    }

    //Liste di oggetti omogenei ai quali è stato dato un valore di similarità basso:
    /*
    * "addressbook/crawl-addressbook-60min/doms/state176.html"
    * "addressbook/crawl-addressbook-60min/doms/state400.html"
    * */
    public void start() throws Exception {

        ManageTreeRepresentation manager = new ManageTreeRepresentation();

        //String dom1 = "/Users/giuseppeporcaro/Desktop/Libri_università/Magistrale/Tesi magistrale/Web Test Generation/Tool Web Testing/Near-duplicate-states-with-kelp/src/main/resources/testDOMA.html";
        //String dom2 = "/Users/giuseppeporcaro/Desktop/Libri_università/Magistrale/Tesi magistrale/Web Test Generation/Tool Web Testing/Near-duplicate-states-with-kelp/src/main/resources/testDOMB.html";
        String dom1 = "mrbs/crawl-mrbs-60min/doms/state23.html";
        String dom2 = "mrbs/crawl-mrbs-60min/doms/state253.html";
        String folderPath = "/run/media/giuseppeporcaro/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/"; //"/Volumes/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/"
        //String folderPath = "";

        //float kernelAttrNotNormalized = kernelAttributeNotNormalized.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);
        //float kernelStandarNotNormalized = kernelStandardNotNormalized.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);
        float kernelAttrNormalized = similarityTool.computeKernelNormalized(folderPath+dom1,folderPath+dom2,typeTree );
        //float kernelStandarNormalized = kernelStandardNormalized.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);
        //float partialTreeKernelNorm = partialTreeKernelNormalized.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);
        //float childBasedKernel = kernelChildBasedNormalized.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);


        //System.out.println("Kernel normalized: ("+kernelAttributes+","+kernelChildren+")\nKernel not normalized: ("+kernelAttri+","+kernelChildr+")");

        System.out.println("################################################################################");
        //System.out.println("Kernel not normalized: \n\tOn attributes: "+kernelAttrNotNormalized+"\n\tStandard: "+kernelStandarNotNormalized);
        System.out.println("Kernel normalized: \n\tOn attributes: "+kernelAttrNormalized/*+"\n\tStandard: "+kernelStandarNormalized*/);
        //System.out.println("\tPartialTreeKenrel: "+partialTreeKernelNorm);
        //System.out.println("\tKernel childBased: "+childBasedKernel);
    }

    public void debugTrees() throws Exception {
        //String pathTree = "/run/media/giuseppeporcaro/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/mrbs/crawl-mrbs-60min/doms/state284.html";
        String pathTree = "/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/testDOMA.html";
        Tree treeA = TreeFactory.createTree(pathTree,typeTree);

        ManageTreeRepresentation manager = new ManageTreeRepresentation();

        TreeRepresentation kelpTreeA = popolateTree(treeA);

        //printTreeDepthFirst(kelpTreeA.getRoot(),1);
        //mrbs crawl-mrbs-60min state284 834

        System.out.println("Altezza: "+manager.getTreeHeight(kelpTreeA.getRoot(),1));
        System.out.println("NUmero nodi: "+manager.getNumNodes(kelpTreeA.getRoot()));
        System.out.println("Average branching factor: "+manager.getAverageBranchingFactor(kelpTreeA.getRoot(),manager.getNumNodes(kelpTreeA.getRoot())));
        System.out.println("Degree: "+manager.getTreeDegree(kelpTreeA.getRoot()));

    }
}
