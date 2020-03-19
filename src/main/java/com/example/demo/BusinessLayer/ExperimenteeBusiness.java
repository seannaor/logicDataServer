package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.Experimentee;
import net.minidev.json.JSONObject;

public class ExperimenteeBusiness implements IExperimenteeBusiness {

    private DataCache cache = DataCache.getInstance();

    @Override
    public boolean beginParticipation(String accessCode) {
        return cache.getExpeeByCode(accessCode) != null;
    }

    @Override
    public boolean fillInStage(String accessCode, JSONObject data) {
        Experimentee expee = cache.getExpeeByCode(accessCode);
//        expee.getExperiment();
        return false;
    }

}
