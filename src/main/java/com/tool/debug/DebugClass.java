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
import it.uniroma2.sag.kelp.kernel.tree.SmoothedPartialTreeKernel;

import static com.tool.Utils.popolateTree;

public class DebugClass {

    DirectKernel<TreeRepresentation> kernelAttributeNotNormalized;
    NormalizationKernel<TreeRepresentation> kernelAttributeNormalized;

    DirectKernel<TreeRepresentation> kernelStandardNotNormalized;
    NormalizationKernel<TreeRepresentation> kernelStandardNormalized;

    public DebugClass(){
        StructureElementSimilarityI jaccardSimilarity = new AllAttributesJaccardSimilarity();
        StructureElementSimilarityI diceSorensen = new AllAttributesDiceSorensenSimilarity();
        StructureElementSimilarityI childrenBasedJaccardSimilarity = new ChildrenBasedJaccardSimilarity();
        StructureElementSimilarityI kelpStandardSimilarity = new LexicalStructureElementSimilarity();

        kernelAttributeNotNormalized = new SmoothedPartialTreeKernel(0.4f,0.4f,1,0.01f,jaccardSimilarity,null);
        kernelStandardNotNormalized = new SmoothedPartialTreeKernel(0.4f,0.4f,1,0.01f,kelpStandardSimilarity,null);

        kernelAttributeNormalized = new NormalizationKernel<>(kernelAttributeNotNormalized);
        kernelStandardNormalized = new NormalizationKernel<>(kernelStandardNotNormalized);
    }

    public void start() throws Exception {

        Tree treeANoScript = TreeFactory.createTree("/Volumes/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/addressbook/crawl-addressbook-60min/doms/state38.html","treeForCrawl");
        Tree treeBNoScript = TreeFactory.createTree("/Volumes/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/addressbook/crawl-addressbook-60min/doms/state697.html","treeForCrawl");


        System.out.println(treeANoScript.getParsedDOM());
        System.out.println("################################################################################");
        System.out.println(treeBNoScript.getParsedDOM());

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


        //System.out.println("Kernel normalized: ("+kernelAttributes+","+kernelChildren+")\nKernel not normalized: ("+kernelAttri+","+kernelChildr+")");

        System.out.println("################################################################################");
        System.out.println("Kernel not normalized: \n\tOn attributes: "+kernelAttrNotNormalized+"\n\tStandard: "+kernelStandarNotNormalized);
        System.out.println("Kernel normalized: \n\tOn attributes: "+kernelAttrNormalized+"\n\tStandard: "+kernelStandarNormalized);
    }
}
