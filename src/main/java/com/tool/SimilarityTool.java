package com.tool;

import com.tool.Trees.TreeFactory;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import it.uniroma2.sag.kelp.kernel.DirectKernel;
import it.uniroma2.sag.kelp.kernel.cache.DynamicIndexSquaredNormCache;
import it.uniroma2.sag.kelp.kernel.tree.SmoothedPartialTreeKernel;
import it.uniroma2.sag.kelp.kernel.tree.deltamatrix.DynamicDeltaMatrix;

import static com.tool.Utils.getDisplayStyle;
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

    /*
    * METODO DI PROVA
    *
    * Questo metodo controlla per ogni coppia di nodi se ci possono essere coppie che hanno diverso valore
    * per l'attributo style:display. Se si, allora ritorna 0.0 come similarità.
    *
    * Motivo ti tale metodo è il fatto che ci sono molte pagine identiche, ma che differiscono solo per il fatto che
    * hanno uno stesso tag con due visibilità diverse. Quindi il kenrel è alto in quanto cambia solo questo attributo,
    * ma la funzionalità è classificata come diversa.
    * */
    public static float computePreKernelSimilarity(TreeRepresentation treeA, TreeRepresentation treeB){
        float sim = -1;
        for(TreeNode nodeA: treeA.getAllNodes()){
            StructureElement seA = nodeA.getContent();
            String attrStyleSx = getDisplayStyle(seA.getTextFromData());
            String tagSx = Utils.getTag(seA.getTextFromData());
            for(TreeNode nodeB: treeB.getAllNodes()){
                StructureElement seB = nodeB.getContent();
                String attrStyleSd = getDisplayStyle(seB.getTextFromData());
                String tagSd = Utils.getTag(seB.getTextFromData());
                if(tagSx.equalsIgnoreCase(tagSd)){
                    if(attrStyleSd != null && attrStyleSx != null){
                        if((attrStyleSd.contains("hidden") && attrStyleSx.contains("none"))||attrStyleSd.contains("none") && attrStyleSx.contains("hidden")){
                            //System.out.println(attrStyleSd+" --- "+attrStyleSx +" | "+attrStyleSd.contains("hidden")+" --- "+attrStyleSx.contains("none"));
                            sim = 0;
                        }
                    }
                }
            }
        }
        return sim;
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
