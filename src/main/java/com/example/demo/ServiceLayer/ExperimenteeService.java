package com.example.demo.ServiceLayer;

import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.ExperimenteeBusiness;
import com.example.demo.BusinessLayer.IExperimenteeBusiness;
import org.json.simple.JSONObject;

import java.util.Map;

public class ExperimenteeService {

    private IExperimenteeBusiness experimenteeBusiness = new ExperimenteeBusiness();

    //UC 2.1 - Login
    public Map<String, Object> beginParticipation(String accessCode) {

        try {
            Stage s = experimenteeBusiness.beginParticipation(accessCode);
            return Map.of("response", "OK", "type", s.getType());
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
    }

    public Map<String, Object> getCurrentStage(String accessCode) {
        try {
            Stage s = experimenteeBusiness.getCurrentStage(accessCode);
            return Map.of("response", "OK", "stage", s.getJson());
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
    }

    public Map<String, Object> getNextStage(String accessCode) {
        try {
            Stage s = experimenteeBusiness.getNextStage(accessCode);
            return Map.of("response", "OK", "stage", s.getJson());
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
    }


    //UC 2.2.*
    public Map<String, Object> fillInStage(String accessCode, JSONObject data) {
        String res ="OK";
        try {
            experimenteeBusiness.fillInStage(accessCode, data);
        } catch (Exception e) {
            res = e.getMessage();
        }
        return Map.of("response", res);
    }
}
