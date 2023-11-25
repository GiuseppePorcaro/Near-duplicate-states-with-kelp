package com.tool.dataset;

import com.tool.data.dom.DomRepresentation;
import com.tool.data.dom.io.WebpageReader;
import com.tool.experiments.data.AnnotatedDataset;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.example.SimpleExample;
import it.uniroma2.sag.kelp.kernel.standard.NormalizationKernel;
import it.uniroma2.sag.kelp.kernel.tree.PartialTreeKernel;
import it.uniroma2.sag.kelp.kernel.tree.SubSetTreeKernel;
import it.uniroma2.sag.kelp.kernel.tree.SubTreeKernel;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DebugStarKernelRunnable implements Runnable{

    private static Integer counter = 0;
    private final Object lock = new Object();
    private final Object lockUpdate = new Object();
    private Integer numThread;
    private Integer slice;
    private Integer start;
    private String folderPath;
    private String datasetDB;
    AnnotatedDataset dataset;


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

                WebpageReader wpReader = new WebpageReader(WebpageReader.REPRESENT_DOM_AS_IS);
                DomRepresentation page1 = wpReader.getDOMRepresentationFromFile(file1);
                DomRepresentation page2 = wpReader.getDOMRepresentationFromFile(file2);
                first.addRepresentation("DOM-TREE", page1);
                second.addRepresentation("DOM-TREE", page2);

                float subTreeKernelResult = computeSubTreeKernelNorm(first, second);
                //float subSetTreeKernelResult = computeSubSetTreeKernelNorm(first, second);
                //float partialTreeKernelResult = computePartialTreeKernelNorm(first, second);

                System.out.printf(appName+" "+crawl+" "+state1+" "+state2+" - "+subTreeKernelResult+"\n");

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private float computeSubTreeKernelNorm(Example first, Example second){
        SubTreeKernel stk = new SubTreeKernel("DOM-TREE");
        NormalizationKernel nstk = new NormalizationKernel(stk);
        return nstk.innerProduct(first, second);
    }

    private float computeSubSetTreeKernelNorm(Example first,Example second){
        SubSetTreeKernel sstkl = new SubSetTreeKernel("DOM-TREE");
        sstkl.setIncludeLeaves(true);
        NormalizationKernel nsstkl = new NormalizationKernel(sstkl);
        return nsstkl.innerProduct(first,second);
    }

    private float computePartialTreeKernelNorm(Example first,Example second){
        PartialTreeKernel ptk2 = new PartialTreeKernel("DOM-TREE");
        ptk2.setMu(0.1F);
        NormalizationKernel nptk2 = new NormalizationKernel(ptk2);
        return nptk2.innerProduct(first,second);
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

    public DebugStarKernelRunnable(Integer slice, Integer start, String folderPath, String datasetDB, Integer numThread) {
        this.slice = slice;
        this.start = start;
        this.folderPath = folderPath;
        this.datasetDB = datasetDB;
        this.numThread = numThread;
        dataset = new AnnotatedDataset("data-input/DS.db",folderPath);
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
