package com.tool.data.goldstandard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Bin {

    private String binName;
    private List<State> states = new ArrayList<>();

    public Bin(String binName) {
        this.binName = binName;
    }

    public String getName() {
        return this.binName;
    }

    public void addState(String stateName, Date dateAdded) {
        State s = new State(stateName,dateAdded,this);
        states.add(s);
    }

    public List<State> getStates() {
        return this.states;
    }
}
