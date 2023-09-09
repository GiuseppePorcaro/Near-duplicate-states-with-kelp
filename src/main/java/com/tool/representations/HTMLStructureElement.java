package com.tool.representations;

import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;

import java.util.Map;

public class HTMLStructureElement extends StructureElement {

    private String tag;
    private Map<String,String> attributes;

    @Override
    public String getTextFromData() {

        String data = ""+tag;


        for(String key: attributes.keySet()){
            String value = attributes.get(key);

            data = data+"~"+key+"="+value; //Vedere se questo carattere separatore va bene
        }

        return data;
    }

    public HTMLStructureElement(String tag, Map<String, String> attributes){
        this.tag = tag;
        this.attributes = attributes;
    }

    @Override
    public void setDataFromText(String structureElementDescription) throws Exception {
        //--
    }
}
