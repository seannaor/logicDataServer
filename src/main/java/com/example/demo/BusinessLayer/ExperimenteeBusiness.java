package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.ResultWrapper;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.Exceptions.ExpEndException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
import com.example.demo.DBAccess;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class ExperimenteeBusiness implements IExperimenteeBusiness {
    @Autowired
    private DataCache cache;
    @Autowired
    private DBAccess db;

//    public ExperimenteeBusiness() {
//        this.cache = DataCache.getInstance();
//    }

    @Override
    public Stage beginParticipation(String accessCode) throws ExpEndException, CodeException {
        Experimentee expee =cache.getExpeeByCode(accessCode);
        return expee.getParticipant().getCurrStage();
    }

    @Override
    public void fillInStage(String accessCode, JSONObject data) throws CodeException, ParseException, ExpEndException, FormatException {
        Participant part = cache.getExpeeByCode(accessCode).getParticipant();
        part.fillInStage(data);
    }

    @Override
    public Stage getCurrentStage(String accessCode) throws CodeException, ExpEndException {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        return expee.getCurrStage();
    }

    @Override
    public Pair<Stage, ResultWrapper> getStage(String accessCode, int idx) throws CodeException, NotInReachException {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        return Pair.of(expee.getStage(idx),expee.getResults(idx));
    }

    @Override
    public Stage getNextStage(String accessCode) throws CodeException, ExpEndException {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        Stage nextStage = expee.getNextStage();
        db.saveExperimentee(expee); //current stage has changed, need to save
        return nextStage;
    }
}
