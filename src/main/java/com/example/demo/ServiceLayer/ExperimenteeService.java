package com.example.demo.ServiceLayer;

import com.example.demo.BusinessLayer.ExperimenteeBusiness;
import com.example.demo.BusinessLayer.IExperimenteeBusiness;
import org.json.simple.JSONObject;

import java.util.List;

public class ExperimenteeService implements IService {

    private IExperimenteeBusiness experimenteeBusiness = new ExperimenteeBusiness();

    //UC 2.1 - Login
    private JSONObject beginParticipation(String accessCode){
        JSONObject res = new JSONObject();
        res.put("response",experimenteeBusiness.beginParticipation(accessCode));
        return res;
    }

    //UC 2.2.*
    private JSONObject fillInStage(String accessCode, JSONObject data){
        JSONObject res = new JSONObject();

        return res;
    }

    public JSONObject requestProcessor(JSONObject map) {
        String op = (String) map.get("operation");
        switch (op) {
            case "beginParticipation":
                return beginParticipation((String) map.get("code"));
            case "fillInStage":
                return fillInStage((String) map.get("code"), (JSONObject) map.get("data"));
            default:
                JSONObject res = new JSONObject();
                res.put("response","operation not found");
                return res;
        }
    }
}
