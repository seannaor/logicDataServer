package com.example.demo.BusinessLayer;


import com.example.demo.BusinessLayer.Exceptions.ExistException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import org.json.simple.JSONObject;

import java.util.List;

public interface ICreatorBusiness {

    //TODO: each return value would change from bool to string or something more meaningful

    //Login
    boolean researcherLogin(String username, String password);

    void createExperiment(String researcherName, String expName) throws ExistException, NotExistException;

    //UC 1.1 - one choice (PARTS)
    void addStageToExperiment(String researcherName, int id, JSONObject stage) throws ExistException, FormatException, NotExistException;
    String saveExperiment(String researcherName, int id) throws NotExistException;

    //UC 1.1 - second choice (ALL)
    void addExperiment(String researcherName, String expName, List<JSONObject> stages) throws NotExistException, FormatException, ExistException;

    //UC 1.2 - one choice (ALL)
    void addGradingTask(String researcherName, int expId, String gradTaskName, List<JSONObject> ExpeeExp,
                        List<Integer> stagesToCheck, List<JSONObject> personalExp) throws NotExistException, FormatException;

    //UC 1.2 - second choice (PARTS)
    // the two funcs below can maybe use addStageToExperiment(String researcherName, String expName/gradTaskName, JSONObject stage)
    void addToPersonal(String researcherName, int expId, String gradTaskName, JSONObject stage) throws NotExistException, FormatException;
    void addToResultsExp(String researcherName, int expId, String gradTaskName, JSONObject stage) throws NotExistException, FormatException;
    String setStagesToCheck(String researcherName, int expId, String gradTaskName,List<Integer> stagesToCheck)throws NotExistException;
    String saveGradingTask(String researcherName, int expId, String gradTaskName)throws NotExistException;

    //UC 1.3
    void addAllie(String researcherName, int expId, String allieMail, List<String> permissions)throws NotExistException;

    void addGrader(String researcherName, int expId, String gradTaskName, String graderMail)throws NotExistException;

    void addExperimentee(String researcherName, int expId, String ExpeeMail) throws NotExistException, ExistException;

    void addExpeeToGrader(String researcherName, int expId, String gradTaskName, String graderMail, String ExpeeMail) throws NotExistException;

    //TODO: add meaningful getters: experimentees, grading tasks, graders, stages ... (anything that the creator might want to observe)
}
