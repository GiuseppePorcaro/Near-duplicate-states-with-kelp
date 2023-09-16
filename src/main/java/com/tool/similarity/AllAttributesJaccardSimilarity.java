package com.tool.similarity;

import com.tool.Utils;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;
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
* */

public class AllAttributesJaccardSimilarity implements StructureElementSimilarityI {

    @Override
    public float sim(StructureElement sx, StructureElement sd) {

        String tagSx = Utils.getTag(sx.getTextFromData());
        String tagSd = Utils.getTag(sd.getTextFromData());

        //System.out.println("Tags: "+tagSx+" - "+tagSd);

        /*tag diversi -> nodi diversi*/
        if(!tagSx.equalsIgnoreCase(tagSd.toLowerCase())){
            //System.out.println("Sim: 0\n");
            return 0f;
        }

        Set<String> attributesSx = getAttributes(sx.getTextFromData());
        Set<String> attributeSd = getAttributes(sd.getTextFromData());
        String idSx = getId(attributesSx);
        String idSd = getId(attributeSd);

        //System.out.println("Ids: "+idSx+" - "+idSd);

        String attrStyleSx = getDisplayStyle(sx.getTextFromData());
        String attrStyleSd = getDisplayStyle(sd.getTextFromData());

        /*Stesso id -> stesso nodo*/
        if( idSx != null && idSd != null ){
            if(idSx.equals(idSd)){
                //System.out.println("Sim: 1\n");
                /*if(attrStyleSd != null && attrStyleSx != null){
                    if((attrStyleSd.contains("block;") && attrStyleSx.contains("none;"))||attrStyleSd.contains("none;") && attrStyleSx.contains("block;")){
                        System.out.println(attrStyleSd+" --- "+attrStyleSx +" | "+attrStyleSd.contains("block;")+" --- "+attrStyleSx.contains("none;"));
                        return 0.0f;
                    }
                }*/
                return 1f;
            }
        }

        int sxSize = attributesSx.size();
        int sdSize = attributeSd.size();

        if(sxSize == 0 && sdSize == 0){
            //System.out.println("Sim: 1\n");
            return 1f;
        }

        if(sxSize == 0 || sdSize == 0){
            //System.out.println("Sim: 0\n");
            return 0f;
        }

        Set<String> union = new HashSet<>(attributesSx);
        union.addAll(attributeSd);
        int intersectionCardinality = (sxSize+sdSize) - union.size();

        //System.out.println("Attributes: "+attributesSx+" - "+attributeSd+" Sizes: "+attributesSx.size()+" - "+attributeSd.size()+ " - inters: "+intersectionCardinality+" - union: "+union.size());

        float sim = 1f * intersectionCardinality/union.size();
        //float sim = 1f * intersectionCardinality+1/union.size()+1; //Considero anche il tag nell'insieme

        //System.out.println("Sim: "+sim+"\n");
        return sim;
    }

}
