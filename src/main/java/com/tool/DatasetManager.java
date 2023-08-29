package com.tool;

import com.tool.similarity.AllAttributesJaccardSimilarity;

import java.sql.*;

/*
* Gestisce la lettura e la scrittura del dataset nel file ds.db
* Durante la scrittura, calcola anche la similarità tra gli html presenti nel file .db
* */

public class DatasetManager {

    private String folderPath = "/Volumes/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels";

    public void main(){

        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:"+folderPath+"/gs.db");
            Statement stat = conn.createStatement();


            /*
            * Cose da fare per popolare il dataset:
            * 1) creare una nuova colonna per tipo di similarità nel file db (per ora solo attribute) OK
            * 2) Iterare sulla tabella nearduplicates: per ogni record prendere appname, state1, state2
            * 3) Ricreare il percorso per arrivare a state1.html e state2.html tramite appname, state1 e state2
            * 4) Presi gli html, calcolare il kernel normalizzato
            * 5) Aggiornare la colonna ATTRIBUTE_SIM
            *
            * */
            ResultSet rs = stat.executeQuery("SELECT appname, crawl,state1, state2 from nearduplicates;");

            int counter = 0;
            while (rs.next()){
                SimilarityTool similarityTool = new SimilarityTool(new AllAttributesJaccardSimilarity(),0.4f,0.4f,1,0.01f,null);

                String appName = rs.getString("appname");
                String crawl = rs.getString("crawl");
                String state1 = rs.getString("state1");
                String state2 = rs.getString("state2");
                //System.out.println(appName+" "+crawl+" "+state1+" "+state2);

                String pathHTML1 = folderPath+"/"+appName+"/"+crawl+"/states/"+state1+".html";
                String pathHTML2 = folderPath+"/"+appName+"/"+crawl+"/states/"+state2+".html";

                float kernel = similarityTool.computeKernelNormalized(pathHTML1,pathHTML2,"noScript");

                //System.out.println("Path state1: "+pathHTML1+"\nPath state2: "+pathHTML2+"\n");
                System.out.println(appName+" "+crawl+" "+state1+" "+state2+" - "+kernel + " | "+(++counter));
            }


            rs.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
