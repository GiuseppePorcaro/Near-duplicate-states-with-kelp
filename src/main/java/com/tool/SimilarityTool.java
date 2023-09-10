package com.tool;

import com.tool.Trees.TreeFactory;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.kernel.DirectKernel;
import it.uniroma2.sag.kelp.kernel.cache.DynamicIndexSquaredNormCache;
import it.uniroma2.sag.kelp.kernel.tree.SmoothedPartialTreeKernel;
import it.uniroma2.sag.kelp.kernel.tree.deltamatrix.DynamicDeltaMatrix;

import static com.tool.representations.ManageTreeRepresentation.popolateTree;


public class SimilarityTool {

    private float LAMBDA = 0.4f;
    private float MU = 0.4f;
    private float terminalFactor = 1;
    private float similarityThreshold = 0.01f;
    private String representationIdentifier = null; //????
    private SmoothedPartialTreeKernel kernel;
    private NormalizationKernel kernelNormalized;

    public SimilarityTool(StructureElementSimilarityI similarity, float LAMBDA, float MU, float terminalFactor, float similarityThreshold, String representationIdentifier) {
        this.LAMBDA = LAMBDA;
        this.MU = MU;
        this.terminalFactor = terminalFactor;
        this.similarityThreshold = similarityThreshold;
        this.representationIdentifier = representationIdentifier;



        this.kernel = new SmoothedPartialTreeKernel(LAMBDA,MU,terminalFactor,similarityThreshold,similarity,representationIdentifier);
        this.kernelNormalized = new NormalizationKernel(kernel);

    }

    public float computeKernelNotNormalized(String pathHtml1, String pathHtml2,String treeType) throws Exception {
        TreeRepresentation firstTree = popolateTree(TreeFactory.createTree(pathHtml1,treeType));
        TreeRepresentation secondTree = popolateTree(TreeFactory.createTree(pathHtml2,treeType));

        kernel.setDeltaMatrix(new DynamicDeltaMatrix());

        return kernel.kernelComputation(firstTree,secondTree);
    }

    public float computeKernelNormalized(String pathHtml1, String pathHtml2, String treeType) throws Exception {
        TreeRepresentation firstTree = popolateTree(TreeFactory.createTree(pathHtml1,treeType));
        TreeRepresentation secondTree = popolateTree(TreeFactory.createTree(pathHtml2,treeType));

        return kernelNormalized.kernelComputation(firstTree,secondTree);
    }

    public DirectKernel<TreeRepresentation> getKernel() {
        return kernel;
    }

    public void setKernel(SmoothedPartialTreeKernel kernel) {
        this.kernel = kernel;
    }

    public NormalizationKernel getKernelNormalized() {
        return kernelNormalized;
    }

    public void setKernelNormalized(NormalizationKernel kernelNormalized) {
        this.kernelNormalized = kernelNormalized;
    }

}
