package com.tool.dataset;

import com.tool.Trees.Tree;
import com.tool.Utils;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;

import javax.swing.plaf.IconUIResource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        /*
        *
        * Possibili statistiche da recuperare e magari usare come parametro per il classificatore:
        * 1) "Fan in" e "Fan out" delle pagine. Sembra che per pagine molto simili, ma classificate diverse abbiano numero diverso di fan in e fan out. Queste
        *       sono semplicemente il numero di stati da cui può essere generato e il numero di stati che può raggiungere tramite gli eventi (Da controllare meglio)
        * 2) NUmero di nodi del tree. C'è nella tabella states del db
        * 3) Branching factor
        * 4) Altezza dell'albero
        * 5) Densità
        * 6) Grado dell'albero
        *
        * */

        /*
        * Devo creare il database dove vado a mettere tutte le statistiche
        * */
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:"+folderPath+"/"+ datasetDB);
            Statement stat = conn.createStatement();

            ResultSet rs = stat.executeQuery("SELECT appname, crawl, state, nodeSize from states order by appname, crawl, state limit "+start+","+slice+";");


            while(rs.next()){

                String appName = rs.getString("appname");
                String crawl = rs.getString("crawl");
                String state = rs.getString("state");
                String numNodes = rs.getString("nodeSize");

                String datasetDBDest = "";
                synchronized (lock){
                    DriverManager.setLoginTimeout(10);
                    Connection connUpdate = DriverManager.getConnection("jdbc:sqlite:"+folderPath+"/"+ datasetDBDest);
                    connUpdate.setAutoCommit(true);

                    String query = "UPDATE nearduplicates SET where appname=? and crawl=? and state1=? and state2=?;";
                    PreparedStatement statement = connUpdate.prepareStatement(query);
                    /*
                     * Fare updates del db
                     * */

                    statement.executeUpdate();

                    statement.close();
                    connUpdate.close();
                }
            }

            rs.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /*
    * Sono tutte da testare.
    *
    * Forse da spostare in ManageTreeRepresentation
    * */
    public int getTreeHeight(TreeNode node, int profondità){
        if(node == null){
            return 0;
        }

        int maxProfondità = 0;
        List<Integer> listChildrenProfondità = new ArrayList<>();

        if(!node.hasChildren()){
            return profondità;
        }else {
            profondità++;
            for(TreeNode child: node.getChildren()){
                listChildrenProfondità.add(getTreeHeight(child, profondità));
            }
            for(Integer p: listChildrenProfondità){
                if(p > maxProfondità){
                    maxProfondità = p;
                }
            }return maxProfondità;
        }
    }

    public float getAverageBranchingFactor(TreeNode root){
        if(root == null){
            return 0.0f;
        }

        //Numero di nodi non root / numero di nodi non leaf
        float numNodesNonRoot = getNumNodes(root)-1;
        float numNodesNonLeaf = getNumNodesNonLeaf(root);

        if(numNodesNonLeaf == 0){
            return 0.0f;
        }
        return numNodesNonRoot/numNodesNonLeaf;
    }

    public float getNumNodesNonLeaf(TreeNode node){
        StructureElement s = node.getContent();
        if(!node.hasChildren()){
            return 0;
        }

        float result = 1;
        for(TreeNode child: node.getChildren()){
            result = result + getNumNodesNonLeaf(child);
        }

        return result;
    }

    public int getTreeDegree(TreeNode node){
        if(!node.hasChildren()){
            return 0;
        }
        int max = node.getChildren().size();

        List<Integer> listNumChildren = new ArrayList<>();
        for(TreeNode child: node.getChildren()){
            listNumChildren.add(getTreeDegree(child));
        }
        for(Integer num: listNumChildren){
            if(num > max){
                max = num;
            }
        }return max;

    }

    public float getDensity(TreeNode root){
        if(root == null){
            return 0;
        }

        int numNodes = getNumNodes(root);
        int height = getTreeHeight(root, 1);
        int maxNodes = (int) Math.pow(height+1,2)-1;

        return (float) numNodes /maxNodes;
    }



    /*Il numero di nodi lo tengo già, quindi probabilmente questa funzione non serve*/
    public int getNumNodes(TreeNode node){
        if(node == null){
            return 0;
        }

        int conter = 1;
        for(TreeNode children: node.getChildren()){
            conter += getNumNodes(children);
        }
        return conter;
    }
}
