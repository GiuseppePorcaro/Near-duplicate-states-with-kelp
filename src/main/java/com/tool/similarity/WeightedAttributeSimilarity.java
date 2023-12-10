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

        Set<String> attributesSx = getAttributes(sx.getTextFromData());
        Set<String> attributeSd = getAttributes(sd.getTextFromData());
        String idSx = getId(attributesSx);
        String idSd = getId(attributeSd);
        String tagSx = Utils.getTag(sx.getTextFromData());
        String tagSd = Utils.getTag(sd.getTextFromData());

        System.out.println("Tags: "+tagSx+" - "+tagSd);
        System.out.println("Attr sx: "+attributesSx+"\nAttr sd: "+attributeSd);

        if(!tagSx.equalsIgnoreCase(tagSd.toLowerCase())){
            System.out.println("Sim: 0 - Tag diversi\n");
            return 0f;
        }

        System.out.println("Ids: "+idSx+" - "+idSd);

        /*Same id -> identical nodes*/
        if( idSx != null && idSd != null ){
            if(idSx.equals(idSd)){
                System.out.println("Sim: 1 - id uguali\n");
                return 1f;
            }
        }

        int sxSize = attributesSx.size();
        int sdSize = attributeSd.size();

        if(sxSize == 0 || sdSize == 0){
            System.out.println("Sim: 0 - both no attributes\n");
            return 0f;
        }

        Set<String> attributesUnion = new HashSet<>(attributesSx);
        attributesUnion.addAll(attributeSd);
        Set<String> attributesNotChosen = new HashSet<>(attributesUnion);
        attributesNotChosen.retainAll(weightsChoosenAttributes.keySet());

        System.out.println("Union: "+attributesUnion.toString()+"\nNot chosen: "+attributesNotChosen.toString());
        int dimAllAttrs = attributesUnion.size();
        int dimWeightsForChosenAttrs = weightsChoosenAttributes.size();
        int[] attrChosen = new int[dimWeightsForChosenAttrs];

        int dimForNotChosenAttrs = dimAllAttrs-dimWeightsForChosenAttrs;
        if(dimForNotChosenAttrs < 0){
            dimForNotChosenAttrs = 0;
        }
        int[] attrOthers = new int[dimForNotChosenAttrs];

        //BUG: QUANDO FACCIO SET.CONTAINS(ATTR)) IO STO CERCANDO UN ATTRIBUTO TIPO "CLASS" IN UN INSIEME DI OGGETTI DEL TIPO "CLASS="VALORE"".
        //QUINDI NON TROVERO' MAI NULLA. DEVO AGGIUSTARE IL BUG PRIMA
        setAttrArray(attributesSx,attributeSd,weightsChoosenAttributes.keySet(),attrChosen);
        setAttrArray(attributesSx,attributeSd,attributesNotChosen,attrOthers);



        System.out.println("AttrChosen: "+printArray(attrChosen)+"\nAttrOthers: "+printArray(attrOthers));

        float sim = computeSim(attrChosen, dimAllAttrs, dimWeightsForChosenAttrs, attrOthers);

        System.out.println("Sim: "+sim+"\n****************************\n\n");
        return sim;
    }

    private String printArray(int[] array){
        List<Integer> list = new ArrayList<>();

        for(int i : array){
            list.add(i);
        }
        return list.toString();
    }

    private float computeSim(int[] attrChosen, int dimAllAttrs, int dimWeightsForChosenAttrs, int[] attrOthers) {
        float sim = 0;

        int i =0;
        for(String attr: weightsChoosenAttributes.keySet()){
            sim += weightsChoosenAttributes.get(attr) * attrChosen[i];
            i++;
        }

        float p = weightOtherAttrs / (dimAllAttrs - dimWeightsForChosenAttrs);
        for (int a: attrOthers){
            sim += a * p;
        }
        return sim;
    }

    private void setAttrArray(Set<String> attributesSx ,Set<String> attributeSd,Collection<String> attributes, int[] attrArray){
        int i = 0;
        for(String attr: attributes){
            if(attributesSx.contains(attr)&&attributeSd.contains(attr)){
                attrArray[i] = 1;
            }else{
                attrArray[i] = 0;
            }i++;
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
