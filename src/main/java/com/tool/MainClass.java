package com.tool;

import com.tool.dataset.DatasetManager;
import com.tool.debug.DebugClass;

public class MainClass {

    public static void main(String args[]) throws Exception {

        /*Itera sui record del database -> calcola il kernel per ogni coppia -> per ora stampa solo il risultato del kernel*/
        /*DatasetManager datasetManager = new DatasetManager();
        datasetManager.computeDatasetSimilarities();*/

        /*Calcola il kernel per una singola coppia di html passati in input al programma*/
        //mainForScriptPython(args);

        DebugClass debugClass = new DebugClass();
        debugClass.start();
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
* **Si potrebbe pensare di applicare due livelli di calcolo? Il primo è su cose speficiche dell'albero, il secondo invece sulle coppie di nodi**
*
*
*
*
*
* */
