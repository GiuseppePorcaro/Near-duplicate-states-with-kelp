package com.tool.data.goldstandard;

import java.util.Date;

public class State {

    private String stateName;
    private Bin bin;
    private Date timeAdded;

    public State(String stateName, Date dateAdded, Bin bin) {
        this.stateName = stateName;
        this.timeAdded = dateAdded;
    }

    public String getName() {
        return this.stateName;
    }

    public Date getTimeAdded() {
        return this.timeAdded;
    }
}
