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

    private DataCache cache = DataCache.getInstance();

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
    public void createExperiment(String researcherName, String expName) throws NotExistException, ExistException {
        ManagementUser c = cache.getManagerByName(researcherName);
        try {
            c.getExperimentByName(expName);
            throw new ExistException(expName);
        } catch (NotExistException ignore) {
        }

        Experiment exp = new Experiment(expName, c);
        c.addExperiment(exp);
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
    public void addExperiment(String researcherName, String expName, List<JSONObject> stages) throws NotExistException, FormatException,ExistException {
        ManagementUser c = cache.getManagerByName(researcherName);
        try {
            c.getExperimentByName(expName);
            throw new ExistException(expName);
        } catch (NotExistException ignore) { }

        Experiment exp = buildExperiment(stages, expName, c);
    }

    @Override
    public void addGradingTask(String researcherName, int expId, String gradTaskName, List<JSONObject> ExpeeExp, List<Integer> stagesToCheck, List<JSONObject> personalExp) throws NotExistException, FormatException {
        ManagementUser c = cache.getManagerByName(researcherName);
        Experiment exp = c.getExperiment(expId);
        Experiment personal = buildExperiment(personalExp, gradTaskName, c);
        Experiment forExpee = buildExperiment(ExpeeExp, gradTaskName, c);
        cache.addGradingTask(new GradingTask(exp, personal, forExpee));
    }

    @Override
    public void addToPersonal(String researcherName, int expId, String gradTaskName, JSONObject stage) throws NotExistException, FormatException {
        ManagementUser c = cache.getManagerByName(researcherName);
        GradingTask gt = cache.getGradingTaskByName(researcherName, expId, gradTaskName);
        Experiment personal = gt.getGeneralExperiment();
        Stage s = Stage.parseStage(stage, personal);
    }

    @Override
    public void addToResultsExp(String researcherName, int expId, String gradTaskName, JSONObject stage) throws NotExistException, FormatException {
        ManagementUser c = cache.getManagerByName(researcherName);
        GradingTask gt = cache.getGradingTaskByName(researcherName, expId, gradTaskName);
        Experiment resExp = gt.getGradingExperiment();
        Stage s = Stage.parseStage(stage, resExp);
    }

    @Override
    public String setStagesToCheck(String researcherName, int expId, String gradTaskName, List<Integer> stagesToCheck) throws NotExistException {
        ManagementUser c = cache.getManagerByName(researcherName);
        GradingTask gt = cache.getGradingTaskByName(researcherName, expId, gradTaskName);
        //add stagesToCheck to gt
        return "TODO:CreatorBusiness.setStagesToCheck";
        //TODO:implements
    }

    @Override
    public String saveGradingTask(String researcherName, int expId, String gradTaskName) throws NotExistException {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return researcherName + " not exist";
        return "TODO:CreatorBusiness.saveGradingTask";
        //TODO:implements??
    }

    @Override
    public void addAllie(String researcherName, int expId, String allieMail, List<String> permissions) throws NotExistException {
        ManagementUser c = cache.getManagerByName(researcherName);
        Experiment exp = c.getExperiment(expId);

        ManagementUser ally = new ManagementUser();
        ally.setUserEmail(allieMail);

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
        cache.addGraderToGradingTask(gt, grader);
    }

    @Override
    public void addExperimentee(String researcherName, int expId, String expeeMail) throws NotExistException, ExistException {
        ManagementUser c = cache.getManagerByName(researcherName);
        Experiment exp = c.getExperiment(expId);
        if (cache.isExpeeInExperiment(expeeMail, expId)) throw new ExistException(expeeMail,"experiment "+expId);
        cache.addExperimentee(new Experimentee(expeeMail, exp));
    }

    @Override
    public void addExpeeToGrader(String researcherName, int expId, String gradTaskName, String graderMail, String expeeMail) throws NotExistException {
        ManagementUser c = cache.getManagerByName(researcherName);
        Experiment exp = c.getExperiment(expId);
        GradingTask gt = cache.getGradingTaskByName(researcherName, expId, gradTaskName);
        Participant participant = cache.getExpeeByMailAndExp(expeeMail, expId).getParticipant();
        Grader grader = cache.getGraderByEMail(graderMail);
        cache.addExpeeToGradingTask(gt, grader, participant);
    }

    private static Experiment buildExperiment(List<JSONObject> stages, String expName, ManagementUser creator) throws FormatException {
        Experiment exp = new Experiment(expName, creator);
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
