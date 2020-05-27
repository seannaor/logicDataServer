package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.Result;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.*;
import com.example.demo.DBAccess;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class GraderBusiness implements IGraderBusiness {

    private DataCache cache;
    private DBAccess db;

    @Autowired
    public GraderBusiness(DataCache cache, DBAccess db) {
        this.cache = cache;
        this.db=db;
    }

    @Override
    public boolean beginGrading(UUID accessCode) {
        try {
            cache.getGraderByCode(accessCode);
            return true;
        } catch (CodeException ignore) {
            return false;
        }
    }

    @Override
    public List<Participant> getParticipantsByTask(UUID accessCode) throws CodeException {
        GraderToGradingTask task = cache.getTaskByCode(accessCode);
        List<Participant> parts = new ArrayList<>();
        for (Participant p : task.getParticipants())
            parts.add(p);

        return parts;
    }

    @Override
    public List<Result> getExpeeRes(UUID accessCode, int pid) throws CodeException, NotExistException, FormatException, NotInReachException {
        GraderToGradingTask task = cache.getTaskByCode(accessCode);
        return task.getExpeeRes(pid);
    }

    public void fillInStage(UUID accessCode, int pid, Map<String, Object> data) throws CodeException, NotInReachException, ExpEndException, ParseException, FormatException, NotExistException {
        fillInStage(getParticipant(accessCode,pid), data);
    }

    public void submitGradingResults(UUID accessCode, int pid) throws NotExistException, CodeException {
        GraderToGradingTask task = cache.getTaskByCode(accessCode);
        task.submitResults(pid);
    }

    public boolean isSubmitted(UUID accessCode, int pid) throws NotExistException, CodeException {
        GraderToGradingTask task = cache.getTaskByCode(accessCode);
        return task.isSubmitted(pid);
    }

    public Stage getNextStage(UUID accessCode, int pid) throws CodeException, ExpEndException, NotExistException {
        Participant participant = getParticipant(accessCode,pid);
        Stage s = participant.getNextStage();
        db.saveParticipant(participant);
        return s;
    }

    public Stage getCurrentStage(UUID accessCode, int pid) throws CodeException, NotExistException, ExpEndException {
        Participant participant = getParticipant(accessCode,pid);
        return participant.getCurrStage();
    }

    public Stage getStage(UUID accessCode, int pid, int idx) throws CodeException, NotExistException, NotInReachException {
        Participant participant = getParticipant(accessCode,pid);
        return participant.getStage(idx);
    }

    public Result getResult(UUID accessCode,int pid, int idx) throws CodeException, NotExistException, NotInReachException {
        Participant participant = getParticipant(accessCode,pid);
        return participant.getResult(idx);
    }


    //Utils
    private Participant getParticipant(UUID accessCode, int pid) throws CodeException, NotExistException {
        GraderToGradingTask task = cache.getTaskByCode(accessCode);
        if(pid==-1)return task.getGeneralExpParticipant();
        return task.getGraderParticipant(pid);
    }

    public void fillInStage(Participant p, Map<String, Object> data) throws NotInReachException, ExpEndException, ParseException, FormatException, NotExistException {
        Result result = p.fillInStage((Map<String, Object>) data.get("data"));
        db.saveStageResult(result);
    }

}
