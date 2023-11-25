package com.tool.debug;

import com.tool.NormalizationKernels.NormalizationKernel;
import com.tool.NormalizationKernels.NormalizationPartialTreeKernel;
import com.tool.NormalizationKernels.NormalizationSubSetTreeKernel;
import com.tool.NormalizationKernels.NormalizationSubTreeKernel;
import com.tool.SimilarityTool;
import com.tool.Trees.Tree;
import com.tool.Trees.TreeFactory;
import com.tool.data.dom.io.WebpageReader;
import com.tool.experiments.data.AnnotatedDataset;
import com.tool.data.dom.DomRepresentation;
import com.tool.representations.ManageTreeRepresentation;
import com.tool.similarity.AllAttributesDiceSorensenSimilarity;
import com.tool.similarity.AllAttributesJaccardSimilarity;
import com.tool.similarity.ChildrenBasedJaccardSimilarity;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.example.SimpleExample;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.ExactMatchingStructureElementSimilarity;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.LexicalStructureElementSimilarity;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.kernel.tree.PartialTreeKernel;
import it.uniroma2.sag.kelp.kernel.tree.SmoothedPartialTreeKernel;
import it.uniroma2.sag.kelp.kernel.tree.SubSetTreeKernel;
import it.uniroma2.sag.kelp.kernel.tree.SubTreeKernel;

import java.io.File;
import java.io.IOException;

import static com.tool.representations.ManageTreeRepresentation.*;

public class DebugClass {

    SmoothedPartialTreeKernel kernelAttributeNotNormalized;
    SmoothedPartialTreeKernel kernelExactMatching;
    NormalizationKernel kernelAttributeNormalized;
    SmoothedPartialTreeKernel kernelStandardNotNormalized;
    NormalizationKernel kernelStandardNormalized;
    NormalizationKernel kernelExactMAtchingNormalized;
    private String typeTree = "all";
    private SimilarityTool similarityTool;
    PartialTreeKernel partialTreeKernel = new PartialTreeKernel(0.4f,0.4f,1,typeTree);
    NormalizationKernel kernelChildBasedNormalized;


    public void debugStarRepresentation() throws IOException {
        System.out.println("Debug representation...\n");

        Example first  = new SimpleExample(),
                second = new SimpleExample();

        String pathToGsCrawls  = "/run/media/giuseppeporcaro/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/";
        String pathToDsDataset = "data-input/DS.db";

        AnnotatedDataset dataset = new AnnotatedDataset(pathToDsDataset,pathToGsCrawls);
        WebpageReader wpReader = new WebpageReader(WebpageReader.REPRESENT_DOM_AS_IS);

        File file1 = dataset.getHtmlFile("addressbook", "crawl-addressbook-60min", "index");
        File file2 = dataset.getHtmlFile("addressbook", "crawl-addressbook-60min", "state141");

        DomRepresentation page1 = wpReader.getDOMRepresentationFromFile(file1);
        DomRepresentation page2 = wpReader.getDOMRepresentationFromFile(file2);
        first.addRepresentation("DOM-TREE", page1);
        second.addRepresentation("DOM-TREE", page2);


        /*
        SmoothedPartialTreeKernel smptk = new SmoothedPartialTreeKernel(0.4f,0.1f,1f,0.01f,new AllAttributesJaccardSimilarity(),"DOM-TREE");
        it.uniroma2.sag.kelp.kernel.standard.NormalizationKernel nsmptk = new it.uniroma2.sag.kelp.kernel.standard.NormalizationKernel(smptk);
        float norm = nsmptk.innerProduct(first,second);
        System.out.println("SMOOTHED PARTIAL TREE KERNEL (mu=0.1,lambda=0.1): "+norm);
         */
        PartialTreeKernel ptk2 = new PartialTreeKernel("DOM-TREE");
        ptk2.setMu(0.1F);
        it.uniroma2.sag.kelp.kernel.standard.NormalizationKernel nptk2 = new it.uniroma2.sag.kelp.kernel.standard.NormalizationKernel(ptk2);
        float kernel = nptk2.innerProduct(first,second);


        System.out.println("PARTIAL TREE KERNEL (mu=0.1) 2: "+kernel);

        PartialTreeKernel ptk3 = new PartialTreeKernel("DOM-TREE");
        ptk3.setMu(0.1F);
        it.uniroma2.sag.kelp.kernel.standard.NormalizationKernel nptk3 = new it.uniroma2.sag.kelp.kernel.standard.NormalizationKernel(ptk3);
        kernel = nptk3.innerProduct(first,second);

        System.out.println("PARTIAL TREE KERNEL (mu=0.1) 3: "+kernel);

        PartialTreeKernel ptk4 = new PartialTreeKernel("DOM-TREE");
        ptk4.setMu(0.1F);
        it.uniroma2.sag.kelp.kernel.standard.NormalizationKernel nptk4 = new it.uniroma2.sag.kelp.kernel.standard.NormalizationKernel(ptk4);
        kernel = nptk4.innerProduct(first,second);

        System.out.println("PARTIAL TREE KERNEL (mu=0.1) 4: "+kernel);

        System.out.println(first.getRepresentation("DOM-TREE").getTextFromData());
        System.out.println(second.getRepresentation("DOM-TREE").getTextFromData());

    }

