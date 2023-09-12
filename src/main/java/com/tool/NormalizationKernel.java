package com.tool;

import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.kernel.tree.SmoothedPartialTreeKernel;
import it.uniroma2.sag.kelp.kernel.tree.deltamatrix.DeltaMatrix;
import it.uniroma2.sag.kelp.kernel.tree.deltamatrix.DynamicDeltaMatrix;

public class NormalizationKernel {

    private SmoothedPartialTreeKernel baseKernel;

    public NormalizationKernel(SmoothedPartialTreeKernel baseKernel){
        this.baseKernel = baseKernel;
    }

    public float kernelComputation(TreeRepresentation treeA, TreeRepresentation treeB){
        baseKernel.setDeltaMatrix(new DynamicDeltaMatrix());
        Float kernelA = baseKernel.kernelComputation(treeA,treeA);
        if(kernelA==0){
            return 0;
        }
        baseKernel.setDeltaMatrix(new DynamicDeltaMatrix());
        Float kernelB = baseKernel.kernelComputation(treeB,treeB);
        if(kernelB==0){
            return 0;
        }
        baseKernel.setDeltaMatrix(new DynamicDeltaMatrix());
        float kernelAB= baseKernel.kernelComputation(treeA, treeB);

        System.out.println("KernelA: "+kernelA+"\nKernelB: "+kernelB+"\nKernelAB: "+kernelAB);
        return (float)(kernelAB/(Math.sqrt(kernelA*kernelB)));
    }

    public void setDeltaMatrix(DeltaMatrix deltaMatrix){
        baseKernel.setDeltaMatrix(deltaMatrix);
    }
}
