package com.tool;

import it.uniroma2.sag.kelp.data.representation.Representation;
import it.uniroma2.sag.kelp.kernel.DirectKernel;

public class NormalizationKernel<T extends Representation> {

    private DirectKernel<T> baseKernel;

    public NormalizationKernel(DirectKernel<T> baseKernel){
        this.baseKernel = baseKernel;
    }

    public float kernelComputation(T treeA, T treeB){
        Float kernelA = baseKernel.kernelComputation(treeA,treeA);
        System.out.println(kernelA);
        if(kernelA==0){
            return 0;
        }
        Float kernelB = baseKernel.kernelComputation(treeB,treeB);
        System.out.println(kernelB);
        if(kernelB==0){
            return 0;
        }
        float kernelAB= baseKernel.kernelComputation(treeA, treeB);
        System.out.println(kernelAB);
        return (float)(kernelAB/(Math.sqrt(kernelA*kernelB)));
    }
}
