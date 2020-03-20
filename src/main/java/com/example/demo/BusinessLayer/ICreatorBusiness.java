package com.example.demo.BusinessLayer;

import net.minidev.json.JSONObject;

import java.util.List;

public interface ICreatorBusiness {

    //Login
    boolean researcherLogin(String username, String password);

    boolean createExperiment(String researcherName, String expName);

    //UC 1.1 - one choice (PARTS)
    boolean addStageToExperiment(String researcherName, int id, JSONObject stage);
    boolean saveExperiment(String researcherName, int id);

    //UC 1.1 - second choice (ALL)
    boolean addExperiment(String researcherName, String expName, List<JSONObject> stages);

    //UC 1.2 - one choice (ALL)
    boolean addGradingTask(String researcherName, int expId, String gradTaskName, List<JSONObject> ExpeeExp,
                           List<Integer> stagesToCheck, List<JSONObject> personalExp);

    //UC 1.2 - second choice (PARTS)
    // the two funcs below can maybe use addStageToExperiment(String researcherName, String expName/gradTaskName, JSONObject stage)
    boolean addToPersonal(String researcherName, int expId, String gradTaskName, JSONObject stage);
    boolean addToResultsExp(String researcherName, int expId, String gradTaskName, JSONObject stage);
    boolean setStagesToCheck(String researcherName, int expId, String gradTaskName,List<Integer> stagesToCheck);
    boolean saveGradingTask(String researcherName, int expId, String gradTaskName);

    //UC 1.3
    boolean addAllie(String researcherName, int expId, String allieMail, String permissions);

    boolean addGrader(String researcherName, int expId, String gradTaskName, String graderMail);

    boolean addExperimentee(String researcherName, int expId, String ExpeeMail);

    boolean addExpeeToGrader(String researcherName, int expId, String gradTaskName, String graderMail, String ExpeeMail);

    //TODO: add meaningful getters: experimentees, grading tasks, graders, stages ... (anything that the creator might want to observe)
}
