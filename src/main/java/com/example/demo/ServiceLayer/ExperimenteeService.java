package com.example.demo.ServiceLayer;

import com.example.demo.BusinessLayer.Entities.Results.ResultWrapper;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.ExperimenteeBusiness;
import com.example.demo.BusinessLayer.IExperimenteeBusiness;
import org.hibernate.engine.jdbc.spi.ResultSetWrapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class ExperimenteeService {
    @Autowired
    private ExperimenteeBusiness experimenteeBusiness;

//    public ExperimenteeService() {
//        this.experimenteeBusiness = new ExperimenteeBusiness();
//    }

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
        String res = "OK";
        try {
            experimenteeBusiness.fillInStage(accessCode, data);
        } catch (Exception e) {
            res = e.getMessage();
        }
        return Map.of("response", res);
    }

    public Map<String, Object> getStageAt(String code, int id) {
        try {
            Pair<Stage, ResultWrapper> pair = experimenteeBusiness.getStage(code, id);
            return Map.of("response", "OK", "stage", pair.getFirst(), "results", pair.getSecond());
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
    }
}
