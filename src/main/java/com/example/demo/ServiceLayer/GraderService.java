package com.example.demo.ServiceLayer;

import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.Result;
import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.GraderBusiness;
import org.json.simple.JSONObject;
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
        try {
            graderBusiness.beginGrading(UUID.fromString(code));
        } catch (Exception e) {
            res = e.getMessage();
        }
        return Map.of("response", res);
    }

    // meaningful getters

    public Map<String, Object> getExperimentees(String code) {
        try {
            List<Participant> experimentees= graderBusiness.getParticipantsByTask(UUID.fromString(code));
            List<Integer> ids = new ArrayList<>();
            experimentees.forEach((p)-> ids.add(p.getParticipantId()));
            return Map.of("response","OK","experimentees", ids);
        } catch (CodeException e) {
            return Map.of("response",e.getMessage());
        }
    }

    public Map<String, Object> getExperimenteeResults(String code,int participantId){
        try{
            List<Result> results = graderBusiness.getExpeeRes(UUID.fromString(code),participantId);
            List<JSONObject> JResults = new ArrayList<>();
            results.forEach((resultWrapper -> JResults.add(resultWrapper.getAsJson())));
            return Map.of("response","OK","results", JResults);
        } catch (Exception e) {
            return Map.of("response",e.getMessage());
        }
    }
}
