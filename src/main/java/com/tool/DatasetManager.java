package com.tool;

import java.sql.*;

/*
* Gestisce la lettura e la scrittura del dataset nel file ds.db
* Durante la scrittura, calcola anche la similarità tra gli html presenti nel file .db
* */
public class DatasetManager {

    private String dbPath = "/Volumes/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/gs.db";

    public void main(){

        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:"+dbPath);
            Statement stat = conn.createStatement();


            /*
            * Cose da fare per popolare il dataset:
            * 1) creare una nuova colonna per tipo di similarità nel file db
            * 2) Iterare sulla tabella nearduplicates: per ogni record prendere appname, state1, state2
            * 3) Ricreare il percorso per arrivare a state1.html e state2.html tramite appname, state1 e state2
            * 4) Presi gli html, calcolare il kernel normalizzato
            *
            * */
            ResultSet rs = stat.executeQuery("SELECT state1, state2, HUMAN_CLASSIFICATION from nearduplicates where appname=\"addressbook\" LIMIT 15;");

            while (rs.next()){
                System.out.println(rs.getString("state1")+" "+rs.getString("state2")+" "+rs.getString("human_classification"));
            }

            rs.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
