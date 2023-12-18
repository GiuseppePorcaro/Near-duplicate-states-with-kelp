package com.tool.dataset;

import com.tool.SimilarityTool;
import com.tool.similarity.WeightedAttributeSimilarity;
import com.tool.similarity.WeightedRefinedAttributeSimilarity;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComputeWeightedRefinedSimilarityRunnable implements Runnable{

    private static Integer counter = 0;
    private static final Object lock = new Object();
    private Integer numThread;
    private Integer slice;
    private Integer start;
    private String folderPath;
    private String datasetDB;
    @Override
    public void run() {

        long startTime = System.currentTimeMillis();
        try {
            DriverManager.setLoginTimeout(30);
            Connection conn = DriverManager.getConnection("jdbc:sqlite:"+ datasetDB);
            Statement stat = conn.createStatement();

            ResultSet rs = stat.executeQuery("SELECT appname, crawl,state1, state2 from nearduplicates order by appname, crawl, state1, state2 limit "+start+","+slice+";");

            List<DatasetRow> entities = new ArrayList<>();

            while(rs.next()){
                entities.add(new DatasetRow(rs.getString("appname"),rs.getString("crawl"),rs.getString("state1"),rs.getString("state2")));
            }

            rs.close();
            conn.close();

            Map<String, Float> weights = new HashMap<>();
            weights.put("class",0.5f);
            weights.put("style",0.1f);
            weights.put("title",0.2f);
            weights.put("href",0.1f);
            for(DatasetRow entity: entities){
                SimilarityTool similarityTool = new SimilarityTool(new WeightedRefinedAttributeSimilarity(weights,0.1f),0.4f,0.1f,1,0.05f,null);

                String appName = entity.getAppname();
                String crawl = entity.getCrawl();
                String state1 = entity.getState1();
                String state2 = entity.getState2();

                String pathHTML1 = folderPath+"/"+appName+"/"+crawl+"/doms/"+state1+".html";
                String pathHTML2 = folderPath+"/"+appName+"/"+crawl+"/doms/"+state2+".html";

                float kernel = similarityTool.computeKernelNormalized(pathHTML1,pathHTML2,"all");

                if(counter % 10 == 0){
                    long elapsedTime = (System.currentTimeMillis() - startTime)/1000;
                    System.out.println("Thread: "+numThread+" | seconds: "+elapsedTime);
                }

                synchronized (lock){
                    DriverManager.setLoginTimeout(30);
                    Connection connUpdate = DriverManager.getConnection("jdbc:sqlite:"+ datasetDB);
                    connUpdate.setAutoCommit(true);

                    String query = "UPDATE nearduplicates SET REFATTRIBUTE=? where appname=? and crawl=? and state1=? and state2=?;";
                    PreparedStatement statement = connUpdate.prepareStatement(query);
                    statement.setFloat(1, kernel);
                    statement.setString(2, appName);
                    statement.setString(3, crawl);
                    statement.setString(4,state1);
                    statement.setString(5,state2);

                    statement.executeUpdate();

                    statement.close();
                    connUpdate.close();

                    counter++;
                    String format = "%-40s%s%n";
                    System.out.printf(format,appName+" "+crawl+" "+state1+" "+state2+" - "+kernel," | Thread: "+numThread+ " - Pair N°:  "+counter);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ComputeWeightedRefinedSimilarityRunnable(Integer slice, Integer start, String folderPath, String datasetDB, Integer numThread) {
        this.slice = slice;
        this.start = start;
        this.folderPath = folderPath;
        this.datasetDB = datasetDB;
        this.numThread = numThread;
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
