package com.tool.similarity;

import com.tool.Utils;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;

import java.util.*;

import static com.tool.Utils.getMapAttribues;

/*
 * As WeightedAttributeSimilarity, but if an attribute has more than a value it check how muck similar are those attributes based on jaccard similarity.
 * Then updates weights accordling on how much they are similar.
 *
 *
 * */
public class MultipleWeightedRefinedAttributeSimilarity implements StructureElementSimilarityI {

    private final Map<String, Map<String, Float>> weightMapForChoosenElements;
    private Map<String, Float> weightsChoosenAttributes = new HashMap<>();
    private final float weightOtherAttrs;

    public MultipleWeightedRefinedAttributeSimilarity(Map<String, Map<String, Float>> weightMapForChoosenElements, float weightOtherAttrs) throws Exception {

        this.weightMapForChoosenElements = weightMapForChoosenElements;
        this.weightOtherAttrs = weightOtherAttrs;

        for(Map<String,Float> map: weightMapForChoosenElements.values()){
            if (!areWeightsLegal(map.values(),weightOtherAttrs)) {
                throw new Exception("The sum of weights for a map is not equal to 1!");
            }
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

        weightsChoosenAttributes = setWeightsToUse(tagSx);

        //System.out.println("Weights: "+weightsChoosenAttributes.values());

        Set<String> attributesChosen = new HashSet<>(weightsChoosenAttributes.keySet());
        Set<String> attributesNotChosen = getAttributesNotChosen(attributesSx.keySet(),attributeSd.keySet(),attributesChosen);

        //System.out.println("Chosen: "+attributesChosen+"\nNot chosen: "+attributesNotChosen);

        Map<String, Float> attrChosen = new HashMap<>();
        Map<String, Float> attrOthers = new HashMap<>();

        setAttrArray(attributesSx,attributeSd,attributesChosen,attrChosen);
        setAttrArray(attributesSx,attributeSd,attributesNotChosen,attrOthers);

        //System.out.println("AttrChosen: "+attrChosen+"\nAttrOthers: "+attrOthers);

        //System.out.println("attrChosen: "+attrChosen+"\nAttrNotChosen: "+attrOthers+"#############################");
        float sim = computeSim(attrChosen, attrOthers);

        //System.out.println("Sim: "+sim+"\n****************************\n\n");
        return sim;
    }


    private Map<String, Float> setWeightsToUse(String tag){

        if(!weightMapForChoosenElements.containsKey(tag)){
            return weightMapForChoosenElements.get("default");
        }
        return weightMapForChoosenElements.get(tag);
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

    private float computeSim(Map<String, Float> attrChosen, Map<String,Float> attrOthers) {
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

    private float getValuesSim(String valuesSxString, String valuesSdString){

        Set<String> valuesSx = new HashSet<>();
        Set<String> valuesSd = new HashSet<>();

        String tokenValuesSx[] = valuesSxString.split("~~");
        //System.out.println("Sx-> Values:");
        for(String value: tokenValuesSx){
            //System.out.println(value+" ");
            valuesSx.add(value);
        }//System.out.println("\n");

        String tokenValuesSd[] = valuesSdString.split("~~");
        //System.out.println("Sd-> Values:");
        for(String value: tokenValuesSd){
            //System.out.println(value+" ");
            valuesSd.add(value);
        }//System.out.println("\n\n");

        Set<String> intersection = new HashSet<>(valuesSx);
        intersection.retainAll(valuesSd);
        Set<String> union = new HashSet<>(valuesSx);
        union.addAll(valuesSd);

        float sim = 1.0f * intersection.size()/ union.size();
        //System.out.println("Attr sim: "+sim);

        return sim;
    }

    private void setAttrArray(Map<String, String> attributesSx ,Map<String, String> attributeSd,Set<String> attributes, Map<String, Float> attrPresence){

        for(String attr: attributes){
            String valueSx = attributesSx.get(attr);
            String valueSd = attributeSd.get(attr);

            //System.out.println("SX-> Attr: "+attr+" = "+valueSx);
            //System.out.println("SD-> Attr: "+attr+" = "+valueSd);

            if(valueSx != null && valueSd != null){
                if(valueSx.equals(valueSd)){
                    attrPresence.put(attr,1.0f);
                }else{
                    attrPresence.put(attr,getValuesSim(valueSx,valueSd));
                }
            }else{
                attrPresence.put(attr,0.0f);
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

/*
*
* * Map<String, Float> weightsDefault = new HashMap<>();
        weightsDefault.put("class",0.5f);
        weightsDefault.put("style",0.2f);
        weightsDefault.put("title",0.2f);

        Map<String, Float> weightAnchor = new HashMap<>();
        weightAnchor.put("href",0.5f);
        weightAnchor.put("class",0.2f);
        weightAnchor.put("target",0.1f);
        weightAnchor.put("type",0.1f);

        Map<String, Float> weightInput = new HashMap<>();
        weightInput.put("name",0.4f);
        weightInput.put("class",0.2f);
        weightInput.put("type",0.1f);
        weightInput.put("placeholder",0.2f);

        Map<String, Float> weightImages = new HashMap<>();
        weightImages.put("src",0.4f);
        weightImages.put("class",0.3f);
        weightImages.put("title",0.1f);
        weightImages.put("alt",0.1f);

        Map<String, Float> weightForm = new HashMap<>();
        weightForm.put("action",0.5f);
        weightForm.put("class",0.2f);
        weightForm.put("name",0.1f);
        weightForm.put("method",0.1f);

 * */

