package com.tool;

import com.tool.Trees.TreeFactory;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.kernel.DirectKernel;
import it.uniroma2.sag.kelp.kernel.tree.SmoothedPartialTreeKernel;

import static com.tool.ManageTreeRepresentation.popolateTree;


public class SimilarityTool {

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

}
