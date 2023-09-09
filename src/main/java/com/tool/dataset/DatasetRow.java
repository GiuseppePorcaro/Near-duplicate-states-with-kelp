package com.tool.dataset;

public class DatasetRow {
    private String appname;
    private String crawl;
    private String state1;
    private String state2;

    public DatasetRow(String appname, String crawl, String state1, String state2) {
        this.appname = appname;
        this.crawl = crawl;
        this.state1 = state1;
        this.state2 = state2;
    }
    public String getAppname() {
        return appname;
    }
    public String getCrawl() {
        return crawl;
    }
    public String getState1() {
        return state1;
    }
    public String getState2() {
        return state2;
    }
}
