package com.example.demo.ServiceLayer;

import com.example.demo.BusinessLayer.GraderBusiness;
import com.example.demo.BusinessLayer.IGraderBusiness;
import org.json.simple.JSONObject;

import java.util.List;

public class GraderService implements IService {

    private IGraderBusiness graderBusiness = new GraderBusiness();

    private JSONObject beginGrading(String code){
        JSONObject res = new JSONObject();
        res.put("response", graderBusiness.beginGrading(code));
        return res;
    }

    public JSONObject requestProcessor(JSONObject map) {
        String op = (String) map.get("operation");
        switch (op) {
            case "beginGrading":
                return beginGrading((String) map.get("code"));
            default:
                JSONObject res = new JSONObject();
                res.put("response","operation not found");
                return res;
        }
    }
}
