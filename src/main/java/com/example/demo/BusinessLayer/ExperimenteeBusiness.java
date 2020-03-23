package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import org.json.simple.JSONObject;

public class ExperimenteeBusiness implements IExperimenteeBusiness {

    private DataCache cache = DataCache.getInstance();

    @Override
    public boolean beginParticipation(String accessCode) {
        return cache.getExpeeByCode(accessCode) != null;
    }

    @Override
    public String fillInStage(String accessCode, JSONObject data) {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        if (expee == null) return "code is not valid";
        Experiment exp = expee.getExperiment();
        if(exp==null) return "couldn't find experiment";
        //fill in a stage at the experiment and return the result
        return "TODO: ExperimenteeBusiness.fillInStage";
    }

}
