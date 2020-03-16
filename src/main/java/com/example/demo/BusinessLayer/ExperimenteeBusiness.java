package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.Experimentee;
import net.minidev.json.JSONObject;

public class ExperimenteeBusiness implements IExperimenteeBusiness {

    @Override
    public boolean beginParticipation(String accessCode) {
        return getExperimentee(accessCode) != null;
    }

    @Override
    public boolean fillInStage(String accessCode, JSONObject data) {
        Experimentee expee = getExperimentee(accessCode);
//        expee.getExperiment();
        return false;
    }

    //TODO: implements
    // if @accessCode legal, return experimentee, else null
    private Experimentee getExperimentee(String accessCode){
        return null;
    }
}
