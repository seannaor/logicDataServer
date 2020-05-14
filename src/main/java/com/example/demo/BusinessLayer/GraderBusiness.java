package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.ResultWrapper;
import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GraderBusiness implements IGraderBusiness {
    @Autowired
    private DataCache cache;

//    public GraderBusiness() {
//        this.cache = DataCache.getInstance();
//    }

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
        for(Participant p:task.getParticipants()){
            if(p.isDone()) parts.add(p);
        }
        return parts;
    }

    @Override
    public List<ResultWrapper> getExpeeRes(UUID accessCode, int parti_id) throws CodeException, NotExistException, FormatException {
        GraderToGradingTask task = cache.getTaskByCode(accessCode);
        return task.getExpeeRes(parti_id);
    }


}
