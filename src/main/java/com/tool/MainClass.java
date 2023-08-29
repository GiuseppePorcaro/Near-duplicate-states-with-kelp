package com.tool;

import com.tool.Trees.TreeFactory;
import com.tool.similarity.AllAttributesJaccardSimilarity;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;

public class MainClass {

    public static void main(String args[]) throws Exception {

        SimilarityTool similarityTool = new SimilarityTool(new AllAttributesJaccardSimilarity(),0.4f,0.4f,1,0.01f,null);

        System.out.println(similarityTool.computeKernelNormalized("src/main/resources/testChildrenA.html","src/main/resources/testChildrenB.html","noScript"));

        /*DatasetManager datasetManager = new DatasetManager();
        datasetManager.main();*/

    }


}
