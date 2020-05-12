package com.example.demo.RoutingLayer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Base64;
import java.util.List;
import java.util.Map;

public class RouterUtils {

    private static JSONObject mapToJSON(Map<String, String> req) {
        JSONObject ret = new JSONObject();
        if (req == null) return ret;
        for (String key : req.keySet()) {
            ret.put(key, req.get(key));
        }
        return ret;
    }

    static JSONObject strToJSON(String req) {
        JSONParser parser = new JSONParser();
        try {
            return (JSONObject) parser.parse(req);
        }
        catch (Exception ignore){
            return new JSONObject();
        }
    }

    static JSONObject arrToJSON(List<String> req) {
        if(req.size()%2!=0) return new JSONObject();
        JSONObject ret = new JSONObject();
        for (int i = 0; i < req.size(); i+=2) {
            ret.put(req.get(i), req.get(i + 1));
        }
        return ret;
    }

    static String decode(String coded){
        byte[] decodedBytes = Base64.getDecoder().decode(coded);
        return new String(decodedBytes);
    }

    static String encode(String to_code){
        return Base64.getEncoder().encodeToString(to_code.getBytes());
    }

}
