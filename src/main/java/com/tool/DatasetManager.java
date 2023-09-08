package com.tool;

import com.tool.Trees.TreeFactory;
import com.tool.similarity.AllAttributesJaccardSimilarity;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.kernel.DirectKernel;
import it.uniroma2.sag.kelp.kernel.tree.PartialTreeKernel;
import it.uniroma2.sag.kelp.kernel.tree.SmoothedPartialTreeKernel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.tool.representations.ManageTreeRepresentation.popolateTree;
import static java.lang.Math.round;


/*
* Gestisce la lettura e la scrittura del dataset nel file ds.db
* Durante la scrittura, calcola anche la similarità tra gli html presenti nel file .db
* */

public class DatasetManager {

    //private String folderPath = "F:\\Università\\Libri_università\\Magistrale\\Tesi_magistrale\\Web_Test_Generation\\Crawls_complete\\GroundTruthModels"; path per windows
    //private String folderPath = "/Volumes/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/";


    public void main() throws InterruptedException {

        int numCores = Runtime.getRuntime().availableProcessors();
        int numRows = 97490;
        int slice = round(numRows/numCores) + 1;
        System.out.println(slice);
        int start = 0;

        List<Thread> threads = new ArrayList<>();

        for(int i = 0; i < numCores; i++){
            start = i*slice;
            Thread t = new Thread(new MyRunnable(slice,start,"/run/media/giuseppeporcaro/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/","gs_full_dom.db",i));

            t.start();
            threads.add(t);

        }

        for(Thread thread: threads){
            thread.join();
        }
    }

    public class MyRunnable implements Runnable{

        private static Integer counter = 0;

        private final Object lock = new Object();
        private Integer numThread;
        private Integer slice;
        private Integer start;

        private String folderPath;
        private String db;
        @Override
        public void run() {

            long startTime = System.currentTimeMillis();
            try {
                Class.forName("org.sqlite.JDBC");
                Connection conn = DriverManager.getConnection("jdbc:sqlite:"+folderPath+"//"+db);
                Statement stat = conn.createStatement();

                ResultSet rs = stat.executeQuery("SELECT appname, crawl,state1, state2 from nearduplicates order by appname, crawl, state1, state2 limit "+start+","+slice+";");


                ResultSet a = rs;
                rs.close();
                conn.close();

                while (a.next()){
                    SimilarityTool similarityTool = new SimilarityTool(new AllAttributesJaccardSimilarity(),0.4f,0.4f,1,0.05f,null);

                    String appName = rs.getString("appname");
                    String crawl = rs.getString("crawl");
                    String state1 = rs.getString("state1");
                    String state2 = rs.getString("state2");

                    String pathHTML1 = folderPath+"/"+appName+"/"+crawl+"/doms/"+state1+".html";
                    String pathHTML2 = folderPath+"/"+appName+"/"+crawl+"/doms/"+state2+".html";

                    float kernel = similarityTool.computeKernelNormalized(pathHTML1,pathHTML2,"all");
                    //System.out.println("Path state1: "+pathHTML1+"\nPath state2: "+pathHTML2+"\n");

                    synchronized (lock){
                        counter++;
                    }
                    System.out.println(appName+" "+crawl+" "+state1+" "+state2+" - "+kernel + " | "+counter+ " | Thread:  "+numThread);

                    if(counter % 10 == 0){
                        long elapsedTime = (startTime - System.currentTimeMillis())/1000;
                        System.out.println("Thread: "+numThread+" | seconds: "+elapsedTime);
                    }

                    String query = "UPDATE nearduplicates SET ATTRIBUTE_SIM=? where appname= ? and crawl = ? and state1 = ? and state2 = ?;";
                    PreparedStatement statement = conn.prepareStatement(query);
                    statement.setFloat(1, kernel);
                    statement.setString(2, appName);
                    statement.setString(3, crawl);
                    statement.setString(4,state1);
                    statement.setString(4,state2);

                    statement.executeUpdate();
                }

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        public MyRunnable(Integer slice, Integer start, String folderPath, String db, Integer numThread) {
            this.slice = slice;
            this.start = start;
            this.folderPath = folderPath;
            this.db = db;
            this.numThread = numThread;
        }
    }
}
