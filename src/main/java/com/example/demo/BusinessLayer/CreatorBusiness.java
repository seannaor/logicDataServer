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
        } catch (NotExistException ignore) {}

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
        exp.addStage(Stage.parseStage(stage));
    }

    @Override
    public String saveExperiment(String researcherName, int id) throws NotExistException {
        ManagementUser c = cache.getManagerByName(researcherName);

        return "TODO: CreatorBusiness.saveExperiment";
        //TODO:implements??
    }

    @Override
    public int addExperiment(String researcherName, String expName, List<JSONObject> stages) throws NotExistException, FormatException, ExistException {
        ManagementUser c = cache.getManagerByName(researcherName);
        try {
            c.getExperimentByName(expName);
            throw new ExistException(expName);
        } catch (NotExistException ignore) {
        }

        Experiment exp = buildExperiment(stages, expName, c);
        return exp.getExperimentId();
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
    public void addToPersonal(String researcherName, int expId, int taskId, JSONObject stage) throws NotExistException, FormatException {
        GradingTask gt = cache.getGradingTaskById(researcherName, expId, taskId);
        Experiment personal = gt.getGeneralExperiment();
        personal.addStage(Stage.parseStage(stage));
    }

    @Override
    public void addToResultsExp(String researcherName, int expId, int taskId, JSONObject stage) throws NotExistException, FormatException {
        GradingTask gt = cache.getGradingTaskById(researcherName, expId, taskId);
        Experiment resExp = gt.getGradingExperiment();
        resExp.addStage(Stage.parseStage(stage));
    }

    @Override
    public void setStagesToCheck(String researcherName, int expId, int taskId, List<Integer> stagesToCheck) throws NotExistException {
        GradingTask gt = cache.getGradingTaskById(researcherName, expId, taskId);
        gt.setStagesByIdx(stagesToCheck);
    }

    @Override
    public String saveGradingTask(String researcherName, int expId, int taskId) throws NotExistException {
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
    public void addGrader(String researcherName, int expId, int taskId, String graderMail) throws NotExistException {
        GradingTask gt = cache.getGradingTaskById(researcherName, expId, taskId);
        Grader grader;
        try {
            grader = cache.getGraderByEMail(graderMail);
        }catch (NotExistException ignore){
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
    public void addExpeeToGrader(String researcherName, int expId, int taskId, String graderMail, String expeeMail) throws NotExistException, ExistException {
        Grader grader = cache.getGraderByEMail(graderMail);
        GradingTask gt = cache.getGradingTaskById(researcherName, expId, taskId);
        Participant participant = cache.getExpeeByMailAndExp(expeeMail, expId).getParticipant();
        cache.addExpeeToGradingTask(gt, grader, participant);
        //TODO: fix?
    }

    private static Experiment buildExperiment(List<JSONObject> stages, String expName, ManagementUser creator) throws FormatException {
        Experiment exp = new Experiment(expName, creator);
        int id = creator.getExperiments().size();
        exp.setExperimentId(id);
        creator.addExperiment(exp);
        for (JSONObject jStage : stages) {
            try{
                exp.addStage(Stage.parseStage(jStage));
            }catch (FormatException e){
                creator.removeExp(expName);
                throw e;
            }
        }
        return exp;
    }
}
