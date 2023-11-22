package com.tool.data.goldstandard;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GoldStandardModel {

    String appName;
    List<Bin> bins = new ArrayList<>();

    public GoldStandardModel(String appName) throws IOException {
        this.appName = appName;
        String jsonFilePath = "data-input/GroundTruthModels/"+appName+"/crawl-"+appName+"-60min"+"/comp_output/classification_with_distances.json";
        File jsonFile = new File(jsonFilePath);

        String content;
        content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        JSONObject json = new JSONObject(content);
        Map<String, Object> map = json.toMap();
        Map<String, Object> statesMap = (Map<String, Object>) map.get("states");

        for(String key : statesMap.keySet()){
            Map<String,Object> stateDataMap = (Map<String, Object>) statesMap.get(key);
            Date dateAdded = new Date((long)stateDataMap.get("timeAdded"));
            String binName = (String)stateDataMap.get("bin");
            String stateName = key;
            this.addState(binName,stateName,dateAdded);
        }
    }

    private void addState(String binName, String stateName, Date dateAdded) {
        this.addBin(binName); //adds bin if does not already exist
        Bin b = getBinByName(binName);
        b.addState(stateName,dateAdded);
    }

    private Bin getBinByName(String binName) {
        for(Bin b : bins){
            if(b.getName().equals(binName)){
                return b;
            }
        }
        return null;
    }

    private void addBin(String binName) {
        for(Bin b : bins){
            if(b.getName().equals(binName)){
                return; //bin already exists, add nothing
            }
        }
        //no bin with the same name exists
        Bin bin = new Bin(binName);
        bins.add(bin);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Gold Standard Model for app: "+this.appName);
        sb.append(System.getProperty("line.separator"));
        sb.append("++++++++++++++++++++++++++++++++++++++++++++++++++++");
        sb.append(System.getProperty("line.separator"));
        sb.append("Number of bins: "+this.bins.size());
        sb.append(System.getProperty("line.separator"));
        sb.append("Number of states: "+this.getNumberOfStates());
        sb.append(System.getProperty("line.separator"));
        sb.append("++++++++++++++++++++++++++++++++++++++++++++++++++++");
        sb.append(System.getProperty("line.separator"));

        sb.append("Bins: ");
        for(Bin b : bins){
            sb.append(b.getName()+" ");
        }

        sb.append(System.getProperty("line.separator"));
        sb.append("++++++++++++++++++++++++++++++++++++++++++++++++++++");
        sb.append(System.getProperty("line.separator"));
        sb.append("Bins detail:");
        sb.append(System.getProperty("line.separator"));

        for(Bin b : bins){
//            sb.append(System.getProperty("line.separator"));
//            sb.append("++++++++++++++++++++++++++++++++++++++++++++++++++++");
            sb.append(System.getProperty("line.separator"));
            sb.append(b.getName()+" ("+b.getStates().size()+" state(s)): ");
            for(State s: b.getStates()){
                sb.append(s.getName()+" ");
            }
        }
        return sb.toString();
    }

    private int getNumberOfStates() {
        int count = 0;
        for(Bin b: bins){
            count += b.getStates().size();
        }
        return count;
    }

    public String printSuggestedDistinctWebPagesTests(){
        StringBuilder sb = new StringBuilder();
        for(Bin b1: bins){
            for(Bin b2: bins){
                if(b1!=b2){
                    Random rand = new Random();
                    rand.setSeed(69420);
                    State state1  = b1.getStates().get(rand.nextInt(b1.getStates().size()));
                    State state2  = b2.getStates().get(rand.nextInt(b2.getStates().size()));
                    sb.append("\""+this.appName+", " +state1.getName()+", "+state2.getName()+"\"");
                    sb.append(System.getProperty("line.separator"));
                }
            }
        }
        return sb.toString();
    }

    public List<Bin> getBins() {
        return this.bins;
    }
}
