package com.tool.data.goldstandard;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;

public class GoldStandardModelReader {

    public static void main(String[] args) throws IOException {
        String appName = "addressbook";
        GoldStandardModel gs = new GoldStandardModel(appName);
        System.out.println(gs.printSuggestedDistinctWebPagesTests());
    }
}
