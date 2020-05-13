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

import java.util.UUID;

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
    public Stage beginParticipation(UUID accessCode) throws ExpEndException, CodeException {
        Experimentee expee =cache.getExpeeByCode(accessCode);
        return expee.getParticipant().getCurrStage();
    }

    @Override
    public void fillInStage(UUID accessCode, JSONObject data) throws CodeException, ParseException, ExpEndException, FormatException {
        Participant part = cache.getExpeeByCode(accessCode).getParticipant();
        ResultWrapper result = part.fillInStage(data);
        db.saveStageResult(result);
    }

    @Override
    public Stage getCurrentStage(UUID accessCode) throws CodeException, ExpEndException {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        return expee.getCurrStage();
    }

    @Override
    public Stage getStage(UUID accessCode, int idx) throws CodeException, NotInReachException {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        return expee.getStage(idx);
    }

    @Override
    public ResultWrapper getResult(UUID accessCode, int idx) throws CodeException, NotInReachException {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        return expee.getResults(idx);
    }

    @Override
    public Stage getNextStage(UUID accessCode) throws CodeException, ExpEndException {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        Stage nextStage = expee.getNextStage();
        db.saveExperimentee(expee); //current stage has changed, need to save
        return nextStage;
    }
}
