package com.example.demo.ServiceLayer;

import com.example.demo.BusinessLayer.Entities.Results.Result;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.ExpEndException;
import com.example.demo.BusinessLayer.ExperimenteeBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.demo.ServiceLayer.JSONUtil.makeStageAndResult;

@Service
public class ExperimenteeService {

    private ExperimenteeBusiness experimenteeBusiness;

    @Autowired
    public ExperimenteeService(ExperimenteeBusiness experimenteeBusiness) {
        this.experimenteeBusiness = experimenteeBusiness;
    }

    //UC 2.1 - Login
    public boolean beginParticipation(String accessCode) {
        return experimenteeBusiness.beginParticipation(UUID.fromString(accessCode));
    }

    public Map<String, Object> getNextStage(String accessCode) {
        try {
            Stage s = experimenteeBusiness.getNextStage(UUID.fromString(accessCode));
            Result res = experimenteeBusiness.getResult(UUID.fromString(accessCode), s.getStageID().getStageIndex());
            return makeStageAndResult(s, res);
        } catch (Exception e) {
            return Map.of("Error", e.getMessage());
        }
    }

    //UC 2.2.*
    public Map<String, Object> fillInStage(String accessCode, Map<String, Object> data) {
        try {
            UUID code = UUID.fromString(accessCode);
            experimenteeBusiness.fillInStage(code, data);
            Stage next = experimenteeBusiness.getNextStage(code);
            Result res = experimenteeBusiness.getResult(code, next.getStageID().getStageIndex());
            return makeStageAndResult(next, res);
        } catch (ExpEndException e) {
            return Map.of(
                    "type", "complete",
                    "stage", Map.of()
            );
        } catch (Exception e) {
            return Map.of("Error", e.getMessage());
        }

    }

    public Map<String, Object> reachableStages(String accessCode) {
        try {
            UUID code = UUID.fromString(accessCode);
            Stage currentStage = experimenteeBusiness.getCurrentStage(code);
            int idx = currentStage.getStageID().getStageIndex();
            Result currentRes = experimenteeBusiness.getResult(UUID.fromString(accessCode), idx);
            Map<String, Object> expMap = new HashMap<>();
            expMap.put("expName", currentStage.getExperiment().getExperimentName());
            List<Map<String, Object>> stagesMapList = new ArrayList<>();
            for (int i = 0; i < idx; i++) {
                Stage s = experimenteeBusiness.getStage(code, i);
                Result r = experimenteeBusiness.getResult(code, i);

                stagesMapList.add(makeStageAndResult(s, r));
            }
            stagesMapList.add(makeStageAndResult(currentStage, currentRes));
            expMap.put("stages", stagesMapList);
            return expMap;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Map.of("response", e.getMessage());
        }
    }

    public Map<String, Object> getCurrentStage(String accessCode) {
        try {
            Stage s = experimenteeBusiness.getCurrentStage(UUID.fromString(accessCode));
            Result res = experimenteeBusiness.getResult(UUID.fromString(accessCode), s.getStageID().getStageIndex());
            if(res == null)
                return Map.of("response", "OK", "type", s.getType(), "stage", s.getAsMap());
            return Map.of("response", "OK", "type", s.getType(), "stage", s.getAsMap(), "result", res.getAsMap());
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
    }

    public Map<String, Object> getStageAt(String accessCode, int id) {
        try {
            Stage s = experimenteeBusiness.getStage(UUID.fromString(accessCode), id);
            Result res = experimenteeBusiness.getResult(UUID.fromString(accessCode), id);
            if (res == null) return Map.of("response", "OK", "stage", s.getAsMap(), "results", "None");
            return Map.of("response", "OK", "stage", s.getAsMap(), "results", res.getAsMap());
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
    }
}
