package com.tool.similarity;

import com.tool.Utils;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;

import java.util.HashSet;
import java.util.Set;

import static com.tool.Utils.*;

public class WeightedAttributeSimilarity implements StructureElementSimilarityI {

    private final float ID_WEIGHT;

    public WeightedAttributeSimilarity(float idWeight){
        ID_WEIGHT = idWeight;
    }
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

        /*Same id -> identical nodes*/
        if( idSx != null && idSd != null ){
            if(idSx.equals(idSd)){
                return 1f;
            }
        }

        /*Per la Weighted Jaccard Similarity dovrei:
        *
        * 1) Creare due vettori, uno per ciascun StructureElement
        * 2) Ognuno di questi vettori conterrà il peso per ciascun attributo che voglio mettere nel peso
        *   Esempio: se voglio considerare i pesi per classe, nome, href allora avrò  due vettori di dimensione 3 che conterranno i pesi per i tre attributi citati
        * 3) Uso le formule che ho scritto sul foglio<
        *
        * Potrei scrivere il costruttore in modo tale che prende una mappa di attributi con i loro pesi ed il tipo di similarità (jacccard, coseno, etc.)
        * Creo una factory per la similarità che deve fare il calcolo, presi i due vettori di pesi degli attributi.
        * Devo costruire i vettori: dalla mappa devo controllare se i due elementi correnti
        *  contengono gli attributi citati nella mappa stessa, se si allora metto il peso corrispettivo, altrimenti metto 0
        * Costruiti i vettori li do all'oggetto/interfaccia che fara il calcolo della similarità pesata.
        *
        *
        * Dopodichè vedo come funziona con un debug.
        *
        * Attributi a cui dare un peso sono: id, class, style, title,
        *
        * */






        /*
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
        return sim;*/
        return 0.0f;
    }
}
