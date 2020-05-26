package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.Result;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.Exceptions.ExpEndException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
import com.example.demo.DBAccess;
import com.example.demo.SpringBooter;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ExperimenteeBusiness implements IExperimenteeBusiness {

    private DataCache cache;
    private DBAccess db;

    @Autowired
    public ExperimenteeBusiness(DataCache cache, DBAccess db) {
        this.cache = cache;
        this.db = db;
    }

    @Override
    public Stage beginParticipation(UUID accessCode) throws ExpEndException, CodeException {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        return expee.getParticipant().getCurrStage();
    }

    @Override
    public void fillInStage(UUID accessCode, Map<String,Object> data) throws CodeException, ParseException, ExpEndException, FormatException, NotInReachException {
        Participant part = cache.getExpeeByCode(accessCode).getParticipant();
        Result result = part.fillInStage(data);
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
    public Result getResult(UUID accessCode, int idx) throws CodeException, NotInReachException {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        return expee.getResult(idx);
    }

    @Override
    public Stage getNextStage(UUID accessCode) throws CodeException, ExpEndException {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        Stage nextStage;
        try {
            nextStage = expee.getNextStage();
        }
        catch (ExpEndException e){
            db.saveExperimentee(expee); //current stage has changed, need to save
            throw e;
        }
        db.saveExperimentee(expee); //current stage has changed, need to save
        return nextStage;
    }

}
