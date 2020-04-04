package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.Stages.CodeStage;
import com.example.demo.BusinessLayer.Entities.Stages.InfoStage;
import com.example.demo.BusinessLayer.Entities.Stages.QuestionnaireStage;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
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
    public String createExperiment(String researcherName, String expName) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if(c==null) return researcherName+" not exist";
        if (c.getExperimentByName(expName)!=null) return expName +" already exist";
        Experiment exp = new Experiment(expName, c);
        c.addExperiment(exp);
        return "new experiment - "+expName+" was created";
        //TODO: return value?
    }

    @Override
    public String addStageToExperiment(String researcherName, int id, JSONObject stage) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return researcherName+" not exist";
        Experiment exp = c.getExperiment(id);
        if (exp == null) return "experiment with id "+id+" not found";
        Stage s = parseStage(stage, exp);
        if(s==null) return "stage is not in the right format";
        return "new stage was added to the experiment";
        //TODO: return value?
    }

    @Override
    public String saveExperiment(String researcherName, int id) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return researcherName+" not exist";

        return "TODO: CreatorBusiness.saveExperiment";
        //TODO:implements??
    }

    @Override
    public String addExperiment(String researcherName, String expName, List<JSONObject> stages) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if(c==null) return researcherName+" not exist";
        if (c.getExperimentByName(expName)!=null) return expName +" already exist";
        Experiment exp = buildExperiment(stages, expName, c);
        if(exp==null) return "a stage is not in the right format";
        return "new experiment - "+expName+" was created";
        //TODO: return value?
    }

    @Override
    public String addGradingTask(String researcherName, int expId, String gradTaskName, List<JSONObject> ExpeeExp, List<Integer> stagesToCheck, List<JSONObject> personalExp) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null)  return researcherName+" not exist";
        Experiment exp = c.getExperiment(expId);
        if (exp == null) return "experiment with id "+expId+" not found";
        Experiment personal = buildExperiment(personalExp, gradTaskName, c);
        Experiment forExpee = buildExperiment(ExpeeExp, gradTaskName, c);
        if (personal == null || forExpee == null) return "a stage is not in the right format";
        cache.addGradingTask(new GradingTask(exp, personal, forExpee));
        return "new grading task - "+gradTaskName+" was created";
        //TODO: return value? and do something with stagesToCheck
    }

    @Override
    public String addToPersonal(String researcherName, int expId, String gradTaskName, JSONObject stage) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return researcherName+" not exist";
        GradingTask gt = cache.getGradingTaskByName(researcherName, expId, gradTaskName);
        if (gt == null) return "grading task "+gradTaskName+" not found";
        Experiment personal = gt.getGeneralExperiment();
        if (personal == null) return "personal experiment was not found";
        Stage s = parseStage(stage, personal);
        if(s==null) return "stage is not in the right format";
        return "new stage was added to the personal experiment";
        //TODO: test
    }

    @Override
    public String addToResultsExp(String researcherName, int expId, String gradTaskName, JSONObject stage) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return researcherName+" not exist";
        GradingTask gt = cache.getGradingTaskByName(researcherName, expId, gradTaskName);
        if (gt == null) return "grading task "+gradTaskName+" not found";
        Experiment resExp = gt.getGradingExperiment();
        if (resExp == null) return "evaluation experiment was not found";
        Stage s = parseStage(stage, resExp);
        if(s==null) return "stage is not in the right format";
        return "new stage was added to the evaluation experiment";
        //TODO: test
    }

    @Override
    public String setStagesToCheck(String researcherName, int expId, String gradTaskName, List<Integer> stagesToCheck) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return researcherName+" not exist";
        GradingTask gt = cache.getGradingTaskByName(researcherName, expId, gradTaskName);
        if (gt == null) return "grading task "+gradTaskName+" not found";
        //add stagesToCheck to gt
        return "TODO:CreatorBusiness.setStagesToCheck";
        //TODO:implements
    }

    @Override
    public String saveGradingTask(String researcherName, int expId, String gradTaskName) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return researcherName+" not exist";
        return "TODO:CreatorBusiness.saveGradingTask";
        //TODO:implements??
    }

    @Override
    public String addAllie(String researcherName, int expId, String allieMail, List<String> permissions) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return researcherName+" not exist";
        Experiment exp = c.getExperiment(expId);
        if (exp == null) return "experiment with id "+expId+" not found";

        ManagementUser ally = new ManagementUser();
        ally.setUserEmail(allieMail);

        List<Permission> pers = new ArrayList<>();
        for (String per : permissions) {
            pers.add(new Permission(per,ally));
        }
        ally.setPermissions(pers);
        return "permissions was set for the user - "+allieMail;
        //TODO: check
    }

    @Override
    public String addGrader(String researcherName, int expId, String gradTaskName, String graderMail) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return researcherName+" not exist";
        GradingTask gt = cache.getGradingTaskByName(researcherName, expId, gradTaskName);
        if (gt == null) return "grading task "+gradTaskName+" not found";
        Grader grader = cache.getGraderByEMail(graderMail);
        if(grader==null){
            grader = new Grader(graderMail,gt.getBaseExperiment());
            cache.addGrader(grader);
        }
        cache.addGraderToGradingTask(gt,grader);
        return graderMail+ " was added to grading task "+gradTaskName;
        //TODO:test
    }

    @Override
    public String addExperimentee(String researcherName, int expId, String expeeMail) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null)  return researcherName+" not exist";
        Experiment exp = c.getExperiment(expId);
        if (exp == null) return "experiment with id "+expId+" not found";
        if(cache.isExpeeInExperiment(expeeMail,expId)) return expeeMail +" already participating this experiment";
        cache.addExperimentee(new Experimentee(expeeMail,exp));
        return expeeMail+" was added to the experiment with id "+expId;
        //TODO:test & maybe send a mail to expeeMail
    }

    @Override
    public String addExpeeToGrader(String researcherName, int expId, String gradTaskName, String graderMail, String expeeMail) {
        ManagementUser c = cache.getManagerByName(researcherName);
        if (c == null) return researcherName+" not exist";
        Experiment exp = c.getExperiment(expId);
        if (exp == null) return "experiment with id "+expId+" not found";
        GradingTask gt = cache.getGradingTaskByName(researcherName, expId, gradTaskName);
        if (gt == null) return "grading task "+gradTaskName+" not found";
        if(!cache.isExpeeInExperiment(expeeMail,expId)) return expeeMail +" not found";
//        {
//            cache.addExperimentee(new Experimentee(expeeMail,exp));
//        }
        Participant participant = cache.getExpeeByMailAndExp(expeeMail,expId).getParticipant();
        Grader grader = cache.getGraderByEMail(graderMail);
        if(grader==null)return graderMail+" not found";
        cache.addExpeeToGradingTask(gt,grader,participant);

        return expeeMail +" was added for "+graderMail+" to grade";
        //TODO:test
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
                    return new QuestionnaireStage((List<JSONObject>) stage.get("questions"), experiment);
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
