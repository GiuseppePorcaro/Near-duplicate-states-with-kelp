package com.tool.data;

import java.io.File;

//TODO: remove this class
public class SubjectSetDataset extends AnnotatedDataset {

	public SubjectSetDataset(String pathToDb,String pathToCrawls) {
		super(pathToDb,pathToCrawls);
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
		String basePath = "src/main/resources/GroundTruthModels/";
		//String filePath = basePath + appName + "/" + crawl + "/states/" + state + ".html";
		String filePath = basePath + appName + "/" + crawl + "/doms/" + state + ".html";
		return new File(filePath);
	}
}
