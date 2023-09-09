package com.tool.dataset;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;


/*
* Gestisce la lettura e la scrittura del dataset nel file ds.db
* Durante la scrittura, calcola anche la similarità tra gli html presenti nel file .db
* */

public class DatasetManager {

    public void computeDatasetSimilarities() throws InterruptedException {

        int numCores = Runtime.getRuntime().availableProcessors();
        int numRows = 97490;
        int slice = round(numRows/numCores) + 1;
        int start = 0;
        String folderPath = "/run/media/giuseppeporcaro/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels";
        String datasetDB = "gs_full_dom.db";

        List<Thread> threads = new ArrayList<>();

        for(int i = 0; i < numCores; i++){
            start = i*slice;
            Thread t = new Thread(new ComputeSimilaritiesRunnable(slice,start,folderPath,datasetDB,i));

            t.start();
            threads.add(t);
        }

        for(Thread thread: threads){
            thread.join();
        }
    }
}

//private String folderPath = "F:\\Università\\Libri_università\\Magistrale\\Tesi_magistrale\\Web_Test_Generation\\Crawls_complete\\GroundTruthModels"; path per windows
//private String folderPath = "/Volumes/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/"; path MacOs
