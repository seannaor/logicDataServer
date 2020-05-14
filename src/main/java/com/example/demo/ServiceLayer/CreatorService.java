package com.example.demo.ServiceLayer;

import com.example.demo.BusinessLayer.CreatorBusiness;
import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CreatorService {
    @Autowired
    private CreatorBusiness creatorBusiness;

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

    public Map<String, Object> addGraderToGradingTask(String researcherName, int expId, int taskId, String graderMail) {
        try {
            return Map.of("response","OK","code",creatorBusiness.addGraderToGradingTask(researcherName, expId, taskId, graderMail));
        } catch (Exception e) {
            return  Map.of("response", e.getMessage());
        }
    }

    public Map<String, Object> addExperimentee(String researcherName, int expId, String ExpeeMail) {
        try {
            String code = creatorBusiness.addExperimentee(researcherName, expId, ExpeeMail);
            return Map.of("response", "OK", "code", code);
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
    }

    public Map<String, Object> addExpeeToGrader(String researcherName, int expId, int taskId, String graderMail, String expeeMail) {
        String res = "OK";
        try {
            creatorBusiness.addExpeeToGrader(researcherName, expId, taskId, graderMail, expeeMail);
        } catch (Exception e) {
            res = e.getMessage();
        }
        return Map.of("response", res);
    }

    // meaningful getters
    public Map<String, Object> getExperiments(String username) {
        List<Experiment> exps;
        try {
            exps = creatorBusiness.getExperiments(username);
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
        List<Integer> ids = new ArrayList<>();
        for (Experiment exp : exps) {
            ids.add(exp.getExperimentId());
        }
        return Map.of("response", "OK", "experiments", ids);
    }

    public Map<String, Object> getStages(String username, int exp_id) {
        List<Stage> stages;
        try {
            stages = creatorBusiness.getStages(username, exp_id);
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
        return stagesResponse(stages);
    }

    public Map<String, Object> getExperimentees(String username, int exp_id) {
        List<Participant> expees;
        try {
            expees = creatorBusiness.getExperimentees(username, exp_id);
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
        List<Integer> ids = new ArrayList<>();
        for (Participant expee : expees) {
            ids.add(expee.getParticipantId());
        }
        return Map.of("response", "OK", "experimentees", ids);
    }

    public Map<String, Object> getAllies(String username, int exp_id) {
        List<ManagementUserToExperiment> allies;
        try {
            allies = creatorBusiness.getAllies(username, exp_id);
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
        List<Map<String, String>> jsons = new ArrayList<>();
        for (ManagementUserToExperiment ally : allies) {
            jsons.add(Map.of("username", ally.getManagementUser().getBguUsername(), "role", ally.getRole()));
        }
        return Map.of("response", "OK", "allies", jsons);
    }

    public Map<String, Object> getGradingTasks(String username, int exp_id) {
        List<GradingTask> tasks;
        try {
            tasks = creatorBusiness.getGradingTasks(username, exp_id);
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
        List<Integer> ids = new ArrayList<>();
        for (GradingTask task : tasks) {
            ids.add(task.getGradingTaskId());
        }
        return Map.of("response", "OK", "tasks", ids);
    }

    public Map<String, Object> getPersonalStages(String username, int exp_id, int taskId) {
        List<Stage> stages;
        try {
            stages = creatorBusiness.getPersonalStages(username, exp_id, taskId);
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
        return stagesResponse(stages);
    }

    public Map<String, Object> getEvaluationStages(String username, int exp_id, int taskId) {
        List<Stage> stages;
        try {
            stages = creatorBusiness.getEvaluationStages(username, exp_id, taskId);
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
        return stagesResponse(stages);
    }

    public Map<String, Object> getTaskGraders(String username, int exp_id, int taskId) {
        List<Grader> graders;
        try {
            graders = creatorBusiness.getTaskGraders(username, exp_id, taskId);
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
        return Map.of("response", "OK", "graders", graders);
    }

    public Map<String, Object> getTaskExperimentees(String username, int exp_id, int taskId) {
        List<Participant> expees;
        try {
            expees = creatorBusiness.getTaskExperimentees(username, exp_id, taskId);
        } catch (Exception e) {
            return Map.of("response", e.getMessage());
        }
        List<Integer> ids = new ArrayList<>();
        for (Participant expee : expees) {
            ids.add(expee.getParticipantId());
        }
        return Map.of("response", "OK", "experimentees", ids);
    }

    // Utils

    private Map<String, Object> stagesResponse(List<Stage> stages) {
        List<JSONObject> jsons = new ArrayList<>();
        for (Stage stage : stages) {
            jsons.add(stage.getJson());
        }
        return Map.of("response", "OK", "stages", jsons);
    }
}
