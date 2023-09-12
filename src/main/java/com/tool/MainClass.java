package com.tool;

import com.tool.dataset.ComputeStatisticsRunnable;
import com.tool.dataset.DatasetManager;
import com.tool.debug.DebugClass;

public class MainClass {

    public static void main(String args[]) throws Exception {

        /*Itera sui record del database -> calcola il kernel per ogni coppia -> per ora stampa solo il risultato del kernel*/
        /*String folderPath = "/run/media/giuseppeporcaro/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels";
        String datasetDB = "gs_full_dom_mu01.db";
        DatasetManager datasetManager = new DatasetManager(folderPath,datasetDB,97490,"similarities");
        datasetManager.computeDatasetFunction();*/

        /*Calcola il kernel per una singola coppia di html passati in input al programma*/
        //mainForScriptPython(args);

        DebugClass debugClass = new DebugClass();
        debugClass.start();
        //debugClass.debugTrees();
    }
}


/*
*
* -Per le coppie che sono classificate 1 del tipo stessa lista di oggetti, ma numero diverso di oggetti nella lista, con paramtetro mu=0.1 si riesce ad ottenere una maggiore
*       similarità se la profondità dell'albero è altra, mentre se è bassa la similarità rimane un po' bassa. Esempio addressbook state397 state532.
*
* -Ci sono coppi di pagine che hanno il DOM identico ad eccezione di un paio di tag <input> e un testo in un <div>. In questo esempio di pagine cambiava solo che un utente
*       era anomino, mentre nell'altra l'utente era un admin. Per questo motivo cambiava solo il testo "anonimus user" con "administrator"
*
* -Il style="display: none;" e style="display: hidden;" ci dicono anche quando due pagine identiche invece sono classificate come diverse. Serve un modo per recuperare
*       questa informazione e quindi stabilire che le due pagine sono diverse
*
*-Ci sono pagine identiche (classificate come diverse) ad eccezione che una inserisce un nuovo dato, mentre una
*       fa un update. Il minimo che può cambiare è il tipo di <form> col bottone <submit> e basta.
*
*
* **Si potrebbe pensare di applicare due livelli di calcolo? Il primo è su cose speficiche dell'albero, il secondo invece sulle coppie di nodi**
*
*
*
*
*
* */
