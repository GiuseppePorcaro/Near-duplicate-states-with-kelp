package com.tool.dataset;

public class ComputeStatisticsRunnable implements Runnable{
    @Override
    public void run() {
        /*
        *
        * Possibili statistiche da recuperare e magari usare come parametro per il classificatore:
        * 1) "Fan in" e "Fan out" delle pagine. Sembra che per pagine molto simili, ma classificate diverse abbiano numero diverso di fan in e fan out. Queste
        *       sono semplicemente il numero di stati da cui può essere generato e il numero di stati che può raggiungere tramite gli eventi (Da controllare meglio)
        * 2) NUmero di nodi del tree
        * 3) Branching factor
        * 4) Altezza dell'albero
        * 5) Densità
        * 6) Grado dell'albero
        *
        * */
    }
}
