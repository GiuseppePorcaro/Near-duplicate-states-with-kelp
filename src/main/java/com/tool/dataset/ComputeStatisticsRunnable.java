package com.tool.dataset;

import com.tool.Trees.Tree;
import com.tool.Trees.TreeFactory;
import com.tool.Utils;
import com.tool.representations.ManageTreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;

import javax.swing.plaf.IconUIResource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.tool.representations.ManageTreeRepresentation.popolateTree;

public class ComputeStatisticsRunnable implements Runnable{

    private static Integer counter = 0;
    private final Object lock = new Object();
    private Integer numThread;
    private Integer slice;
    private Integer start;
    private String folderPath;
    private String datasetDB;

    public ComputeStatisticsRunnable(Integer numThread, Integer slice, Integer start, String folderPath, String datasetDB) {
        this.numThread = numThread;
        this.slice = slice;
        this.start = start;
        this.folderPath = folderPath;
        this.datasetDB = datasetDB;
    }

    @Override
    public void run() {

        System.out.println(start+" - "+slice+" - "+numThread);

        ManageTreeRepresentation manager = new ManageTreeRepresentation();

        try {
            ResultSet rs;
            Connection conn;

                conn = DriverManager.getConnection("jdbc:sqlite:"+folderPath+"/"+ datasetDB);
                Statement stat = conn.createStatement();

                rs = stat.executeQuery("SELECT appname, crawl, state, nodeSize from states order by appname, crawl, state limit "+start+","+slice+";");

            while(rs.next()){

                String appName = rs.getString("appname");
                String crawl = rs.getString("crawl");
                String state = rs.getString("state");
                int numNodes = rs.getInt("nodeSize");

                String pathHtml = folderPath+"/"+appName+"/"+crawl+"/doms/"+state+".html";
                String treeType = "all";

                TreeRepresentation tree = popolateTree(TreeFactory.createTree(pathHtml,treeType));
                TreeNode root = tree.getRoot();

                String datasetDBDest = "gs_stats.db";
                synchronized (lock){
                    DriverManager.setLoginTimeout(10);
                    Connection connUpdate = DriverManager.getConnection("jdbc:sqlite:"+folderPath+"/"+ datasetDBDest);
                    connUpdate.setAutoCommit(true);

                    String query = "INSERT INTO states(appname,crawl,state,numNodes,height,degree,averageBranchingFactor) VALUES(?,?,?,?,?,?,?)";
                    PreparedStatement statement = connUpdate.prepareStatement(query);
                    statement.setString(1,appName);
                    statement.setString(2,crawl);
                    statement.setString(3,state);
                    statement.setInt(4,numNodes);
                    statement.setInt(5,manager.getTreeHeight(root,1));
                    statement.setInt(6,manager.getTreeDegree(root));
                    statement.setFloat(7,manager.getAverageBranchingFactor(root,numNodes));

                    statement.executeUpdate();

                    statement.close();
                    connUpdate.close();

                    counter++;
                    String format = "%-100s%s%n";
                    System.out.printf(format, appName + " " + crawl + " " + state + " " + numNodes, " | Thread: " + numThread + " - Pair N°:  " + counter);

                }

            }

            rs.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
