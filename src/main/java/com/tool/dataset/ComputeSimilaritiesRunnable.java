package com.tool.dataset;

import com.tool.SimilarityTool;
import com.tool.similarity.AllAttributesJaccardSimilarity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComputeSimilaritiesRunnable implements Runnable{

    private static Integer counter = 0;
    private final Object lock = new Object();
    private Integer numThread;
    private Integer slice;
    private Integer start;
    private String folderPath;
    private String datasetDB;
    @Override
    public void run() {

        long startTime = System.currentTimeMillis();
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:"+folderPath+"/"+ datasetDB);
            Statement stat = conn.createStatement();

            ResultSet rs = stat.executeQuery("SELECT appname, crawl,state1, state2 from nearduplicates order by appname, crawl, state1, state2 limit "+start+","+slice+";");

            List<DatasetRow> entities = new ArrayList<>();

            while(rs.next()){
                entities.add(new DatasetRow(rs.getString("appname"),rs.getString("crawl"),rs.getString("state1"),rs.getString("state2")));
            }

            rs.close();
            conn.close();

            for(DatasetRow entity: entities){
                SimilarityTool similarityTool = new SimilarityTool(new AllAttributesJaccardSimilarity(),0.4f,0.4f,1,0.05f,null);

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
                    DriverManager.setLoginTimeout(10);
                    Connection connUpdate = DriverManager.getConnection("jdbc:sqlite:"+folderPath+"/"+ datasetDB);
                    connUpdate.setAutoCommit(true);

                    String query = "UPDATE nearduplicates SET ATTRIBUTE_SIM=? where appname=? and crawl=? and state1=? and state2=?;";
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
                    System.out.println(appName+" "+crawl+" "+state1+" "+state2+" - "+kernel + " |\tThread: "+numThread+ " - Pair N°:  "+counter);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ComputeSimilaritiesRunnable(Integer slice, Integer start, String folderPath, String datasetDB, Integer numThread) {
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
