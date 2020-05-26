package com.example.demo.ServiceLayer;

import com.example.demo.BusinessLayer.Entities.Results.Result;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.ExperimenteeBusiness;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class ExperimenteeService {

    private ExperimenteeBusiness experimenteeBusiness;

    @Autowired
    public ExperimenteeService(ExperimenteeBusiness experimenteeBusiness){
        this.experimenteeBusiness=experimenteeBusiness;
    }

    //UC 2.1 - Login
    public Map<String, Object> beginParticipation(String accessCode) {
        try {
            experimenteeBusiness.beginParticipation(UUID.fromString(accessCode));
            return Map.of("response", "OK");
        } catch (Exception e) {
            return Map.of("response", e.getMessage());

        }
    }

    public Map<String, Object> getCurrentStage(String accessCode) {
        try {
            Stage s = experimenteeBusiness.getCurrentStage(UUID.fromString(accessCode));
            Result res = experimenteeBusiness.getResult(UUID.fromString(accessCode),s.getStageID().getStageIndex());
            return Map.of("response", "OK", "type",s.getType(),"stage", s.getJson(),"result",res.getJson());
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
    }

    public Map<String, Object> getNextStage(String accessCode) {
        try {
            Stage s = experimenteeBusiness.getNextStage(UUID.fromString(accessCode));
            Result res = experimenteeBusiness.getResult(UUID.fromString(accessCode),s.getStageID().getStageIndex());
            return Map.of("response", "OK", "type",s.getType(),"stage", s.getJson(),"result",res.getJson());
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

    public Map<String, Object> getStageAt(String accessCode, int id) {
        try {
            Stage s = experimenteeBusiness.getStage(UUID.fromString(accessCode), id);
            Result res = experimenteeBusiness.getResult(UUID.fromString(accessCode), id);
            if(res==null)  return Map.of("response", "OK", "stage", s.getJson(), "results", "None");
            return Map.of("response", "OK", "stage", s.getJson(), "results", res.getJson());
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
    }
}
