package com.tool;

import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.kernel.tree.SmoothedPartialTreeKernel;
import it.uniroma2.sag.kelp.kernel.tree.deltamatrix.DeltaMatrix;

public class NormalizationKernel {

    private SmoothedPartialTreeKernel baseKernel;

    public NormalizationKernel(SmoothedPartialTreeKernel baseKernel){
        this.baseKernel = baseKernel;
    }

    public float kernelComputation(TreeRepresentation treeA, TreeRepresentation treeB){
        Float kernelA = baseKernel.kernelComputation(treeA,treeA);
        if(kernelA==0){
            return 0;
        }
        Float kernelB = baseKernel.kernelComputation(treeB,treeB);
        if(kernelB==0){
            return 0;
        }
        float kernelAB= baseKernel.kernelComputation(treeA, treeB);
        return (float)(kernelAB/(Math.sqrt(kernelA*kernelB)));
    }

    public void setDeltaMatrix(DeltaMatrix deltaMatrix){
        baseKernel.setDeltaMatrix(deltaMatrix);
    }
}
