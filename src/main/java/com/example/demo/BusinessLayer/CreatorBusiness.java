package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.ExistException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreatorBusiness implements ICreatorBusiness {

    private DataCache cache;

    public CreatorBusiness() {
        this.cache = DataCache.getInstance();
    }

    @Override
    public boolean researcherLogin(String username, String password) {
        ManagementUser c;
        try {
            c = cache.getManagerByName(username);
        } catch (NotExistException ignore) {
            return false;
        }
        return c.getBguPassword().equals(password);
    }

    @Override
    public int createExperiment(String researcherName, String expName) throws NotExistException, ExistException {
        ManagementUser c = cache.getManagerByName(researcherName);
        try {
            c.getExperimentByName(expName);
            throw new ExistException(expName);
        } catch (NotExistException ignore) {
        }

        int id = c.getExperiments().size()+1;
        Experiment exp = new Experiment(expName, c);
        exp.setExperimentId(id);
        c.addExperiment(exp);
        return id;
    }

    @Override
    public void addStageToExperiment(String researcherName, int id, JSONObject stage) throws FormatException, NotExistException {
        ManagementUser c = cache.getManagerByName(researcherName);
        Experiment exp = c.getExperiment(id);
        Stage s = Stage.parseStage(stage, exp);
        //TODO: order of parsing the stage changed, re-implement
    }

    @Override
    public String saveExperiment(String researcherName, int id) throws NotExistException {
        ManagementUser c = cache.getManagerByName(researcherName);

        return "TODO: CreatorBusiness.saveExperiment";
        //TODO:implements??
    }

    @Override
    public void addExperiment(String researcherName, String expName, List<JSONObject> stages) throws NotExistException, FormatException, ExistException {
        ManagementUser c = cache.getManagerByName(researcherName);
        try {
            c.getExperimentByName(expName);
            throw new ExistException(expName);
        } catch (NotExistException ignore) {
        }

        Experiment exp = buildExperiment(stages, expName, c);
    }

    @Override
    public int addGradingTask(String researcherName, int expId, String gradTaskName, List<JSONObject> ExpeeExp, List<Integer> stagesToCheck, List<JSONObject> personalExp) throws NotExistException, FormatException {
        ManagementUser c = cache.getManagerByName(researcherName);
        Experiment exp = c.getExperiment(expId);
        Experiment personal = buildExperiment(personalExp, gradTaskName+"/personal", c);
        Experiment forExpee = buildExperiment(ExpeeExp, gradTaskName+"/forExpee", c);
        GradingTask gt  = new GradingTask(exp, personal, forExpee);
        gt.setStagesByIdx(stagesToCheck);
        int id = cache.getAllGradingTasks(researcherName, expId).size();
        gt.setGradingTaskId(id);
        cache.addGradingTask(gt);
        return id;
    }

    @Override
    public void addToPersonal(String researcherName, int expId, String gradTaskName, JSONObject stage) throws NotExistException, FormatException {
        GradingTask gt = cache.getGradingTaskByName(researcherName, expId, gradTaskName);
        Experiment personal = gt.getGeneralExperiment();
        addStageToExp(stage,personal);
    }

    @Override
    public void addToResultsExp(String researcherName, int expId, String gradTaskName, JSONObject stage) throws NotExistException, FormatException {
        GradingTask gt = cache.getGradingTaskByName(researcherName, expId, gradTaskName);
        Experiment resExp = gt.getGradingExperiment();
        addStageToExp(stage,resExp);
    }

    private void addStageToExp(JSONObject stage,Experiment exp) throws FormatException {
        Stage s = Stage.parseStage(stage, exp);
//        exp.addStage(s);
        //TODO: order of parsing the stage changed, re-implement
    }

    @Override
    public void setStagesToCheck(String researcherName, int expId, String gradTaskName, List<Integer> stagesToCheck) throws NotExistException {
        GradingTask gt = cache.getGradingTaskByName(researcherName, expId, gradTaskName);
        gt.setStagesByIdx(stagesToCheck);
    }

    @Override
    public String saveGradingTask(String researcherName, int expId, String gradTaskName) throws NotExistException {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return researcherName + " not exist";
        return "TODO:CreatorBusiness.saveGradingTask";
        //TODO:implements??
    }

    @Override
    public void setAlliePermissions(String researcherName, int expId, String allieMail, List<String> permissions) throws NotExistException {
        ManagementUser c = cache.getManagerByName(researcherName);
        Experiment exp = c.getExperiment(expId);

        ManagementUser ally;
        try{
            ally = cache.getManagerByEMail(allieMail);
        }catch (NotExistException ignore){
            ally = new ManagementUser();
            ally.setUserEmail(allieMail);
            cache.addManager(ally);
        }
        ally.addExperiment(exp);

        List<Permission> pers = new ArrayList<>();
        for (String per : permissions) {
            pers.add(new Permission(per, ally));
        }
        ally.setPermissions(pers);
    }

    @Override
    public void addGrader(String researcherName, int expId, String gradTaskName, String graderMail) throws NotExistException {
        ManagementUser c = cache.getManagerByName(researcherName);
        GradingTask gt = cache.getGradingTaskByName(researcherName, expId, gradTaskName);
        Grader grader = cache.getGraderByEMail(graderMail);
        if (grader == null) {
            grader = new Grader(graderMail, gt.getBaseExperiment());
            cache.addGrader(grader);
        }
        cache.addGraderToGradingTask(gt, grader,"CODE");//TODO:figure out WTF
    }

    @Override
    public void addExperimentee(String researcherName, int expId, String expeeMail) throws NotExistException, ExistException {
        ManagementUser c = cache.getManagerByName(researcherName);
        Experiment exp = c.getExperiment(expId);
        if (cache.isExpeeInExperiment(expeeMail, expId)) throw new ExistException(expeeMail, "experiment " + expId);
        cache.addExperimentee(new Experimentee(expeeMail, exp));
    }

    @Override
    public void addExpeeToGrader(String researcherName, int expId, String gradTaskName, String graderMail, String expeeMail) throws NotExistException, ExistException {
        Grader grader = cache.getGraderByEMail(graderMail);
        GradingTask gt = cache.getGradingTaskByName(researcherName, expId, gradTaskName);
        Participant participant = cache.getExpeeByMailAndExp(expeeMail, expId).getParticipant();
        cache.addExpeeToGradingTask(gt, grader, participant);
        //TODO: test
    }

    private static Experiment buildExperiment(List<JSONObject> stages, String expName, ManagementUser creator) throws FormatException {
        Experiment exp = new Experiment(expName, creator);
        int id = creator.getExperiments().size()+1;
        exp.setExperimentId(id);
        creator.addExperiment(exp);
        for (JSONObject jStage : stages) {
            Stage s = Stage.parseStage(jStage, exp);
            if (s == null) {
                creator.removeExp(expName);
                return null;
            }
        }
        return exp;
    }
}
