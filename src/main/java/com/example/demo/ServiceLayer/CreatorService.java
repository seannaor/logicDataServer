package com.example.demo.ServiceLayer;

import com.example.demo.BusinessLayer.CreatorBusiness;
import com.example.demo.BusinessLayer.ICreatorBusiness;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class CreatorService implements IService{
    private ICreatorBusiness creatorBusiness = new CreatorBusiness();

    //Login
    public Map<String,Object> researcherLogin(String username, String password) {
        return Map.of("response", creatorBusiness.researcherLogin(username, password));
    }

    public Map<String,Object> createExperiment(String researcherName, String expName) {
        return null;
    }

    //UC 1.1 - one choice (PARTS)
    public Map<String,Object> addStageToExperiment(String researcherName, int id, JSONObject stage) {
        return null;
    }

    public Map<String,Object> saveExperiment(String researcherName, int id) {
        return null;
    }

    //UC 1.1 - second choice (ALL)
    public Map<String,Object> addExperiment(String researcherName, String expName, List<JSONObject> stages) {
        return null;
    }

    //UC 1.2 - one choice (ALL)
    public Map<String,Object> addGradingTask(String researcherName, int expId, String gradTaskName, List<JSONObject> ExpeeExp,
                                     List<Integer> stagesToCheck, List<JSONObject> personalExp) {
        return null;
    }

    //UC 1.2 - second choice (PARTS)
    // the two funcs below can maybe use addStageToExperiment(String researcherName, String expName/gradTaskName, JSONObject stage)
    public Map<String,Object> addToPersonal(String researcherName, int expId, String gradTaskName, JSONObject stage) {
        return null;
    }

    public Map<String,Object> addToResultsExp(String researcherName, int expId, String gradTaskName, JSONObject stage) {
        return null;
    }

    public Map<String,Object> setStagesToCheck(String researcherName, int expId, String gradTaskName, List<Integer> stagesToCheck) {
        return null;
    }

    public Map<String,Object> saveGradingTask(String researcherName, int expId, String gradTaskName) {
        return null;
    }

    //UC 1.3
    public Map<String,Object> addAllie(String researcherName, int expId, String allieMail, List<String> permissions) {
        return null;
    }

    public Map<String,Object> addGrader(String researcherName, int expId, String gradTaskName, String graderMail) {
        return null;
    }

    public Map<String,Object> addExperimentee(String researcherName, int expId, String ExpeeMail) {
        return null;
    }

    public Map<String,Object> addExpeeToGrader(String researcherName, int expId, String gradTaskName, String graderMail, String ExpeeMail) {
        return null;
    }

    public Map<String,Object> requestProcessor(Map<String,Object> map) {
        String op = (String) map.get("operation");
        if(op==null) op = "null";
        switch (op) {
            case "researcherLogin":
                return researcherLogin((String) map.get("username"), (String) map.get("password"));
            case "createExperiment":
                return createExperiment((String) map.get("username"), (String) map.get("expName"));
            case "addStageToExperiment":
                return addStageToExperiment((String) map.get("username"), (int) map.get("expId"), (JSONObject) map.get("stage"));
            case "saveExperiment":
                return saveExperiment((String) map.get("username"), (int) map.get("expId"));
            case "addExperiment":
                return addExperiment((String) map.get("username"), (String) map.get("expName"), (List<JSONObject>) map.get("stages"));
            case "addGradingTask":
                return addGradingTask((String) map.get("username"), (int) map.get("expId"), (String) map.get("taskName"),
                        (List<JSONObject>) map.get("expeeStages"), (List<Integer>) map.get("stagesToCheck"), (List<JSONObject>) map.get("personalStage"));
            case "addToPersonal":
                return addToPersonal((String) map.get("username"), (int) map.get("expId"),
                        (String) map.get("taskName"), (JSONObject) map.get("stage"));
            case "addToResultsExp":
                return addToResultsExp((String) map.get("username"), (int) map.get("expId"),
                        (String) map.get("taskName"), (JSONObject) map.get("stage"));
            case "setStagesToCheck":
                return setStagesToCheck((String) map.get("username"), (int) map.get("expId"),
                        (String) map.get("taskName"), (List<Integer>) map.get("stagesToCheck"));
            case "saveGradingTask":
                return saveGradingTask((String) map.get("username"), (int) map.get("expId"), (String) map.get("taskName"));
            case "addAllie":
                return addAllie((String) map.get("username"), (int) map.get("expId"),(String) map.get("mail"),(List<String>)map.get("permissions"));
            case "addGrader":
                return addGrader((String) map.get("username"), (int) map.get("expId"),
                        (String) map.get("taskName"),(String) map.get("mail"));
            case "addExperimentee":
                return addExperimentee((String) map.get("username"), (int) map.get("expId"),(String) map.get("mail"));
            case "addExpeeToGrader":
               return  addExpeeToGrader((String) map.get("username"), (int) map.get("expId"),
                       (String) map.get("taskName"),(String) map.get("graderMail"),(String) map.get("expeeMail"));
            default:
                return Map.of("response","operation not found");
        }
    }
}
