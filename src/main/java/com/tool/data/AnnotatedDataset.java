package com.tool.data;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for a generic annotated dataset from the work of Yandrapally et Al.
 * 
 * Offers basic file-access and sqlite database interfaces.
 * 
 * @author Luigi Libero Lucio Starace
 *
 */
public class AnnotatedDataset {
	
	String pathToDb;
	String pathToCrawls;

	/**
	 *
	 * @param pathToDb path to SQLite database file
	 * @param pathToCrawls path to directory containing the associated crawls (must not terminate with '/')
	 */
	public AnnotatedDataset(String pathToDb, String pathToCrawls) {
		this.pathToDb = pathToDb;
		this.pathToCrawls = pathToCrawls;
	}

	/**
	 * Returns all the annotated data entries in the GoldenStandard dataset.
	 * 
	 * @return a List of Dataentry objects, each representing an annotated dataset entry.
	 */
	public List<DatasetEntry> getAllAnnotatedEntries() {
		List<DatasetEntry> result = new ArrayList<DatasetEntry>();
		try {
			Class.forName("org.sqlite.JDBC");
			Connection database = DriverManager.getConnection("jdbc:sqlite:" + pathToDb);
			Statement s = database.createStatement();
			ResultSet rs = s.executeQuery("select * from nearduplicates where HUMAN_CLASSIFICATION<>-1");
			// for each annotated entry in GroundTruth
			while(rs.next()) {
				DatasetEntry d = new DatasetEntry(rs);
				result.add(d);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Returns a File object for the html file corresponding to 
	 * state <code>state</code> in the crawl <code>crawl</code>
	 * for the web app <code>appName</code>
	 * 
	 * @param appName String representing the name of the app
	 * @param crawl String representing the name of the crawl
	 * @param state String representing the name of a given state in the crawl
	 * @return a File object for the html file corresponding to <code>state</code>
	 */
	public File getHtmlFile(String appName, String crawl, String state) {
		String basePath = this.pathToCrawls;
		//String filePath = basePath + appName + "/" + crawl + "/states/" + state + ".html";
		String filePath = basePath + "/" + appName + "/" + crawl + "/doms/" + state + ".html";
		return new File(filePath);
	}
}
