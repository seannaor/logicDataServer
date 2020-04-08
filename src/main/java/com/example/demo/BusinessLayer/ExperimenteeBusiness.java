package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.Exceptions.ExpEndException;
import org.json.simple.JSONObject;

public class ExperimenteeBusiness implements IExperimenteeBusiness {

    private DataCache cache = DataCache.getInstance();

    @Override
    public Stage beginParticipation(String accessCode) throws CodeException, ExpEndException {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        return expee.getParticipant().getCurrStage();
    }

    @Override
    public void fillInStage(String accessCode, JSONObject data) throws CodeException, ExpEndException{
        Experimentee expee = cache.getExpeeByCode(accessCode);
        Stage currStage = expee.getCurrStage();
        currStage.fillIn(data);
    }

    @Override
    public Stage getCurrentStage(String accessCode) throws CodeException, ExpEndException {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        return expee.getCurrStage();
    }

    @Override
    public Stage getNextStage(String accessCode) throws CodeException, ExpEndException {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        return expee.getNextStage();
    }
}
