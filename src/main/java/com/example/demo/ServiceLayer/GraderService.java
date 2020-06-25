package com.example.demo.ServiceLayer;

import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.Result;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.GraderBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class GraderService {
    @Autowired
    private GraderBusiness graderBusiness;

    public Map<String, Object> beginGrading(String code) {
        String res = "OK";
        if (graderBusiness.beginGrading(UUID.fromString(code)))
            return Map.of("response", "OK");
        return Map.of("response", new CodeException(code));
    }

    public Map<String, Object> fillInStage(String accessCode, int pid, Map<String, Object> data) {
        String res = "OK";
        try {
            graderBusiness.fillInStage(UUID.fromString(accessCode), pid, data);
        } catch (Exception e) {
            res = e.getMessage();
        }
        return Map.of("response", res);
    }

    public Map<String, Object> getNextStage(String accessCode, int pid) {
        try {
            Stage s = graderBusiness.getNextStage(UUID.fromString(accessCode), pid);
            return s.getAsMap();
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
    }

    public Map<String, Object> getCurrentStage(String accessCode, int pid) {
        try {
            Stage s = graderBusiness.getCurrentStage(UUID.fromString(accessCode), pid);
            return s.getAsMap();
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
    }

    public Map<String, Object> getStage(String accessCode, int pid, int idx) {
        try {
            Stage s = graderBusiness.getStage(UUID.fromString(accessCode), pid, idx);
            Result res = graderBusiness.getResult(UUID.fromString(accessCode), pid, idx);

            Map<String, Object> stageMap = s.getAsMap();
            if (res == null)
                return Map.of("type", stageMap.get("type"), "stage", stageMap.get("stage"), "results", "None");
            return Map.of("type", stageMap.get("type"), "stage", stageMap.get("stage"), "results", res.getAsMap());
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
    }

    // meaningful getters

    public Map<String, Object> getExperimentees(String code) {
        try {
            List<Participant> experimentees = graderBusiness.getParticipantsByTask(UUID.fromString(code));
            List<Integer> ids = new ArrayList<>();
            experimentees.forEach((p) -> ids.add(p.getParticipantId()));
            return Map.of("experimentees", ids);
        } catch (CodeException e) {
            return Map.of("response", e.getMessage());
        }
    }

    public Map<String, Object> getExperimenteeResults(String code, int participantId) {
        try {
            List<Result> results = graderBusiness.getExpeeRes(UUID.fromString(code), participantId);
            List<Map<String, Object>> JResults = new ArrayList<>();
            results.forEach((resultWrapper -> JResults.add(resultWrapper.getAsMap())));
            return Map.of("results", JResults);
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
    }
}
