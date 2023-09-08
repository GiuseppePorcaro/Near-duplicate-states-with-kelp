package com.tool;

import com.tool.similarity.AllAttributesJaccardSimilarity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;


/*
* Gestisce la lettura e la scrittura del dataset nel file ds.db
* Durante la scrittura, calcola anche la similarità tra gli html presenti nel file .db
* */

public class DatasetManager {

    public void main() throws InterruptedException {

        int numCores = Runtime.getRuntime().availableProcessors();
        int numRows = 97490;
        int slice = round(numRows/numCores) + 1;
        int start = 0;
        String folderPath = "/run/media/giuseppeporcaro/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels";
        String datasetDB = "gs_full_dom.db";

        List<Thread> threads = new ArrayList<>();

        for(int i = 0; i < numCores; i++){
            start = i*slice;
            Thread t = new Thread(new MyRunnable(slice,start,folderPath,datasetDB,i));

            t.start();
            threads.add(t);
        }

        for(Thread thread: threads){
            thread.join();
        }
    }

    /*
    *
    * Parallelizza il calcolo e la scrittura delle similarità sul database
    *
    * */
    public class MyRunnable implements Runnable{

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
                Class.forName("org.sqlite.JDBC");
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
                        long elapsedTime = (startTime - System.currentTimeMillis())/1000;
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

        public MyRunnable(Integer slice, Integer start, String folderPath, String datasetDB, Integer numThread) {
            this.slice = slice;
            this.start = start;
            this.folderPath = folderPath;
            this.datasetDB = datasetDB;
            this.numThread = numThread;
        }
    }

    public class DatasetRow {
        private String appname;
        private String crawl;
        private String state1;
        private String state2;

        public DatasetRow(String appname, String crawl, String state1, String state2) {
            this.appname = appname;
            this.crawl = crawl;
            this.state1 = state1;
            this.state2 = state2;
        }

        public String getAppname() {
            return appname;
        }

        public String getCrawl() {
            return crawl;
        }

        public String getState1() {
            return state1;
        }

        public String getState2() {
            return state2;
        }
    }
}

//private String folderPath = "F:\\Università\\Libri_università\\Magistrale\\Tesi_magistrale\\Web_Test_Generation\\Crawls_complete\\GroundTruthModels"; path per windows
//private String folderPath = "/Volumes/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/"; path MacOs
