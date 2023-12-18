package com.tool.dataset;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;


/*
* Gestisce la lettura e la scrittura del dataset nel file ds.db
* Durante la scrittura, calcola anche la similarità tra gli html presenti nel file .db
* */

public class DatasetManager {

    private String folderPath;
    private String datasetDB;
    String selectQuery = "";
    String updateQuery = "";
    private int numRows;
    private String action;

    public DatasetManager(String folderPath, String datasetDB, int numRows, String action) {
        this.folderPath = folderPath;
        this.datasetDB = datasetDB;
        this.numRows = numRows;
        this.action = action;
    }

    public void computeDatasetFunction() throws InterruptedException {

        int numCores = Runtime.getRuntime().availableProcessors();
        int slice = round(numRows/numCores) + 1;
        int start = 0;

        List<Thread> threads = new ArrayList<>();

        for(int i = 0; i < numCores; i++){
            start = i*slice;
            Thread t = new Thread(runnableFactory(action,slice, start,i));
            //System.out.println(start+" "+slice);
            t.start();
            threads.add(t);
        }

        for(Thread thread: threads){
            thread.join();
        }
    }

    public Runnable runnableFactory(String action,int slice, int start,int i){
        switch (action){
            case "statistics":
                return new ComputeStatisticsRunnable(i,slice,start,folderPath,datasetDB);
            case "similarities":
                return new ComputeSimilaritiesRunnable(slice,start,folderPath,datasetDB,i);
            case "kernels":
                return new ComputeKernelsRunnable(slice, start, folderPath, datasetDB,i);
            case "kernelsStar":
                return new ComputeKernelsStarRunnable(slice, start, folderPath, datasetDB,i);
            case "similaritiesStarDomRepr":
                return new ComputeAttrSimilarityStarDomReprRunnable(slice, start, folderPath, datasetDB,i);
            case "debugKernels":
                return new DebugStarKernelRunnable(slice, start, folderPath, datasetDB,i);
            case "weightedSimilarity":
                return new ComputeWeightedSimilarityRunnable(slice, start, folderPath, datasetDB,i);
            case "weightedRefinedSimilarity":
                return new ComputeWeightedRefinedSimilarityRunnable(slice, start,folderPath,datasetDB,i);
            default:
                return null;
        }
    }

}

//private String folderPath = "F:\\Università\\Libri_università\\Magistrale\\Tesi_magistrale\\Web_Test_Generation\\Crawls_complete\\GroundTruthModels"; path per windows
//private String folderPath = "/Volumes/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/"; path MacOs
