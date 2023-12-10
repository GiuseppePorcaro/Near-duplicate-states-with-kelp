package com.tool;

import com.tool.dataset.ComputeStatisticsRunnable;
import com.tool.dataset.DatasetManager;
import com.tool.debug.DebugClass;

public class MainClass {

    public static void main(String args[]) throws Exception {

        /*Itera sui record del database -> calcola il kernel per ogni coppia -> per ora stampa solo il risultato del kernel*/
        /*String folderPath = "/run/media/giuseppeporcaro/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels";
        String datasetDB = "/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/data/gs_full_dom_mu01_kernels_StarDomRepresentation.db";
        DatasetManager datasetManager = new DatasetManager(folderPath,datasetDB,100,"debugKernels");
        datasetManager.computeDatasetFunction();*/

        DebugClass debugClass = new DebugClass();
        debugClass.debugAttributeSimilarityVariants();
        //debugClass.debugStarRepresentation();
        //debugClass.debugKernels();
        //debugClass.debugTrees();
    }
}


/*
*
* IMPLEMENTATO -Per le coppie che sono classificate 1 del tipo stessa lista di oggetti, ma numero diverso di oggetti nella lista, con paramtetro mu=0.1 si
*       riesce ad ottenere una maggiore similarità se la profondità dell'albero è altra, mentre se è bassa la similarità rimane un po' bassa.
*       Esempio addressbook state397 state532.
*
* -Ci sono coppie di pagine che hanno il DOM identico ad eccezione di un paio di tag <input> e un testo in un <div>. In questo esempio di pagine cambiava solo che un utente
*       era anomino, mentre nell'altra l'utente era un admin. Per questo motivo cambiava solo il testo "anonimus user" con "administrator"
*
* IMPLEMENTATO -Il style="display: none;" e style="display: hidden;" ci dicono anche quando due pagine identiche invece sono classificate come diverse.
*       Serve un modo per recuperare questa informazione e quindi stabilire che le due pagine sono diverse
*
* -Ci sono pagine identiche (classificate come diverse) ad eccezione che una inserisce un nuovo dato, mentre una
*       fa un update. Il minimo che può cambiare è il tipo di <form> col bottone <submit> e basta.
*
* -Ci sono pagine i cui DOM sono molto diversi, attribute_sim è molto alto e human classification = 2:
*       1) Le pagine presentano molte date scritte che variano solo per i giorni/mesi/anni. | NON SEMBRA ESSERE RESPONSABILE DELLA MAIN FEATURE
*       2) Molti tag <a> che hanno href che fanno riferimento a file .php che variano solo per i valori degli attributi passati, cioè i valori delle date | NON SEMBRA ESSERE RESPONSABILE DELLA MAIN FEATURE
*       3) per alti valori di MU sembra abbassarsi la sim per questo tipo di coppia (probabilmente perchè più si scende più i tag simili vegnono ripetuti, dando alta sim)
*
*
*
*  -Pagine identiche (attribute_sim >= 1), ma classificate come 2:
*       1) pagekit/crawl-pagekit-60min/states/state688 - pagekit/crawl-pagekit-60min/states/state693 (cambiano solo i minuti in 3 date, il resto identico)
*       2) pagekit/crawl-pagekit-60min/states/state688 - pagekit/crawl-pagekit-60min/states/state833 (come 1) )
*       3) pagekit/crawl-pagekit-60min/states/state693 - pagekit/crawl-pagekit-60min/states/state759 (tutto identico tranne il class di soli due <div>, i quali sono diversi)
*       4) phoenix/crawl-phoenix-60min/states/state495 - /phoenix/crawl-phoenix-60min/states/state511 (lista di oggetti omogenei identica, ma in una pag c'è un singolo
*               form in più nella lista per aggiungere un nuovo elemento)
*
*
*
*
*
*
*
* */
