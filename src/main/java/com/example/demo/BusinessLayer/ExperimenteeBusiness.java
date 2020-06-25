package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.Result;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.*;
import com.example.demo.DBAccess;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
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
    public boolean beginParticipation(UUID accessCode) {
        try {
            cache.getExpeeByCode(accessCode);
        } catch (CodeException ignore) {
            return false;
        }
        return true;
    }

    @Override
    public void fillInStage(UUID accessCode, Map<String, Object> data) throws CodeException, ParseException, ExpEndException, FormatException, NotInReachException, NotExistException {
        Participant part = cache.getExpeeByCode(accessCode).getParticipant();
        if (part.getCurrStage().getType().equals("info")) return;
        Result result = part.fillInStage((Map<String, Object>) data.get("data"));
        db.saveStageResult(result);
    }

    @Override
    public Stage getCurrentStage(UUID accessCode) throws CodeException, ExpEndException, NotExistException {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        return expee.getCurrStage();
    }

    public List<Stage> getReachableStages(UUID accessCode) throws NotInReachException, CodeException, NotExistException {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        List<Stage> stages = new LinkedList<>();
        for (int i = 0; i < expee.getParticipant().getCurrStageIdx()+1; i++) {
            stages.add(expee.getStage(i));
        }
        return stages;
    }

    public List<Result> getReachableResults(UUID accessCode) throws NotInReachException, CodeException {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        List<Result> results = new LinkedList<>();
        for (int i = 0; i < expee.getParticipant().getCurrStageIdx()+1; i++) {
            results.add(expee.getResult(i));
        }
        return results;
    }

    @Override
    public Stage getStage(UUID accessCode, int idx) throws CodeException, NotInReachException, NotExistException {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        return expee.getStage(idx);
    }

    @Override
    public Result getResult(UUID accessCode, int idx) throws CodeException, NotInReachException {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        return expee.getResult(idx);
    }

    @Override
    public Stage getNextStage(UUID accessCode) throws CodeException, ExpEndException, NotExistException {
        Experimentee expee = cache.getExpeeByCode(accessCode);
        Stage nextStage;
        try {
            nextStage = expee.getNextStage();
        } catch (ExpEndException | NotExistException e) {
            db.saveExperimentee(expee); //current stage has changed, need to save
            throw e;
        }
        db.saveExperimentee(expee); //current stage has changed, need to save
        return nextStage;
    }

}
