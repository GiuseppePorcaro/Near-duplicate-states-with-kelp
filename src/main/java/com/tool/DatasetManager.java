package com.tool;

import java.sql.*;

/*
* Gestisce la lettura e la scrittura del dataset nel file ds.db
* Durante la scrittura, calcola anche la similarità tra gli html presenti nel file .db
* */
public class DatasetManager {

    public void main(){

        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:/Volumes/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/gs.db");
            Statement stat = conn.createStatement();

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
