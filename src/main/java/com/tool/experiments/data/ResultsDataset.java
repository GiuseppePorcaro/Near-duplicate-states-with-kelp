package com.tool.experiments.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class ResultsDataset {
	
	private String TABLE_NAME = "results";
	Connection resultsDatabase;
	
	public ResultsDataset(String pathToDb,String tableName) throws SQLException {
		resultsDatabase = DriverManager.getConnection("jdbc:sqlite:"+pathToDb);
		resultsDatabase.setAutoCommit(false);
		this.TABLE_NAME = tableName;
	}
	
	public void cleanUp() throws ClassNotFoundException, SQLException {

		Class.forName("org.sqlite.JDBC");
		Statement s = resultsDatabase.createStatement();
		s.executeUpdate("drop table if exists "+TABLE_NAME);
		s.executeUpdate("CREATE TABLE "+TABLE_NAME+" ("
				+ "appname text,"
				+ "crawl text,"
				+ "state1 text,"
				+ "state2 text,"
				+ "DOM_RTED real,"
				+ "DOM_Levenshtein real,"
				+ "DOM_contentHash real,"
				+ "DOM_SIMHASH real,"
				+ "VISUAL_BlockHash real,"
				+ "VISUAL_PHash real,"
				+ "VISUAL_Hyst real,"
				+ "VISUAL_PDiff real,"
				+ "VISUAL_SIFT real,"
				+ "VISUAL_SSIM real,"
				+ "HUMAN_CLASSIFICATION int,"
				+ "TAGS text,"
				+ "treeKernel text,"
				+ "domRepresentation text,"
				+ "kernelValue real,"
				+ "sizeState1 int,"
				+ "sizeState2 int,"
				+ "PRIMARY KEY(appname, crawl, state1, state2, treeKernel, domRepresentation)"
				+ ")");
	}
	
	public void saveDatasetEntry(DatasetEntry e) throws SQLException {
		PreparedStatement ps = resultsDatabase.prepareStatement("INSERT INTO "+TABLE_NAME+""
				+ "(appname,crawl,state1,state2,DOM_RTED,DOM_Levenshtein,DOM_contentHash,"
				+ "DOM_SIMHASH,VISUAL_BlockHash,VISUAL_PHash,VISUAL_Hyst,VISUAL_PDiff,"
				+ "VISUAL_SIFT,VISUAL_SSIM,HUMAN_CLASSIFICATION,TAGS,treeKernel,domRepresentation,kernelValue,sizeState1,sizeState2) VALUES("
				+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"
				+ ")");
		
		int parameterIndex = 1;
		ps.setString(parameterIndex++, e.getAppName());
		ps.setString(parameterIndex++, e.getCrawl());
		ps.setString(parameterIndex++, e.getState1());
		ps.setString(parameterIndex++, e.getState2());
		ps.setDouble(parameterIndex++, e.getDomRted());
		ps.setDouble(parameterIndex++, e.getDOM_Levenshtein());
		ps.setDouble(parameterIndex++, e.getDOM_contentHash());
		ps.setDouble(parameterIndex++, e.getDOM_SIMHASH());
		ps.setDouble(parameterIndex++, e.getVISUAL_BlockHash());
		ps.setDouble(parameterIndex++, e.getVISUAL_PHash());
		ps.setDouble(parameterIndex++, e.getVISUAL_Hyst());
		ps.setDouble(parameterIndex++, e.getVISUAL_PDiff());
		ps.setDouble(parameterIndex++, e.getVISUAL_SIFT());
		ps.setDouble(parameterIndex++, e.getVISUAL_SSIM());
		ps.setInt(parameterIndex++,    e.getHUMAN_CLASSIFICATION());
		ps.setString(parameterIndex++, e.getTAGS());
		ps.setString(parameterIndex++, e.getTreeKernel());
		ps.setString(parameterIndex++, e.getDomRepresentation());
		ps.setDouble(parameterIndex++, e.getKernelValue());
		ps.setInt(parameterIndex++,    e.getSizeState1());
		ps.setInt(parameterIndex++,    e.getSizeState2());
		
		ps.executeUpdate();
	}
	
	public void commit() throws SQLException {
		resultsDatabase.commit();
	}
	
}
