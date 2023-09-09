package com.tool;

import com.tool.Trees.Tree;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Utils {

    public static String getTestDOM(String path){
        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
            in.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        return contentBuilder.toString();
    }

    public static String getId(Set<String> attributes){

        for(String attrValue: attributes){
            String token[] = attrValue.split("=");
            String attr = token[0];
            if(attr.equalsIgnoreCase("id")){
                return token[1];
            }
        }return null;
    }

    public static Set<String> getAttributes(String data){

        Set<String> attrValues = new HashSet<>();
        String token [] = data.split("~");


        for(int i = 1 ; i < token.length; i++){
            if(token[i] != ""){
                attrValues.add(token[i]);
            }
        }
        return attrValues;
    }

    public static Set<String> getChildrenData(String data){
        Set<String> childrenData = new HashSet<>();
        String token[] = data.split("~~");

        for(String s: token){
            childrenData = getAttributes(s);
        }
        return childrenData;

    }

    public static String getTag(String data){

        String token [] = data.split("~");

        return token[0];

    }
}
