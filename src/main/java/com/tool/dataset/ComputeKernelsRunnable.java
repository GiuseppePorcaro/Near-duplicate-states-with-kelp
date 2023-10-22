package com.tool.dataset;

import com.tool.NormalizationKernels.NormalizationPartialTreeKernel;
import com.tool.NormalizationKernels.NormalizationSubSetTreeKernel;
import com.tool.NormalizationKernels.NormalizationSubTreeKernel;
import com.tool.Trees.TreeFactory;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.kernel.tree.PartialTreeKernel;
import it.uniroma2.sag.kelp.kernel.tree.SubSetTreeKernel;
import it.uniroma2.sag.kelp.kernel.tree.SubTreeKernel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.tool.representations.ManageTreeRepresentation.popolateTree;

public class ComputeKernelsRunnable implements Runnable{

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
            DriverManager.setLoginTimeout(10);
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
                NormalizationSubTreeKernel normalizationSubTreeKernel = new NormalizationSubTreeKernel(new SubTreeKernel());
                NormalizationSubSetTreeKernel normalizationSubSetTreeKernel = new NormalizationSubSetTreeKernel(new SubSetTreeKernel());
                NormalizationPartialTreeKernel normalizationPartialTreeKernel = new NormalizationPartialTreeKernel(new PartialTreeKernel());


                String appName = entity.getAppname();
                String crawl = entity.getCrawl();
                String state1 = entity.getState1();
                String state2 = entity.getState2();

                String pathHTML1 = folderPath+"/"+appName+"/"+crawl+"/doms/"+state1+".html";
                String pathHTML2 = folderPath+"/"+appName+"/"+crawl+"/doms/"+state2+".html";

                TreeRepresentation firstTree = popolateTree(TreeFactory.createTree(pathHTML1,"all"));
                TreeRepresentation secondTree = popolateTree(TreeFactory.createTree(pathHTML2,"all"));

                float subTreeKernelResult = normalizationSubTreeKernel.kernelComputation(firstTree,secondTree);
                float subSetTreeKernelResult = normalizationSubSetTreeKernel.kernelComputation(firstTree,secondTree);
                float partialTreeKernelResult = normalizationPartialTreeKernel.kernelComputation(firstTree,secondTree);


                if(counter % 10 == 0){
                    long elapsedTime = (System.currentTimeMillis() - startTime)/1000;
                    System.out.println("Thread: "+numThread+" | seconds: "+elapsedTime);
                }

                synchronized (lock){
                    DriverManager.setLoginTimeout(10);
                    Connection connUpdate = DriverManager.getConnection("jdbc:sqlite:"+folderPath+"/"+ datasetDB);
                    connUpdate.setAutoCommit(true);

                    String queryUpdatePartialTreeKernel = "UPDATE nearduplicates SET PARTIAL_TREE_KERNEL=? where appname=? and crawl=? and state1=? and state2=?;";
                    String queryUpdateSubTreeKernel = "UPDATE nearduplicates SET SUB_TREE_KERNEL=? where appname=? and crawl=? and state1=? and state2=?;";
                    String queryUpdateSubSetTreeKernel = "UPDATE nearduplicates SET SUB_SET_TREE_KERNEL=? where appname=? and crawl=? and state1=? and state2=?;";

                    updateKernelColumn(connUpdate, queryUpdatePartialTreeKernel, appName, crawl, state1, state2,partialTreeKernelResult);
                    updateKernelColumn(connUpdate, queryUpdateSubTreeKernel, appName, crawl, state1, state2,subTreeKernelResult);
                    updateKernelColumn(connUpdate, queryUpdateSubSetTreeKernel, appName, crawl, state1, state2,subSetTreeKernelResult);

                    connUpdate.close();

                    counter++;
                    String format = "%-40s%s%n";
                    System.out.printf(format,appName+" "+crawl+" "+state1+" "+state2+" - "+partialTreeKernelResult+" "+ subTreeKernelResult+" "+subSetTreeKernelResult,"| Thread: "+numThread+ " - Pair N°:  "+counter);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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

    public ComputeKernelsRunnable(Integer slice, Integer start, String folderPath, String datasetDB, Integer numThread) {
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
