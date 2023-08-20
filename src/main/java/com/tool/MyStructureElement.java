package com.tool;

import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;

import java.util.Map;
import java.util.Set;

public class MyStructureElement extends StructureElement {

    private String tag;
    private Map<String,String> attributes;


    public MyStructureElement(String tag, Map<String, String> attributes){
        this.tag = tag;
        this.attributes = attributes;
    }

    @Override
    public void setDataFromText(String structureElementDescription) throws Exception {

    }

    @Override
    public String getTextFromData() {

        String data = ""+tag;


        for(String key: attributes.keySet()){
            String value = attributes.get(key);

            data = data+"#"+key+"="+value;
        }

        return data;
    }
}
