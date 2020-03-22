package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import com.example.demo.BusinessLayer.Entities.Permission;
import com.example.demo.BusinessLayer.Entities.Stages.CodeStage;
import com.example.demo.BusinessLayer.Entities.Stages.InfoStage;
import com.example.demo.BusinessLayer.Entities.Stages.QuestionnaireStage;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CreatorBusiness implements ICreatorBusiness {

    private DataCache cache = DataCache.getInstance();

    @Override
    public boolean researcherLogin(String username, String password) {
        ManagementUser c = cache.getManagerByName(username);
        if (c == null) return false;
        return c.getBguPassword().equals(password);
    }

    @Override
    public boolean createExperiment(String researcherName, String expName) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null || c.hasExperiment(expName)) return false;
        Experiment exp = new Experiment(expName, c);
        c.addExperiment(exp);
        return true;
        //TODO: return value?
    }

    @Override
    public boolean addStageToExperiment(String researcherName, int id, JSONObject stage) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return false;
        Experiment exp = c.getExperiment(id);
        if (exp == null) return false;
        Stage s = parseStage(stage, exp);
        return s != null;
        //TODO: return value?
    }

    @Override
    public boolean saveExperiment(String researcherName, int id) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return false;
        return false;
        //TODO:implements??
    }

    @Override
    public boolean addExperiment(String researcherName, String expName, List<JSONObject> stages) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null || c.hasExperiment(expName)) return false;
        Experiment exp = buildExperiment(stages, expName, c);
        return exp != null;
        //TODO: return value?
    }

    @Override
    public boolean addGradingTask(String researcherName, int expId, String gradTaskName, List<JSONObject> ExpeeExp, List<Integer> stagesToCheck, List<JSONObject> personalExp) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return false;
        Experiment exp = c.getExperiment(expId);
        if (exp == null) return false;
        Experiment personal = buildExperiment(personalExp, gradTaskName, c);
        Experiment forExpee = buildExperiment(ExpeeExp, gradTaskName, c);
        if (personal == null || forExpee == null) return false;
        GradingTask gt = new GradingTask(exp, personal, forExpee);
        cache.addGradingTask(new GraderToGradingTask(gt));
        return true;
        //TODO: return value? and do something with stagesToCheck
    }

    @Override
    public boolean addToPersonal(String researcherName, int expId, String gradTaskName, JSONObject stage) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return false;
        GradingTask gt = cache.getGradingTaskByName(researcherName, expId, gradTaskName);
        if (gt == null) return false;
        Experiment personal = gt.getGeneralExperiment();
        if(personal==null) return false;
        Stage s = parseStage(stage, personal);
        return s != null;
        //TODO: test
    }

    @Override
    public boolean addToResultsExp(String researcherName, int expId, String gradTaskName, JSONObject stage) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return false;
        GradingTask gt = cache.getGradingTaskByName(researcherName, expId, gradTaskName);
        if (gt == null) return false;
        Experiment resTest = gt.getGradingExperiment();
        Stage s = parseStage(stage, resTest);
        return s != null;
        //TODO: test
    }

    @Override
    public boolean setStagesToCheck(String researcherName, int expId, String gradTaskName, List<Integer> stagesToCheck) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return false;
        GradingTask gt = cache.getGradingTaskByName(researcherName, expId, gradTaskName);
        if (gt == null) return false;
        //add stagesToCheck to gt
        return false;
        //TODO:implements
    }

    @Override
    public boolean saveGradingTask(String researcherName, int expId, String gradTaskName) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return false;
        return false;
        //TODO:implements??
    }

    @Override
    public boolean addAllie(String researcherName, int expId, String allieMail, String permissions) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return false;
        Experiment exp = c.getExperiment(expId);
        if (exp == null) return false;

        ManagementUser ally = new ManagementUser();
        ally.setUserEmail(allieMail);
        ally.setPermissions(Set.of(new Permission(permissions, ally)));
        return true;
        //TODO: check
    }

    @Override
    public boolean addGrader(String researcherName, int expId, String gradTaskName, String graderMail) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return false;
        return false;
        //TODO:implements
    }

    @Override
    public boolean addExperimentee(String researcherName, int expId, String ExpeeMail) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return false;
        return false;
        //TODO:implements
    }

    @Override
    public boolean addExpeeToGrader(String researcherName, int expId, String gradTaskName, String graderMail, String ExpeeMail) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return false;
        return false;
        //TODO:implements
    }

    //UC 1.1.* generate the stage and add him to the experiment
    private static Stage parseStage(JSONObject stage, Experiment experiment) {
        try {
            switch ((String) stage.get("type")) {
                case "info":
                    return new InfoStage((String) stage.get("info"), experiment);

                case "code":
                    String desc = (String) stage.get("description");
                    String template = (String) stage.get("template");
                    List<String> requirements = (List<String>) stage.get("requirements");
                    return new CodeStage(desc, template, requirements, experiment);

                case "questionnaire":
                    return new QuestionnaireStage((List<JSONObject>) stage.get("questionnaire"), experiment);
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    private static Experiment buildExperiment(List<JSONObject> stages, String expName, ManagementUser creator) {
        Experiment exp = new Experiment(expName, creator);
        creator.addExperiment(exp);
        for (JSONObject jStage : stages) {
            Stage s = parseStage(jStage, exp);
            if (s == null) {
                creator.removeExp(expName);
                return null;
            }
        }
        return exp;
    }
}
