package com.tool.similarity;

import com.tool.Utils;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;

import java.util.*;

import static com.tool.Utils.*;

public class WeightedAttributeSimilarity implements StructureElementSimilarityI {

    private final Map<String, Float> weightsChoosenAttributes;
    private final float weightOtherAttrs;

    public WeightedAttributeSimilarity(Map<String, Float> weightsChoosenAttributes, float weightOtherAttrs) throws Exception {

        this.weightsChoosenAttributes = weightsChoosenAttributes;
        this.weightOtherAttrs = weightOtherAttrs;

        if (!areWeightsLegal(weightsChoosenAttributes.values(),weightOtherAttrs)) {
            throw new Exception("The sum of weights is not equal to 1!");
        }
    }

    @Override
    public float sim(StructureElement sx, StructureElement sd) {

        Map<String, String> attributesSx = getMapAttribues(sx.getTextFromData());
        Map<String, String> attributeSd = getMapAttribues(sd.getTextFromData());

        String idSx = attributesSx.get("id");
        String idSd = attributeSd.get("id");
        String tagSx = Utils.getTag(sx.getTextFromData());
        String tagSd = Utils.getTag(sd.getTextFromData());

        //System.out.println("Tags: "+tagSx+" - "+tagSd);
        //System.out.println("Attr sx: "+attributesSx+"\nAttr sd: "+attributeSd);

        if(!tagSx.equalsIgnoreCase(tagSd.toLowerCase())){
            //System.out.println("Sim: 0 - Tag diversi\n");
            return 0f;
        }

        //System.out.println("Ids: "+idSx+" - "+idSd);

        /*Same id -> identical nodes*/
        if( idSx != null && idSd != null ){
            if(idSx.equals(idSd)){
                //System.out.println("Sim: 1 - id uguali\n");
                return 1f;
            }else{
                //System.out.println("Sim: 0 - id diversi\n");
                return 0f;
            }
        }

        int sxSize = attributesSx.size();
        int sdSize = attributeSd.size();

        if(sxSize == 0 || sdSize == 0){
            //System.out.println("Sim: 0 - both no attributes\n");
            return 0f;
        }

        Set<String> attributesChosen = new HashSet<>(weightsChoosenAttributes.keySet());
        Set<String> attributesNotChosen = getAttributesNotChosen(attributesSx.keySet(),attributeSd.keySet(),attributesChosen);



        //System.out.println("Chosen: "+attributesChosen+"\nNot chosen: "+attributesNotChosen);

        Map<String, Integer> attrChosen = new HashMap<>();
        Map<String, Integer> attrOthers = new HashMap<>();

        setAttrArray(attributesSx,attributeSd,attributesChosen,attrChosen);
        setAttrArray(attributesSx,attributeSd,attributesNotChosen,attrOthers);

        //System.out.println("AttrChosen: "+attrChosen+"\nAttrOthers: "+attrOthers);

        float sim = computeSim(attrChosen, attrOthers);

        //System.out.println("Sim: "+sim+"\n****************************\n\n");
        return sim;
    }


    private Set<String> getAttributesNotChosen(Set<String> attributesSx,Set<String> attributeSd, Set<String> attributes){

        Set<String> attrNotChosen = new HashSet<>();

        for(String attr: attributesSx){
            if(!attributes.contains(attr)){
                attrNotChosen.add(attr);
            }
        }

        for(String attr: attributeSd){
            if(!attributes.contains(attr)){
                attrNotChosen.add(attr);
            }
        }

        return attrNotChosen;
    }

    private float computeSim(Map<String, Integer> attrChosen, Map<String,Integer> attrOthers) {
        float sim = 0;

        for(String attr: weightsChoosenAttributes.keySet()){
            sim += weightsChoosenAttributes.get(attr) * attrChosen.get(attr);

        }

        float p = weightOtherAttrs / attrOthers.size();
        for (String attr: attrOthers.keySet()){
            sim += attrOthers.get(attr) * p;
        }
        return sim;
    }

    private void setAttrArray(Map<String, String> attributesSx ,Map<String, String> attributeSd,Set<String> attributes, Map<String, Integer> attrPresence){

        for(String attr: attributes){
            String valueSx = attributesSx.get(attr);
            String valueSd = attributeSd.get(attr);

            if(valueSx != null && valueSd != null){
                if(valueSx.equals(valueSd)){
                    attrPresence.put(attr,1);
                }else{
                    attrPresence.put(attr,0);
                }
            }else{
                attrPresence.put(attr,0);
            }
        }
    }

    private boolean areWeightsLegal(Collection<Float> weigths, float weightOtherAttrs) {

        float sum = 0;
        for(float weight: weigths){
            sum += weight;
        }

        return sum + weightOtherAttrs == 1;
    }
}
