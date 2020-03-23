package com.example.demo.BusinessLayer;

import net.minidev.json.JSONObject;

import java.util.List;

public interface ICreatorBusiness {

    //TODO: each return value would change from bool to string or something more meaningful

    //Login
    boolean researcherLogin(String username, String password);

    String createExperiment(String researcherName, String expName);

    //UC 1.1 - one choice (PARTS)
    String addStageToExperiment(String researcherName, int id, JSONObject stage);
    String saveExperiment(String researcherName, int id);

    //UC 1.1 - second choice (ALL)
    String addExperiment(String researcherName, String expName, List<JSONObject> stages);

    //UC 1.2 - one choice (ALL)
    String addGradingTask(String researcherName, int expId, String gradTaskName, List<JSONObject> ExpeeExp,
                           List<Integer> stagesToCheck, List<JSONObject> personalExp);

    //UC 1.2 - second choice (PARTS)
    // the two funcs below can maybe use addStageToExperiment(String researcherName, String expName/gradTaskName, JSONObject stage)
    String addToPersonal(String researcherName, int expId, String gradTaskName, JSONObject stage);
    String addToResultsExp(String researcherName, int expId, String gradTaskName, JSONObject stage);
    String setStagesToCheck(String researcherName, int expId, String gradTaskName,List<Integer> stagesToCheck);
    String saveGradingTask(String researcherName, int expId, String gradTaskName);

    //UC 1.3
    String addAllie(String researcherName, int expId, String allieMail, List<String> permissions);

    String addGrader(String researcherName, int expId, String gradTaskName, String graderMail);

    String addExperimentee(String researcherName, int expId, String ExpeeMail);

    String addExpeeToGrader(String researcherName, int expId, String gradTaskName, String graderMail, String ExpeeMail);

    //TODO: add meaningful getters: experimentees, grading tasks, graders, stages ... (anything that the creator might want to observe)
}
