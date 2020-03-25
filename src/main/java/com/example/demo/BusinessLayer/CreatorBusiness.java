package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import com.example.demo.BusinessLayer.Entities.Stages.CodeStage;
import com.example.demo.BusinessLayer.Entities.Stages.InfoStage;
import com.example.demo.BusinessLayer.Entities.Stages.QuestionnaireStage;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import net.minidev.json.JSONObject;

import java.util.List;

public class CreatorBusiness implements ICreatorBusiness {
    @Override
    public boolean researcherLogin(String username, String password) {
        ManagementUser c = getCreator(username);
        if(c==null) return false;
        return c.getBguPassword().equals(password);
    }

    @Override
    public boolean addStageToExperiment(String researcherName, String expName, JSONObject stage) {
        ManagementUser c = getCreator(researcherName);
        if(c==null) return false;
        Experiment exp = c.getExperiment(expName);

        return false;
    }

    @Override
    public boolean saveExperiment(String researcherName, String expName) {
        ManagementUser c = getCreator(researcherName);
        if(c==null) return false;
        return false;
    }

    @Override
    public boolean addExperiment(String researcherName, String expName, List<JSONObject> stages) {
        ManagementUser c = getCreator(researcherName);
        if(c==null) return false;
        return false;
    }

    @Override
    public boolean addGradingTask(String researcherName, String expName, String gradTaskName, List<JSONObject> ExpeeExp, List<Integer> stagesToCheck, List<JSONObject> personalExp) {
        ManagementUser c = getCreator(researcherName);
        if(c==null) return false;
        return false;
    }

    @Override
    public boolean addToPersonal(String researcherName, String expName, String gradTaskName, JSONObject stage) {
        ManagementUser c = getCreator(researcherName);
        if(c==null) return false;
        return false;
    }

    @Override
    public boolean addToResultsExp(String researcherName, String expName, String gradTaskName, JSONObject stage) {
        ManagementUser c = getCreator(researcherName);
        if(c==null) return false;
        return false;
    }

    @Override
    public boolean setStagesToCheck(String researcherName, String expName, String gradTaskName, List<Integer> stagesToCheck) {
        ManagementUser c = getCreator(researcherName);
        if(c==null) return false;
        return false;
    }

    @Override
    public boolean saveGradingTask(String researcherName, String expName, String gradTaskName) {
        ManagementUser c = getCreator(researcherName);
        if(c==null) return false;
        return false;
    }

    @Override
    public boolean addAllie(String researcherName, String expName, String allieMail, String permissions) {
        ManagementUser c = getCreator(researcherName);
        if(c==null) return false;
        return false;
    }

    @Override
    public boolean addGrader(String researcherName, String expName, String gradTaskName, String graderMail) {
        ManagementUser c = getCreator(researcherName);
        if(c==null) return false;
        return false;
    }

    @Override
    public boolean addExperimentee(String researcherName, String expName, String ExpeeMail) {
        ManagementUser c = getCreator(researcherName);
        if(c==null) return false;
        return false;
    }

    @Override
    public boolean addExpeeToGrader(String researcherName, String expName, String gradTaskName, String graderMail, String ExpeeMail) {
        ManagementUser c = getCreator(researcherName);
        if(c==null) return false;
        return false;
    }

    private ManagementUser getCreator(String name){
        return null;
    }

    //UC 1.1.*
    private static Stage parseStage(JSONObject stage, Experiment experiment) {
        switch ((String) stage.get("type")) {
            case "info":
                return new InfoStage((String) stage.get("info"), experiment);
//            case "code":
//                String desc = (String) stage.get("description");
//                String template = (String) stage.get("template");
//                List<String> requirements = (List<String>) stage.get("requirements");
//                return new CodeStage(desc, template, requirements, experiment);
//            case "questionnaire":
//                return new QuestionnaireStage((List<JSONObject>) stage.get("questionnaire"), experiment);
        }
        return null;
    }
}
