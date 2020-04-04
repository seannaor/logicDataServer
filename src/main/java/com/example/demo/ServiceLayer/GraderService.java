package com.example.demo.ServiceLayer;

import com.example.demo.BusinessLayer.GraderBusiness;
import com.example.demo.BusinessLayer.IGraderBusiness;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class GraderService implements IService {

    private IGraderBusiness graderBusiness = new GraderBusiness();

    public Map<String,Object> beginGrading(String code){
        return Map.of("response", graderBusiness.beginGrading(code));
    }

    public Map<String,Object> requestProcessor(Map<String,Object> map) {
        String op = (String) map.get("operation");
        switch (op) {
            case "beginGrading":
                return beginGrading((String) map.get("code"));
            default:
                return Map.of("response","operation not found");
        }
    }
}
