package com.tool.similarity;

import com.tool.Utils;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;
import java.util.HashSet;
import java.util.Set;
import static com.tool.Utils.getAttributes;
import static com.tool.Utils.getId;

public class AllAttributesDiceSorensenSimilarity implements StructureElementSimilarityI {
    @Override
    public float sim(StructureElement sx, StructureElement sd) {

        String tagSx = Utils.getTag(sx.getTextFromData());
        String tagSd = Utils.getTag(sd.getTextFromData());

        //System.out.println("Tags: "+tagSx+" - "+tagSd);

        if(!tagSx.equalsIgnoreCase(tagSd.toLowerCase())){
            //System.out.println("Sim: 0\n");
            return 0f;
        }

        Set<String> attributesSx = getAttributes(sx.getTextFromData());
        Set<String> attributeSd = getAttributes(sd.getTextFromData());
        String idSx = getId(attributesSx);
        String idSd = getId(attributeSd);

        //System.out.println("Ids: "+idSx+" - "+idSd);

        if( idSx != null && idSd != null ){
            if(idSx.equals(idSd)){
                //System.out.println("Sim: 1\n");
                return 1f;
            }
        }

        if(attributesSx.size() == 0 && attributeSd.size() == 0){
            //System.out.println("Sim: 1\n");
            return 1f;
        }


        Set<String> intersection = new HashSet<>(attributesSx);
        intersection.retainAll(attributeSd);

        //System.out.println("Attributes: "+attributesSx+" - "+attributeSd+" Sizes: "+attributesSx.size()+" - "+attributeSd.size()+ " - inters: "+intersection.size());

        float sim = (2f*intersection.size())/(attributeSd.size()+attributesSx.size());

        //System.out.println("Sim: "+sim+"\n");

        return sim;
    }


}
