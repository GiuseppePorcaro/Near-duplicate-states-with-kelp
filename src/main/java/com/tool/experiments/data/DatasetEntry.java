package com.tool.experiments.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatasetEntry {
	private String appName;
	private String crawl;
	private String state1;
	private String state2;
	private double DomRted;
	private double DOM_Levenshtein;
	private double DOM_contentHash;
	private double DOM_SIMHASH;
	private double VISUAL_BlockHash;
	private double VISUAL_PHash;
	private double VISUAL_Hyst;
	private double VISUAL_PDiff;
	private double VISUAL_SIFT;
	private double VISUAL_SSIM;
	private int HUMAN_CLASSIFICATION;
	private String TAGS;
	private String treeKernel;
	private String domRepresentation;
	private double kernelValue;
	private int sizeState1;
	private int sizeState2;
	
	// constructors
	public DatasetEntry() {}
	
	public DatasetEntry(ResultSet rs) throws SQLException {
		appName = rs.getString("appname");
		crawl   = rs.getString("crawl");
		state1  = rs.getString("state1");
		state2  = rs.getString("state2");
		DomRted = rs.getDouble("DOM_RTED");
		DOM_Levenshtein = rs.getDouble("DOM_Levenshtein");
		DOM_contentHash = rs.getDouble("DOM_contentHash");
		DOM_SIMHASH = rs.getDouble("DOM_SIMHASH");
		VISUAL_BlockHash = rs.getDouble("VISUAL_BlockHash");
		VISUAL_PHash = rs.getDouble("VISUAL_PHash");
		VISUAL_Hyst = rs.getDouble("VISUAL_Hyst");
		VISUAL_PDiff = rs.getDouble("VISUAL_PDiff");
		VISUAL_SIFT = rs.getDouble("VISUAL_SIFT");
		VISUAL_SSIM = rs.getDouble("VISUAL_SSIM");
		HUMAN_CLASSIFICATION = rs.getInt("HUMAN_CLASSIFICATION");
		String TAGS = rs.getString("TAGS");
	}
	
	public boolean isClone() {
		return HUMAN_CLASSIFICATION == 0;
	}
	
	public boolean isNearDuplicate() {
		return HUMAN_CLASSIFICATION == 1;
	}
	
	public boolean isDistinct() {
		return HUMAN_CLASSIFICATION == 2;
	}
	
	// getters and setters
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getCrawl() {
		return crawl;
	}
	public void setCrawl(String crawl) {
		this.crawl = crawl;
	}
	public String getState1() {
		return state1;
	}
	public void setState1(String state1) {
		this.state1 = state1;
	}
	public String getState2() {
		return state2;
	}
	public void setState2(String state2) {
		this.state2 = state2;
	}
	public double getDomRted() {
		return DomRted;
	}
	public void setDomRted(double domRted) {
		DomRted = domRted;
	}
	public double getDOM_Levenshtein() {
		return DOM_Levenshtein;
	}
	public void setDOM_Levenshtein(double dOM_Levenshtein) {
		DOM_Levenshtein = dOM_Levenshtein;
	}
	public double getDOM_contentHash() {
		return DOM_contentHash;
	}
	public void setDOM_contentHash(double dOM_contentHash) {
		DOM_contentHash = dOM_contentHash;
	}
	public double getDOM_SIMHASH() {
		return DOM_SIMHASH;
	}
	public void setDOM_SIMHASH(double dOM_SIMHASH) {
		DOM_SIMHASH = dOM_SIMHASH;
	}
	public double getVISUAL_BlockHash() {
		return VISUAL_BlockHash;
	}
	public void setVISUAL_BlockHash(double vISUAL_BlockHash) {
		VISUAL_BlockHash = vISUAL_BlockHash;
	}
	public double getVISUAL_PHash() {
		return VISUAL_PHash;
	}
	public void setVISUAL_PHash(double vISUAL_PHash) {
		VISUAL_PHash = vISUAL_PHash;
	}
	public double getVISUAL_Hyst() {
		return VISUAL_Hyst;
	}
	public void setVISUAL_Hyst(double vISUAL_Hyst) {
		VISUAL_Hyst = vISUAL_Hyst;
	}
	public double getVISUAL_PDiff() {
		return VISUAL_PDiff;
	}
	public void setVISUAL_PDiff(double vISUAL_PDiff) {
		VISUAL_PDiff = vISUAL_PDiff;
	}
	public double getVISUAL_SIFT() {
		return VISUAL_SIFT;
	}
	public void setVISUAL_SIFT(double vISUAL_SIFT) {
		VISUAL_SIFT = vISUAL_SIFT;
	}
	public double getVISUAL_SSIM() {
		return VISUAL_SSIM;
	}
	public void setVISUAL_SSIM(double vISUAL_SSIM) {
		VISUAL_SSIM = vISUAL_SSIM;
	}
	public int getHUMAN_CLASSIFICATION() {
		return HUMAN_CLASSIFICATION;
	}
	public void setHUMAN_CLASSIFICATION(int hUMAN_CLASSIFICATION) {
		HUMAN_CLASSIFICATION = hUMAN_CLASSIFICATION;
	}
	public String getTAGS() {
		return TAGS;
	}
	public void setTAGS(String tAGS) {
		TAGS = tAGS;
	}
	
	public int getSizeState1() {
		return sizeState1;
	}

	public void setSizeState1(int sizeState1) {
		this.sizeState1 = sizeState1;
	}

	public int getSizeState2() {
		return sizeState2;
	}

	public void setSizeState2(int sizeState2) {
		this.sizeState2 = sizeState2;
	}

	public double getKernelValue() {
		return kernelValue;
	}

	public void setKernelValue(double kernelValue) {
		this.kernelValue = kernelValue;
	}

	public String getTreeKernel() {
		return treeKernel;
	}

	public void setTreeKernel(String treeKernel) {
		this.treeKernel = treeKernel;
	}

	public String getDomRepresentation() {
		return domRepresentation;
	}

	public void setDomRepresentation(String domRepresentation) {
		this.domRepresentation = domRepresentation;
	}
	
	
}
