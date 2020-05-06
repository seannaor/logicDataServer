package com.example.demo.ServiceLayer;

import com.example.demo.BusinessLayer.CreatorBusiness;
import com.example.demo.BusinessLayer.Exceptions.ExistException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import com.example.demo.BusinessLayer.ICreatorBusiness;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class CreatorService {
    private ICreatorBusiness creatorBusiness;

    public CreatorService() {
        this.creatorBusiness = new CreatorBusiness();
    }

    //Login
    public Map<String, Object> researcherLogin(String username, String password) {
        return Map.of("response", creatorBusiness.researcherLogin(username, password));
    }

    public Map<String, Object> createExperiment(String researcherName, String expName) {
        try {
            return Map.of("response", "OK", "id", creatorBusiness.createExperiment(researcherName, expName));
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
    }

    //UC 1.1 - one choice (PARTS)
    public Map<String, Object> addStageToExperiment(String researcherName, int id, JSONObject stage) {
        String res = "OK";
        try {
            creatorBusiness.addStageToExperiment(researcherName, id, stage);
        } catch (Exception e) {
            res = e.getMessage();
        }
        return Map.of("response", res);
    }

    public Map<String, Object> saveExperiment(String researcherName, int id) {
        return null;
    }

    //UC 1.1 - second choice (ALL)
    public Map<String, Object> addExperiment(String researcherName, String expName, List<JSONObject> stages) {
        try {
            return Map.of("response", "OK", "id", creatorBusiness.addExperiment(researcherName, expName, stages));
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
    }

    //UC 1.2 - one choice (ALL)
    public Map<String, Object> addGradingTask(String researcherName, int expId, String gradTaskName, List<JSONObject> ExpeeExp,
                                              List<Integer> stagesToCheck, List<JSONObject> personalExp) {
        try {
            return Map.of("response", "OK", "id",
                    creatorBusiness.addGradingTask(researcherName, expId, gradTaskName, ExpeeExp, stagesToCheck, personalExp));
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
    }

    //UC 1.2 - second choice (PARTS)
    // the two funcs below can maybe use addStageToExperiment(String researcherName, String expName/gradTaskName, JSONObject stage)
    public Map<String, Object> addToPersonal(String researcherName, int expId, int taskId, JSONObject stage) {
        String res = "OK";
        try {
            creatorBusiness.addToPersonal(researcherName, expId, taskId, stage);
        } catch (Exception e) {
            res = e.getMessage();
        }
        return Map.of("response", res);
    }

    public Map<String, Object> addToResultsExp(String researcherName, int expId, int taskId, JSONObject stage) {
        String res = "OK";
        try {
            creatorBusiness.addToResultsExp(researcherName, expId, taskId, stage);
        } catch (Exception e) {
            res = e.getMessage();
        }
        return Map.of("response", res);
    }

    public Map<String, Object> setStagesToCheck(String researcherName, int expId, int taskId, List<Integer> stagesToCheck) {
        String res = "OK";
        try {
            creatorBusiness.setStagesToCheck(researcherName, expId, taskId, stagesToCheck);
        } catch (Exception e) {
            res = e.getMessage();
        }
        return Map.of("response", res);
    }

    public Map<String, Object> saveGradingTask(String researcherName, int expId, String gradTaskName) {
        return null;
    }

    //UC 1.3
    public Map<String, Object> setAlliePermissions(String researcherName, int expId, String allieMail, String role, List<String> permissions) {
        String res = "OK";
        try {
            creatorBusiness.setAlliePermissions(researcherName, expId, allieMail, role, permissions);
        } catch (Exception e) {
            res = e.getMessage();
        }
        return Map.of("response", res);
    }

    public Map<String, Object> addGrader(String researcherName, int expId, int taskId, String graderMail) {
        String res = "OK";
        try {
            creatorBusiness.addGrader(researcherName, expId, taskId, graderMail);
        } catch (Exception e) {
            res = e.getMessage();
        }
        return Map.of("response", res);
    }

    public Map<String, Object> addExperimentee(String researcherName, int expId, String ExpeeMail) {
        try {
            String code = creatorBusiness.addExperimentee(researcherName, expId, ExpeeMail);
            return Map.of("response","OK","code",code);
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
    }

    public Map<String, Object> addExpeeToGrader(String researcherName, int expId, int taskId, String graderMail, String ExpeeMail) {
        String res = "OK";
        try {
            creatorBusiness.addExpeeToGrader(researcherName, expId, taskId, graderMail, ExpeeMail);
        } catch (Exception e) {
            res = e.getMessage();
        }
        return Map.of("response", res);
    }
}
