package com.tool.similarity;

import com.tool.Utils;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/*
 *
 * Calculate the similarity between two nodes built ad hoc to represent an HTML DOM node, using the Jaccard similarity index on
 * set built with nodes children.
 * If the nodes have different tags, they are different (similarity 0).
 * If they have the same tag but no children, then similarity is 1.
 * If they have the same tag and they have children, Jaccard similarity is calculated on the sets formed by the children of the nodes.
 *
 * */

public class ChildrenBasedJaccardSimilarity implements StructureElementSimilarityI {
    @Override
    public float sim(StructureElement sx, StructureElement sd) {

        String tagSx = Utils.getTag(sx.getTextFromData());
        String tagSd = Utils.getTag(sd.getTextFromData());

        /*Se i tag sono diversi -> diverse funzionalità*/
        if(!tagSx.equalsIgnoreCase(tagSd.toLowerCase())){
            return 0f;
        }

        //System.out.println("Tags: "+tagSx+" - "+tagSd);

        ArrayList<TreeNode> childrenSx = (ArrayList<TreeNode>)  sx.getAdditionalInformation("children");
        ArrayList<TreeNode> childrenSd = (ArrayList<TreeNode>)  sd.getAdditionalInformation("children");

        Set<String> childrenSxSet = getChildrenSet(childrenSx);
        Set<String> childrenSdSet = getChildrenSet(childrenSd);

        /*Stesso tag ma diversi attributi -> nodi identici*/
        if(childrenSxSet.size() == 0 && childrenSdSet.size() == 0){
            //System.out.println("Sim: 1\n");
            return 1f;
        }

        Set<String> union = new HashSet<>(childrenSxSet);
        union.addAll(childrenSdSet);
        int intersectionCardinality = (childrenSxSet.size()+childrenSdSet.size()) - union.size();

        float sim = 1f * intersectionCardinality/union.size();

        //System.out.println("Children: "+childrenSxSet+" -- "+childrenSdSet+"\nIntersection: "+intersectionCardinality+" - union: "+union.size());
        //System.out.println("Sim: "+sim+"\n");

        return sim;
    }

    private Set<String> getChildrenSet(ArrayList<TreeNode> children){
        Set<String> childrenSet = new HashSet<>();

        for(TreeNode n: children){
            childrenSet.add(n.getContent().getTextFromData());
        }

        return childrenSet;
    }
}
