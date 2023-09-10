package com.tool.dataset;

import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;

import javax.swing.plaf.IconUIResource;
import java.util.ArrayList;
import java.util.List;

public class ComputeStatisticsRunnable implements Runnable{

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
    }

    /*
    * Sono tutte da testare
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

        float result[] = new float[2];
        computeBranchingFactors(root,result);

        if(result[1] == 0){
            return 0.0f;
        }
        return result[0]/result[1]; //result[0] è la somma dei figli, result[1] è il numero di nodi
    }

    public float getDensity(TreeNode root){
        if(root == null){
            return 0;
        }

        int numNodes = getNumNodes(root);
        int height = getTreeHeight(root, 1);
        int maxNodes = (int) Math.pow(height+1,2)-1;

        return numNodes/maxNodes;
    }

    public void computeBranchingFactors(TreeNode node, float result[]){
        if(node != null){
            result[0] = node.getChildren().size();
            result[1]++;

            for(TreeNode children: node.getChildren()){
                computeBranchingFactors(children,result);
            }
        }
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
