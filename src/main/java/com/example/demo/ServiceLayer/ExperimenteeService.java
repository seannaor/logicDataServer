package com.example.demo.ServiceLayer;

import com.example.demo.BusinessLayer.Entities.Results.ResultWrapper;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.ExperimenteeBusiness;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class ExperimenteeService {
    @Autowired
    private ExperimenteeBusiness experimenteeBusiness;

    //UC 2.1 - Login
    public Map<String, Object> beginParticipation(String accessCode) {

        try {
            Stage s = experimenteeBusiness.beginParticipation(UUID.fromString(accessCode));
            return Map.of("response", "OK", "type", s.getType());
        } catch (Exception e) {
            return Map.of("response", e.getMessage());

        }
    }

    public Map<String, Object> getCurrentStage(String accessCode) {
        try {
            Stage s = experimenteeBusiness.getCurrentStage(UUID.fromString(accessCode));
            return Map.of("response", "OK", "stage", s.getJson());
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
    }

    public Map<String, Object> getNextStage(String accessCode) {
        try {
            Stage s = experimenteeBusiness.getNextStage(UUID.fromString(accessCode));
            return Map.of("response", "OK", "stage", s.getJson());
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
    }


    //UC 2.2.*
    public Map<String, Object> fillInStage(String accessCode, JSONObject data) {
        String res = "OK";
        try {
            experimenteeBusiness.fillInStage(UUID.fromString(accessCode), data);
        } catch (Exception e) {
            res = e.getMessage();
        }
        return Map.of("response", res);
    }

    public Map<String, Object> getStageAt(String code, int id) {
        try {
            Stage s = experimenteeBusiness.getStage(UUID.fromString(code), id);
            ResultWrapper res = experimenteeBusiness.getResult(UUID.fromString(code), id);
            if(res==null)  return Map.of("response", "OK", "stage", s.getJson(), "results", "None");
            return Map.of("response", "OK", "stage", s.getJson(), "results", res.getAsJson());
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
    }
}
