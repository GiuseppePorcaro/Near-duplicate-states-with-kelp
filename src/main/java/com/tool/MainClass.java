package com.tool;

import com.tool.Trees.TreeFactory;
import com.tool.debug.DebugClass;
import com.tool.similarity.AllAttributesJaccardSimilarity;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class MainClass {

    public static void main(String args[]) throws Exception {

        /*Itera sui record del database -> calcola il kernel per ogni coppia -> per ora stampa solo il risultato del kernel*/
        DatasetManager datasetManager = new DatasetManager();
        datasetManager.main();

        /*Calcola il kernel per una singola coppia di html passati in input al programma*/
        //mainForScriptPython(args);

        /*DebugClass debugClass = new DebugClass();
        debugClass.start();*/
    }
}
