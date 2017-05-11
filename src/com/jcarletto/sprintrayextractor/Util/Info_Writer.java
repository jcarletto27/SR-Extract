package com.jcarletto.sprintrayextractor.Util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by jcarlett on 5/11/2017.
 * <p>
 * <p>
 * File type looks like this
 * [
 * {
 * "TotalSolidArea": 399.99997,
 * "LargestArea": 399.99997,
 * "SmallestArea": 399.99997,
 * "PixDiff": 999999999,
 * "AreaCount": 1
 * },
 * {
 * "TotalSolidArea": 399.99997,
 * "LargestArea": 399.99997,
 * "SmallestArea": 399.99997,
 * "PixDiff": 999999999,
 * "AreaCount": 1
 * }
 * ]
 * all values are mm^2
 */
public class Info_Writer {
    private final String TotalSolidArea = "TotalSolidArea";
    private final String LargestArea = "LargestArea";
    private final String SmallestArea = "SmallestArea";
    private final String PixDiff = "PixDiff";
    private final String AreaCount = "AreaCount";


    private JSONArray array;

    public Info_Writer() {
        array = new JSONArray();
    }

    public void writeInfo(double totalSolidArea, int pixelCount) {
        JSONObject fileInfo = new JSONObject();
        fileInfo.put("PixelCount", pixelCount);
        fileInfo.put(TotalSolidArea, totalSolidArea);
        fileInfo.put(LargestArea, totalSolidArea);
        fileInfo.put(SmallestArea, totalSolidArea);
        fileInfo.put(PixDiff, 0);
        fileInfo.put(AreaCount, 1);

        array.add(fileInfo);
    }

    public String getJsonInfo() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(array.toJSONString());


        return gson.toJson(element);
    }


}
