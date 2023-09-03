package com.tool;

import com.tool.Trees.TreeFactory;
import com.tool.similarity.AllAttributesJaccardSimilarity;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;

public class MainClass {

    public static void main(String args[]) throws Exception {

        /*Itera sui record del database -> calcola il kernel per ogni coppia -> per ora stampa solo il risultato del kernel*/
        DatasetManager datasetManager = new DatasetManager();
        datasetManager.main();

        /*Calcola il kernel per una singola coppia di html passati in input al programma*/
        //mainForScriptPython(args);

    }

    public static void mainForScriptPython(String args[]) throws Exception {
        SimilarityTool similarityTool = new SimilarityTool(new AllAttributesJaccardSimilarity(),0.4f,0.4f,1,0.01f,null);

        String folderPath = "/Volumes/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/";
        String appName = args[0];
        String crawl = args[1];
        String state1 = args[2];
        String state2 = args[3];
        Integer counter = Integer.parseInt(args[4]);
        //System.out.println(appName+" "+crawl+" "+state1+" "+state2);

        String pathHTML1 = folderPath+"/"+appName+"/"+crawl+"/doms/"+state1+".html";
        String pathHTML2 = folderPath+"/"+appName+"/"+crawl+"/doms/"+state2+".html";

        float kernel = similarityTool.computeKernelNormalized(pathHTML1,pathHTML2,"treeForCrawl");
        //System.out.println("Path state1: "+pathHTML1+"\nPath state2: "+pathHTML2+"\n");
        System.out.println(appName+" "+crawl+" "+state1+" "+state2+" - | "+kernel+ " | "+counter);
    }


}
