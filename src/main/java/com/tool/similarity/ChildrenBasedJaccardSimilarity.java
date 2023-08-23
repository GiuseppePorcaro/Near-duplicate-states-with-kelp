package com.tool.similarity;

import com.sun.source.tree.Tree;
import com.tool.Utils;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.tool.Utils.getId;

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
