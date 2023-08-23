package com.tool.similarity;

import com.tool.MyStructureElement;
import com.tool.Utils;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.tool.Utils.*;

/*
*
* Calculate the similarity between two nodes built ad hoc to represent an HTML DOM node, using the Jaccard similarity index.
* If the nodes have different tags, they are different (similarity 0).
* If they have the same id, then the nodes are identical (similarity 1).
* If they have the same tag but no attributes, then similarity is 1.
* If they have the same tag but one of the two has no attributes, similarity is 0.
* If they have the same tag and they have attributes, Jaccard similarity is calculated on the sets formed by the attributes of the nodes.
*
* Additionally, it's possible to also consider the children of the nodes.
* In such cases, Jaccard is also calculated on sets composed of the children of the nodes,
* and the arithmetic mean is taken between the two calculated similarities.
*
* */

public class AllAttributesJaccardSimilarity implements StructureElementSimilarityI {

    @Override
    public float sim(StructureElement sx, StructureElement sd) {

        String tagSx = Utils.getTag(sx.getTextFromData());
        String tagSd = Utils.getTag(sd.getTextFromData());

        System.out.println("Tags: "+tagSx+" - "+tagSd);

        if(!tagSx.equalsIgnoreCase(tagSd.toLowerCase())){
            System.out.println("Sim: 0\n");
            return 0f;
        }

        Set<String> attributesSx = getAttributes(sx.getTextFromData());
        Set<String> attributeSd = getAttributes(sd.getTextFromData());
        String idSx = getId(attributesSx);
        String idSd = getId(attributeSd);

        System.out.println("Ids: "+idSx+" - "+idSd);

        if( idSx != null && idSd != null ){
            if(idSx.equals(idSd)){
                System.out.println("Sim: 1\n");
                return 1f;
            }
        }

        int sxSize = attributesSx.size();
        int sdSize = attributeSd.size();

        if(sxSize == 0 && sdSize == 0){
            System.out.println("Sim: 1\n");
            return 1f;
        }

        if(sxSize == 0 || sdSize == 0){
            System.out.println("Sim: 0\n");
            return 0f;
        }

        Set<String> union = new HashSet<>(attributesSx);
        union.addAll(attributeSd);
        int intersectionCardinality = (sxSize+sdSize) - union.size();

        System.out.println("Attributes: "+attributesSx+" - "+attributeSd+" Sizes: "+attributesSx.size()+" - "+attributeSd.size()+ " - inters: "+intersectionCardinality+" - union: "+union.size());

        float sim = 1f * intersectionCardinality/union.size();
        //float sim = 1f * intersectionCardinality+1/union.size()+1; //Considero anche il tag nell'insieme

        /*Under testing*/
        boolean considerChildresSx = (boolean) sx.getAdditionalInformation("considerChildren");
        boolean considerChildresSd = (boolean) sd.getAdditionalInformation("considerChildren");
        if(considerChildresSx && considerChildresSd){
            ArrayList<TreeNode> childrenSx = (ArrayList<TreeNode>)  sx.getAdditionalInformation("children");
            ArrayList<TreeNode> childrenSd = (ArrayList<TreeNode>)  sd.getAdditionalInformation("children");

            float childrenSim = 0.0f;
            for(TreeNode t1: childrenSx){
                t1.getContent().addAdditionalInformation("considerChildren",false);
                for(TreeNode t2:childrenSd){
                    childrenSim = childrenSim + sim(t1.getContent(),t2.getContent());
                }
                t1.getContent().addAdditionalInformation("considerChildren",true);
            }

            childrenSim = childrenSim / (childrenSx.size() + childrenSd.size());
            sim = (sim + childrenSim )/ 2;
            System.out.println("Attributes: "+childrenSx+" - "+childrenSd+" Sizes: "+childrenSx.size()+" - "+childrenSd.size()+" - ChildrenSim: "+childrenSim);
        }

        System.out.println("Sim: "+sim+"\n");
        return sim;
    }

}
