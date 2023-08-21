package com.tool;

import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;
import java.util.HashSet;
import java.util.Set;

public class AllAttributesJaccardSimilarity implements StructureElementSimilarityI {


    @Override
    public float sim(StructureElement sx, StructureElement sd) {

        String tagSx = getTag(sx.getTextFromData());
        String tagSd = getTag(sd.getTextFromData());

        if(!tagSx.equalsIgnoreCase(tagSd.toLowerCase())){
            return 0f;
        }

        Set<String> attributesSx = getAttributes(sx.getTextFromData());
        Set<String> attributeSd = getAttributes(sd.getTextFromData());
        int sxSize = attributesSx.size();
        int sdSize = attributeSd.size();

        if(sxSize == 0 && sdSize == 0){
            return 1f;
        }

        if(sxSize == 0 || sdSize == 0){
            return 0f;
        }

        Set<String> union = new HashSet<>(attributesSx);
        union.addAll(attributeSd);
        int intersectionCardinality = (sxSize+sdSize) - union.size();

        //System.out.println(tagSx + " " +tagSd + "\t"+attributesSx+"\t"+attributeSd+"\t - "+intersectionCardinality + ", "+unionCardinality + ", "+intersectionCardinality/unionCardinality);

        return 1f *intersectionCardinality/union.size();
    }

    private Set<String> getAttributes(String data){

        Set<String> attrValues = new HashSet<>();
        String token [] = data.split("#");


        for(int i = 1 ; i < token.length; i++){
            if(token[i] != ""){
                attrValues.add(token[i]);
            }
        }
        return attrValues;
    }

    private String getTag(String data){

        String token [] = data.split("#");

        return token[0];

    }
}
