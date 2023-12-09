package com.tool.dataset;

import com.tool.data.dom.DomRepresentation;
import com.tool.data.dom.io.WebpageReader;
import com.tool.experiments.data.AnnotatedDataset;
import com.tool.similarity.AttributeSimilarity;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.example.SimpleExample;
import it.uniroma2.sag.kelp.kernel.tree.SmoothedPartialTreeKernel;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComputeAttrSimilarityStarDomReprRunnable implements Runnable{

    private static Integer counter = 0;
    private final Object lock = new Object();
    private final Object lockUpdate = new Object();
    private Integer numThread;
    private Integer slice;
    private Integer start;
    private String folderPath;
    private String datasetDB;
    AnnotatedDataset dataset;
    WebpageReader wpReader;

    @Override
    public void run() {

        long startTime = System.currentTimeMillis();
        System.out.println("Thread: "+numThread+"start: "+start+" - slice: "+slice);
        try {
            DriverManager.setLoginTimeout(300);
            Connection conn = DriverManager.getConnection("jdbc:sqlite:"+ datasetDB);
            Statement stat = conn.createStatement();

            ResultSet rs = stat.executeQuery("SELECT appname, crawl,state1, state2 from nearduplicates order by appname, crawl, state1, state2 limit "+start+","+slice+";");

            List<DatasetRow> entities = new ArrayList<>();

            while(rs.next()){
                entities.add(new DatasetRow(rs.getString("appname"),rs.getString("crawl"),rs.getString("state1"),rs.getString("state2")));
            }

            rs.close();
            conn.close();

            for(DatasetRow entity: entities){

                String appName = entity.getAppname();
                String crawl = entity.getCrawl();
                String state1 = entity.getState1();
                String state2 = entity.getState2();

                Example first  = new SimpleExample(), second = new SimpleExample();

                File file1 = dataset.getHtmlFile(appName, crawl, state1);
                File file2 = dataset.getHtmlFile(appName, crawl, state2);

                DomRepresentation page1 = wpReader.getDOMRepresentationFromFile(file1);
                DomRepresentation page2 = wpReader.getDOMRepresentationFromFile(file2);
                first.addRepresentation("DOM-TREE", page1);
                second.addRepresentation("DOM-TREE", page2);

                float kernel = computeAttrSim(first, second);

                synchronized (lock){

                    if(counter % 100 == 0){
                        long elapsedTime = (System.currentTimeMillis() - startTime)/1000;
                        System.out.println("Thread: "+numThread+" | seconds: "+elapsedTime+ " | Pair N°:  "+counter);
                    }

                    DriverManager.setLoginTimeout(300);
                    Connection connUpdate = DriverManager.getConnection("jdbc:sqlite:"+ datasetDB);
                    connUpdate.setAutoCommit(true);

                    String queryUpdate = "UPDATE nearduplicates SET ATTRIBUTE_SIM_DOMTREE_REP=? where appname=? and crawl=? and state1=? and state2=?;";

                        updateKernelColumn(connUpdate,queryUpdate,appName,crawl,state1,state2,kernel);

                    connUpdate.close();

                    counter++;
                    String format = "%-40s%s%n";
                    System.out.printf(format,appName+" "+crawl+" "+state1+" "+state2+" - "+kernel,"| Thread: "+numThread+ " - Pair N°:  "+counter);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private float computeAttrSim(Example first, Example second){
        SmoothedPartialTreeKernel smptk = new SmoothedPartialTreeKernel(0.4f,0.1f,1f,0.01f,new AttributeSimilarity(),"DOM-TREE");
        it.uniroma2.sag.kelp.kernel.standard.NormalizationKernel nsmptk = new it.uniroma2.sag.kelp.kernel.standard.NormalizationKernel(smptk);
        return nsmptk.innerProduct(first,second);
    }

    private static void updateKernelColumn(Connection connUpdate, String query, String appName, String crawl, String state1, String state2, float kernel) throws SQLException {

        PreparedStatement statement = connUpdate.prepareStatement(query);
        statement.setFloat(1, kernel);
        statement.setString(2, appName);
        statement.setString(3, crawl);
        statement.setString(4, state1);
        statement.setString(5, state2);

        statement.executeUpdate();

        statement.close();
    }

    public ComputeAttrSimilarityStarDomReprRunnable(Integer slice, Integer start, String folderPath, String datasetDB, Integer numThread) {
        this.slice = slice;
        this.start = start;
        this.folderPath = folderPath;
        this.datasetDB = datasetDB;
        this.numThread = numThread;
        dataset = new AnnotatedDataset(null,folderPath);
        wpReader = new WebpageReader(WebpageReader.REPRESENT_DOM_ONLY_BODY_NO_SCRIPTS);
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
