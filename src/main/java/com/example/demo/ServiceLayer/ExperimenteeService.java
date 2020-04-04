package com.example.demo.ServiceLayer;

import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.ExperimenteeBusiness;
import com.example.demo.BusinessLayer.IExperimenteeBusiness;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class ExperimenteeService implements IService {

    private IExperimenteeBusiness experimenteeBusiness = new ExperimenteeBusiness();

    //UC 2.1 - Login
    public Map<String,Object> beginParticipation(String accessCode){
        return Map.of("response",experimenteeBusiness.beginParticipation(accessCode));
    }

    public Map<String,Object> getCurrentStage(String accessCode){
        return Map.of("stage",experimenteeBusiness.getCurrentStage(accessCode));
    }

    public Map<String,Object> getNextStage(String accessCode){
        return Map.of("stage",experimenteeBusiness.getNextStage(accessCode));
    }

    //UC 2.2.*
    public Map<String,Object> fillInStage(String accessCode, JSONObject data){
        return Map.of("response",experimenteeBusiness.fillInStage(accessCode,data));
    }

    public Map<String,Object> requestProcessor(Map<String,Object> map) {
        String op = (String) map.get("operation");
        switch (op) {
            case "beginParticipation":
                return beginParticipation((String) map.get("code"));
            case "fillInStage":
                return fillInStage((String) map.get("code"), (JSONObject) map.get("data"));
            default:
                return Map.of("response","operation not found");
        }
    }
}
