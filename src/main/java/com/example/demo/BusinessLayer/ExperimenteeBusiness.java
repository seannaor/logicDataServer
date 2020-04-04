package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import org.json.simple.JSONObject;

public class ExperimenteeBusiness implements IExperimenteeBusiness {

    private DataCache cache = DataCache.getInstance();

    @Override
    public boolean beginParticipation(String accessCode) {
//        return cache.getExpeeByCode(accessCode)!=null;
        return true;
    }

    @Override
    public String fillInStage(String accessCode, JSONObject data) {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        if (expee == null) return "code is not valid";
        Stage currStage = expee.getCurrStage();
//        currStage.fillIn(data);
        //fill in a stage at the experiment and return next stage
        return "TODO: ExperimenteeBusiness.fillInStage";
    }

    @Override
    public Stage getCurrentStage(String accessCode) {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        return expee.getCurrStage();
    }

    @Override
    public Stage getNextStage(String accessCode) {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        return expee.getNextStage();
    }

}
