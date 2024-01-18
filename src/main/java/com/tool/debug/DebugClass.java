package com.tool.debug;

import com.tool.NormalizationKernels.NormalizationKernel;
import com.tool.NormalizationKernels.NormalizationPartialTreeKernel;
import com.tool.NormalizationKernels.NormalizationSubSetTreeKernel;
import com.tool.NormalizationKernels.NormalizationSubTreeKernel;
import com.tool.SimilarityTool;
import com.tool.Trees.Tree;
import com.tool.Trees.TreeFactory;
import com.tool.Utils;
import com.tool.data.dom.io.WebpageReader;
import com.tool.experiments.data.AnnotatedDataset;
import com.tool.data.dom.DomRepresentation;
import com.tool.representations.ManageTreeRepresentation;
import com.tool.similarity.*;
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
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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


    public void debugAttributeSimilarityVariants() throws Exception {
        Map<String, Float> weights = new HashMap<>();
        weights.put("class",0.4f);
        weights.put("style",0.1f);
        weights.put("src",0.1f);
        weights.put("title",0.2f);
        weights.put("href",0.1f);
        Map<String,Map<String,Float>> mapOfChoosenWeights = createWeights();

        System.out.println("w: "+mapOfChoosenWeights.get("default").values());

        String app = "pagekit";
        String dom1 = app+"/crawl-"+app+"-60min/doms/state238.html";
        String dom2 = app+"/crawl-"+app+"-60min/doms/state889.html";
        String folderPath = "/run/media/giuseppeporcaro/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/"; //"/Volumes/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/"

        //String domA = "/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/testDOMA.html";
        //String domB = "/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/testDOMB.html";
        String domA = folderPath+dom1;
        String domB = folderPath+dom2;

        String domString1 = Utils.getTestDOM(folderPath+dom1);
        String domString2 = Utils.getTestDOM(folderPath+dom2);

        System.out.println(domString1);


        SimilarityTool toolSimAttribute = new SimilarityTool(new AttributeSimilarity(),0.4f,0.1f,1,0.05f,null);
        SimilarityTool toolWeightedSimAttr = new SimilarityTool(new WeightedAttributeSimilarity(weights,0.1f),0.4f,0.1f,1,0.05f,null);
        SimilarityTool toolSimAttrIDVariant = new SimilarityTool(new AttributeSimliarityDifferentIDVariant(),0.4f,0.1f,1,0.05f,null);
        SimilarityTool toolSimWeightRefinedSimAttr = new SimilarityTool(new WeightedRefinedAttributeSimilarity(weights,0.1f),0.4f,0.1f,1,0.05f,null);
        SimilarityTool toolMultipleWeightRefSimAttr = new SimilarityTool(new MultipleWeightedRefinedAttributeSimilarity(mapOfChoosenWeights,0.1f),0.4f,0.1f,1,0.05f,null);

        float kernel = toolSimAttribute.computeKernelNormalized(domA,domB,"all");
        float weightedKernel = toolWeightedSimAttr.computeKernelNormalized(domA,domB,"all");
        float kernelIDVariant = toolSimAttrIDVariant.computeKernelNormalized(domA,domB,"all");
        float weightedRefinedKernel = toolSimWeightRefinedSimAttr.computeKernelNormalized(domA,domB,"all");
        float multipleWeightsKernel = toolMultipleWeightRefSimAttr.computeKernelNormalized(domA,domB,"all");

        float kernel2 = toolSimAttribute.computeKernelNormalizedByDOM(domString1, domString2,"all");
        float weightedKernel2 = toolWeightedSimAttr.computeKernelNormalizedByDOM(domString1, domString2,"all");
        float weightedRefinedKernel2 = toolSimWeightRefinedSimAttr.computeKernelNormalizedByDOM(domString1, domString2,"all");

        System.out.println("Kernel: "+kernel+"\nWeighted: "+weightedKernel+"\nWeighted Refined kernel: "+weightedRefinedKernel);
        System.out.println("Kernel2: "+kernel2+"\nWeighted2: "+weightedKernel2+"\nWeighted Refined kernel2: "+weightedRefinedKernel2);
        //System.out.println("Weighted Refined kernel: "+weightedRefinedKernel);
    }



    private static Map<String,Map<String,Float>> createWeights() {
        Map<String,Map<String,Float>> mapOfChoosenWeights = new HashMap<>();

        Map<String, Float> weightsDefault = new HashMap<>();
        weightsDefault.put("class",0.5f);
        weightsDefault.put("style",0.2f);
        weightsDefault.put("title",0.2f);

        Map<String, Float> weightAnchor = new HashMap<>();
        weightAnchor.put("href",0.5f);
        weightAnchor.put("class",0.2f);
        weightAnchor.put("target",0.1f);
        weightAnchor.put("type",0.1f);

        Map<String, Float> weightInput = new HashMap<>();
        weightInput.put("name",0.4f);
        weightInput.put("class",0.2f);
        weightInput.put("type",0.1f);
        weightInput.put("placeholder",0.2f);

        Map<String, Float> weightImages = new HashMap<>();
        weightImages.put("src",0.4f);
        weightImages.put("class",0.3f);
        weightImages.put("title",0.1f);
        weightImages.put("alt",0.1f);

        Map<String, Float> weightForm = new HashMap<>();
        weightForm.put("action",0.5f);
        weightForm.put("class",0.2f);
        weightForm.put("name",0.1f);
        weightForm.put("method",0.1f);

        mapOfChoosenWeights.put("default",weightsDefault);
        mapOfChoosenWeights.put("a",weightAnchor);
        mapOfChoosenWeights.put("input",weightInput);
        mapOfChoosenWeights.put("img",weightImages);
        mapOfChoosenWeights.put("form",weightForm);

        return mapOfChoosenWeights;

    }


    public void debugStarRepresentation() throws IOException {
        System.out.println("Debug representation...\n");

        Example first  = new SimpleExample(),
                second = new SimpleExample();

        String pathToGsCrawls  = "/run/media/giuseppeporcaro/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/";
        String pathToDsDataset = "data-input/DS.db";

        AnnotatedDataset dataset = new AnnotatedDataset(pathToDsDataset,pathToGsCrawls);
        WebpageReader wpReader = new WebpageReader(WebpageReader.REPRESENT_DOM_AS_IS);

        File file1 = dataset.getHtmlFile("addressbook", "crawl-addressbook-60min", "index");
        File file2 = dataset.getHtmlFile("addressbook", "crawl-addressbook-60min", "state5");
        //index - state33 -> kernel diversi con più esecuzioni
        //

        DomRepresentation page1 = wpReader.getDOMRepresentationFromFile(file1);
        DomRepresentation page2 = wpReader.getDOMRepresentationFromFile(file2);
        first.addRepresentation("DOM-TREE", page1);
        second.addRepresentation("DOM-TREE", page2);
        /*
        SmoothedPartialTreeKernel smptk = new SmoothedPartialTreeKernel(0.4f,0.1f,1f,0.01f,new AttributeSimilarity(),"DOM-TREE");
        it.uniroma2.sag.kelp.kernel.standard.NormalizationKernel nsmptk = new it.uniroma2.sag.kelp.kernel.standard.NormalizationKernel(smptk);
        float norm = nsmptk.innerProduct(first,second);
        System.out.println("SMOOTHED PARTIAL TREE KERNEL (mu=0.1,lambda=0.1): "+norm);*/

        for(int i = 0; i < 10; i++){
            PartialTreeKernel ptk2 = new PartialTreeKernel("DOM-TREE");
            //ptk1.disableCache();
            ptk2.setMu(0.1F);
            it.uniroma2.sag.kelp.kernel.standard.NormalizationKernel nptk2 = new it.uniroma2.sag.kelp.kernel.standard.NormalizationKernel(ptk2);
            float norm = nptk2.innerProduct(first,second);
            System.out.println("NORMALIZED PARTIAL TREE KERNEL (mu=0.01): "+norm);
        }


        System.out.println(first.getRepresentation("DOM-TREE").getTextFromData());
        System.out.println(second.getRepresentation("DOM-TREE").getTextFromData());

    }

    private void createTmpFile(String name, String dom){
        try {
            FileWriter myWriter = new FileWriter("src/main/resources/tmp/"+name);
            myWriter.write(dom);
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DebugClass(){
        StructureElementSimilarityI jaccardSimilarity = new AttributeSimilarity();
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
