package com.tool;

import com.tool.Trees.TreeFactory;
import com.tool.similarity.AllAttributesJaccardSimilarity;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.kernel.DirectKernel;
import it.uniroma2.sag.kelp.kernel.tree.PartialTreeKernel;
import it.uniroma2.sag.kelp.kernel.tree.SmoothedPartialTreeKernel;

import java.sql.*;

import static com.tool.representations.ManageTreeRepresentation.popolateTree;


/*
* Gestisce la lettura e la scrittura del dataset nel file ds.db
* Durante la scrittura, calcola anche la similarità tra gli html presenti nel file .db
* */

public class DatasetManager {

    //private String folderPath = "F:\\Università\\Libri_università\\Magistrale\\Tesi_magistrale\\Web_Test_Generation\\Crawls_complete\\GroundTruthModels"; path per windows
    private String folderPath = "/Volumes/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/";

    public void main(){

        DirectKernel<TreeRepresentation> kernelAttributeNotNormalized = new SmoothedPartialTreeKernel(0.4f,0.4f,1,0.05f,new AllAttributesJaccardSimilarity(),null);

        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:"+folderPath+"//gs.db");
            Statement stat = conn.createStatement();

            /*
            * PROBLEMA: La matrice DeltaMatrix viene ingrandita a dismisura
            * */
            ResultSet rs = stat.executeQuery("SELECT appname, crawl,state1, state2 from nearduplicates;");

            int counter = 0;
            while (rs.next()){
                SimilarityTool similarityTool = new SimilarityTool(new AllAttributesJaccardSimilarity(),0.4f,0.4f,1,0.05f,null);

                String appName = rs.getString("appname");
                String crawl = rs.getString("crawl");
                String state1 = rs.getString("state1");
                String state2 = rs.getString("state2");

                String pathHTML1 = folderPath+"/"+appName+"/"+crawl+"/doms/"+state1+".html";
                String pathHTML2 = folderPath+"/"+appName+"/"+crawl+"/doms/"+state2+".html";

                PartialTreeKernel partialTreeKernel = new PartialTreeKernel(0.4f,0.4f,1,null);
                NormalizationKernel partialTreeKernelNormalized = new NormalizationKernel(partialTreeKernel);

                TreeRepresentation firstTree = popolateTree(TreeFactory.createTree(pathHTML1,"treeForCrawl"));
                TreeRepresentation secondTree = popolateTree(TreeFactory.createTree(pathHTML2,"treeForCrawl"));
                float partialTreeKernelNorm = partialTreeKernelNormalized.kernelComputation(firstTree,secondTree);



                //float kernel = similarityTool.computeKernelNormalized(pathHTML1,pathHTML2,"treeForCrawl");
                //System.out.println("Path state1: "+pathHTML1+"\nPath state2: "+pathHTML2+"\n");
                //System.out.println(appName+" "+crawl+" "+state1+" "+state2+" - "+kernel + " | "+(++counter));
                System.out.println("\tPartialTreeKenrel: "+partialTreeKernelNorm);


                /*
                * Qui aggiornare il database
                * */
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
