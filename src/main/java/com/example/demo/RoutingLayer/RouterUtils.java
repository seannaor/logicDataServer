package com.example.demo.RoutingLayer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

public class RouterUtils {


//    @RequestMapping("/{service}")
//    public Map<String, Object> requestProcessor(@PathVariable String service, @RequestParam String req) {
//        System.out.println("received: " + req);
//        JSONObject jsonReq = strToJSON(req);
//        if (!serviceMap.containsKey(service)) return Map.of("response", "no such entity " + service);
//        return serviceMap.get(service).requestProcessor(jsonReq);
//    }

    private static JSONObject mapToJSON(Map<String, String> req) {
        JSONObject ret = new JSONObject();
        if (req == null) return ret;
        for (String key : req.keySet()) {
            ret.put(key, req.get(key));
        }
        return ret;
    }

    static JSONObject strToJSON(String req) {
        JSONObject ret = new JSONObject();
        JSONParser parser = new JSONParser();
        try {
            return (JSONObject) parser.parse(req);
        }
        catch (Exception ignore){
            return new JSONObject();
        }
    }

    @Controller
    public class IndexController implements ErrorController {
        @Override
        @RequestMapping("/error")
        @ResponseBody
        public String getErrorPath() {
            // TODO Auto-generated method stub
            return "sorry your request could not be processed :(";
        }

    }

}
