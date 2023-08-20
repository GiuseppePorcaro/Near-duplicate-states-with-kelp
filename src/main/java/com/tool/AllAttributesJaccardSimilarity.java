package com.tool;

import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;

import java.util.HashSet;
import java.util.Set;

public class AllAttributesJaccardSimilarity implements StructureElementSimilarityI {


    @Override
    public float sim(StructureElement sx, StructureElement sd) {

        //Se i nodi li rappresento come insiemi di tag e attributo=valore, allora qui uso un qualche tipo di indice di similitudine tra insiemi. Poi si vedono i casi particolari.

        //FARE DEBUG E VEDERE PERCHè IL VALORE DEL KERNEL RIMANE A 0

        String tagSx = getTag(sx.getTextFromData());
        String tagSd = getTag(sd.getTextFromData());

        if(!tagSx.equalsIgnoreCase(tagSd.toLowerCase())){
            return 0;
        }

        Set<String> attributesSx = getAttributes(sx.getTextFromData());
        Set<String> attributeSd = getAttributes(sd.getTextFromData());

        Set<String> union = new HashSet<>(attributesSx);
        Set<String> intersection = new HashSet<>(attributesSx);
        intersection.retainAll(attributeSd);
        union.addAll(attributeSd);

        attributesSx.retainAll(attributeSd);
        float intersectionCardinality = intersection.size();
        float unionCardinality = union.size();

        if(unionCardinality == 0){
            return 1;
        }

        System.out.println(tagSx + " " +tagSd + "\t"+attributesSx+"\t"+attributeSd+"\t - "+intersectionCardinality + ", "+unionCardinality + ", "+intersectionCardinality/unionCardinality);

        return intersectionCardinality/unionCardinality;
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
