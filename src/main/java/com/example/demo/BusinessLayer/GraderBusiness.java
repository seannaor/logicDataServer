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

@Service
public class GraderBusiness implements IGraderBusiness {
    @Autowired
    private DataCache cache;

//    public GraderBusiness() {
//        this.cache = DataCache.getInstance();
//    }

    @Override
    public boolean beginGrading(String accessCode) {
        try {
            cache.getGraderByCode(accessCode);
            return true;
        } catch (CodeException ignore) {
            return false;
        }
    }

    @Override
    public List<Integer> getParticipantsByTask(String accessCode) throws CodeException {
        GraderToGradingTask task = cache.getTaskByCode(accessCode);
        List<Integer> parts = new ArrayList<>();
        for(Participant p:task.getParticipants()){
            if(p.isDone())
                parts.add(p.getParticipantId());
        }
        return parts;
    }

    @Override
    public List<ResultWrapper> getExpeeRes(String accessCode, int parti_id) throws CodeException, NotExistException, FormatException {
        GraderToGradingTask task = cache.getTaskByCode(accessCode);
        return task.getExpeeRes(parti_id);

    }


}