    public DebugClass(){
        StructureElementSimilarityI jaccardSimilarity = new AllAttributesJaccardSimilarity();
        StructureElementSimilarityI diceSorensen = new AllAttributesDiceSorensenSimilarity();
        StructureElementSimilarityI childrenBasedJaccardSimilarity = new ChildrenBasedJaccardSimilarity();
        StructureElementSimilarityI lexicalStructureElementSimilarity = new LexicalStructureElementSimilarity();
        StructureElementSimilarityI exactMatchingStructureElementSimilarity = new ExactMatchingStructureElementSimilarity();
        similarityTool = new SimilarityTool(jaccardSimilarity,0.4f,0.8f,1f,0.01f,typeTree);

        kernelAttributeNotNormalized = new SmoothedPartialTreeKernel(0.4f,0.4f,1f,0.01f,jaccardSimilarity,typeTree);
        kernelStandardNotNormalized = new SmoothedPartialTreeKernel(0.4f,0.4f,1,0.01f,lexicalStructureElementSimilarity,typeTree);
        kernelExactMatching = new SmoothedPartialTreeKernel(0.4f,0.4f,1,0.01f,exactMatchingStructureElementSimilarity,typeTree);

        kernelAttributeNormalized = new NormalizationKernel(kernelAttributeNotNormalized);
        kernelStandardNormalized = new NormalizationKernel(kernelStandardNotNormalized);
        kernelExactMAtchingNormalized = new NormalizationKernel(kernelExactMatching);

        kernelChildBasedNormalized = new NormalizationKernel(new SmoothedPartialTreeKernel(0.4f,0.1f,1,0.01f,childrenBasedJaccardSimilarity,typeTree));
    }

    public void debugKernels() throws Exception {
        SubTreeKernel subTreeKernel = new SubTreeKernel();
        SubSetTreeKernel subSetTreeKernel = new SubSetTreeKernel();
        PartialTreeKernel partialTreeKernel1 = new PartialTreeKernel();

        String dom1 = "/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/testDOMA.html";
        String dom2 = "/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/testChildrenA.html";

        TreeRepresentation firstTree = popolateTree(TreeFactory.createTree(dom1,typeTree));
        TreeRepresentation secondTree = popolateTree(TreeFactory.createTree(dom2,typeTree));

        NormalizationSubTreeKernel subTreeKernelNormalizationKernel = new NormalizationSubTreeKernel(subTreeKernel);
        NormalizationSubSetTreeKernel subSetTreeKernelNormalizationKernel = new NormalizationSubSetTreeKernel(subSetTreeKernel);
        NormalizationPartialTreeKernel partialTreeKernel1NormalizationKernel = new NormalizationPartialTreeKernel(partialTreeKernel1);

        float subTreeKernelResult = subTreeKernelNormalizationKernel.kernelComputation(firstTree,secondTree);
        float subSetTreeKernelResult = subSetTreeKernelNormalizationKernel.kernelComputation(firstTree,secondTree);
        float partialTreeKernelResult = partialTreeKernel1NormalizationKernel.kernelComputation(firstTree,secondTree);

        System.out.println("SubTreeKernel: "+subTreeKernelResult+"\nSubSetTreeKernel: "+subSetTreeKernelResult+"\nPartialTreeKernel: "+partialTreeKernelResult);


    }






    public void start() throws Exception {

        ManageTreeRepresentation manager = new ManageTreeRepresentation();

        String dom1 = "/Users/giuseppeporcaro/Desktop/Libri_università/Magistrale/Tesi magistrale/Web Test Generation/Tool Web Testing/Near-duplicate-states-with-kelp/src/main/resources/testDOMA.html";
        String dom2 = "/Users/giuseppeporcaro/Desktop/Libri_università/Magistrale/Tesi magistrale/Web Test Generation/Tool Web Testing/Near-duplicate-states-with-kelp/src/main/resources/testDOMB.html";
        TreeRepresentation firstTree = popolateTree(TreeFactory.createTree(dom1,typeTree));
        TreeRepresentation secondTree = popolateTree(TreeFactory.createTree(dom2,typeTree));

        //String dom1 = "mrbs/crawl-mrbs-60min/doms/state23.html";
        //String dom2 = "mrbs/crawl-mrbs-60min/doms/state253.html";
        String folderPath = "/run/media/giuseppeporcaro/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/"; //"/Volumes/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/"
        //String folderPath = "";

        //float kernelAttrNotNormalized = kernelAttributeNotNormalized.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);
        //float kernelStandarNotNormalized = kernelStandardNotNormalized.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);
        float kernelAttrNormalized = similarityTool.computeKernelNormalized(dom1,dom2,typeTree );
        float kernelLexicalNormalized = kernelStandardNormalized.kernelComputation(firstTree,secondTree);
        float kernelExactMatch = kernelExactMAtchingNormalized.kernelComputation(firstTree,secondTree);
        //float partialTreeKernelNorm = partialTreeKernelNormalized.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);
        //float childBasedKernel = kernelChildBasedNormalized.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);


        //System.out.println("Kernel normalized: ("+kernelAttributes+","+kernelChildren+")\nKernel not normalized: ("+kernelAttri+","+kernelChildr+")");

        System.out.println("################################################################################");
        //System.out.println("Kernel not normalized: \n\tOn attributes: "+kernelAttrNotNormalized+"\n\tStandard: "+kernelStandarNotNormalized);
        System.out.println("Kernel normalized: \n\tOn attributes: "+kernelAttrNormalized+"\n\tStandard: "+kernelLexicalNormalized);
        System.out.println("\tkernelExactMatch: "+kernelExactMatch);
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
