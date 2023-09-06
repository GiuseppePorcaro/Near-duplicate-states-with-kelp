package com.tool.debug;

import com.tool.NormalizationKernel;
import com.tool.Trees.Tree;
import com.tool.Trees.TreeFactory;
import com.tool.similarity.AllAttributesDiceSorensenSimilarity;
import com.tool.similarity.AllAttributesJaccardSimilarity;
import com.tool.similarity.ChildrenBasedJaccardSimilarity;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.LexicalStructureElementSimilarity;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.kernel.DirectKernel;
import it.uniroma2.sag.kelp.kernel.tree.PartialTreeKernel;
import it.uniroma2.sag.kelp.kernel.tree.SmoothedPartialTreeKernel;

import static com.tool.Utils.popolateTree;

public class DebugClass {

    DirectKernel<TreeRepresentation> kernelAttributeNotNormalized;
    NormalizationKernel<TreeRepresentation> kernelAttributeNormalized;

    DirectKernel<TreeRepresentation> kernelStandardNotNormalized;
    NormalizationKernel<TreeRepresentation> kernelStandardNormalized;

    PartialTreeKernel partialTreeKernel = new PartialTreeKernel(0.4f,0.4f,1,null);
    NormalizationKernel<TreeRepresentation> kernelChildBasedNormalized;
    public DebugClass(){
        StructureElementSimilarityI jaccardSimilarity = new AllAttributesJaccardSimilarity();
        StructureElementSimilarityI diceSorensen = new AllAttributesDiceSorensenSimilarity();
        StructureElementSimilarityI childrenBasedJaccardSimilarity = new ChildrenBasedJaccardSimilarity();
        StructureElementSimilarityI kelpStandardSimilarity = new LexicalStructureElementSimilarity();

        kernelAttributeNotNormalized = new SmoothedPartialTreeKernel(0.4f,0.4f,1,0.01f,jaccardSimilarity,null);
        kernelStandardNotNormalized = new SmoothedPartialTreeKernel(0.4f,0.4f,1,0.01f,kelpStandardSimilarity,null);

        kernelAttributeNormalized = new NormalizationKernel<>(kernelAttributeNotNormalized);
        kernelStandardNormalized = new NormalizationKernel<>(kernelStandardNotNormalized);

        kernelChildBasedNormalized = new NormalizationKernel<>(new SmoothedPartialTreeKernel(0.4f,0.4f,1,0.01f,childrenBasedJaccardSimilarity,null));
    }

    //Liste di oggetti omogenei ai quali è stato dato un valore di similarità basso:
    /*
    * "addressbook/crawl-addressbook-60min/doms/state176.html"
    * "addressbook/crawl-addressbook-60min/doms/state400.html"
    * */
    public void start() throws Exception {

        String dom1 = "addressbook/crawl-addressbook-60min/doms/state176.html";
        String dom2 = "addressbook/crawl-addressbook-60min/doms/state538.html";

        Tree treeANoScript = TreeFactory.createTree("/Volumes/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/"+dom1,"treeForCrawl");
        Tree treeBNoScript = TreeFactory.createTree("/Volumes/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/"+dom2,"treeForCrawl");



        //System.out.println(treeANoScript.getParsedDOM());
        //System.out.println("################################################################################");
        //System.out.println(treeBNoScript.getParsedDOM());

        if(treeANoScript == null || treeBNoScript == null){
            System.out.println("Type of tree not defined");
            return;
        }
        TreeRepresentation kelpTreeANoScript = popolateTree(treeANoScript);
        TreeRepresentation kelpTreeBNoScript = popolateTree(treeBNoScript);

        NormalizationKernel partialTreeKernelNormalized = new NormalizationKernel(partialTreeKernel);

        float kernelAttrNotNormalized = kernelAttributeNotNormalized.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);
        float kernelStandarNotNormalized = kernelStandardNotNormalized.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);
        float kernelAttrNormalized = kernelAttributeNormalized.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);
        float kernelStandarNormalized = kernelStandardNormalized.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);
        float partialTreeKernelNorm = partialTreeKernelNormalized.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);
        float childBasedKernel = kernelChildBasedNormalized.kernelComputation(kelpTreeANoScript,kelpTreeBNoScript);


        //System.out.println("Kernel normalized: ("+kernelAttributes+","+kernelChildren+")\nKernel not normalized: ("+kernelAttri+","+kernelChildr+")");

        System.out.println("################################################################################");
        System.out.println("Kernel not normalized: \n\tOn attributes: "+kernelAttrNotNormalized+"\n\tStandard: "+kernelStandarNotNormalized);
        System.out.println("Kernel normalized: \n\tOn attributes: "+kernelAttrNormalized+"\n\tStandard: "+kernelStandarNormalized);
        System.out.println("\tPartialTreeKenrel: "+partialTreeKernelNorm);
        System.out.println("\tKernel childBased: "+childBasedKernel);
    }
}
