package com.example.demo.BusinessLayer;


import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.ExistException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import org.json.simple.JSONObject;

import java.util.List;

public interface ICreatorBusiness {

    //Login
    boolean researcherLogin(String username, String password);

    int createExperiment(String researcherName, String expName) throws ExistException, NotExistException;

    //UC 1.1 - one choice (PARTS)
    void addStageToExperiment(String researcherName, int id, JSONObject stage) throws ExistException, FormatException, NotExistException;

    String saveExperiment(String researcherName, int id) throws NotExistException;

    //UC 1.1 - second choice (ALL)
    int addExperiment(String researcherName, String expName, List<JSONObject> stages) throws NotExistException, FormatException, ExistException;

    //UC 1.2 - one choice (ALL)
    int addGradingTask(String researcherName, int expId, String gradTaskName, List<JSONObject> ExpeeExp,
                       List<Integer> stagesToCheck, List<JSONObject> personalExp) throws NotExistException, FormatException;

    //UC 1.2 - second choice (PARTS)
    // the two funcs below can maybe use addStageToExperiment(String researcherName, String expName/taskId, JSONObject stage)
    void addToPersonal(String researcherName, int expId, int taskId, JSONObject stage) throws NotExistException, FormatException;

    void addToResultsExp(String researcherName, int expId, int taskId, JSONObject stage) throws NotExistException, FormatException;

    void setStagesToCheck(String researcherName, int expId, int taskId, List<Integer> stagesToCheck) throws NotExistException;

    String saveGradingTask(String researcherName, int expId, int taskId) throws NotExistException;

    //UC 1.3
    void setAlliePermissions(String researcherName, int expId, String allieMail, String allieRole, List<String> permissions) throws NotExistException;

    void addGrader(String researcherName, int expId, int taskId, String graderMail) throws NotExistException;

    String addExperimentee(String researcherName, int expId, String ExpeeMail) throws NotExistException, ExistException;

    void addExpeeToGrader(String researcherName, int expId, int taskId, String graderMail, String ExpeeMail) throws NotExistException, ExistException;

    //TODO: add meaningful getters: experimentees, grading tasks, graders, stages ... (anything that the creator might want to observe)
    List<Experiment> getExperiments(String username) throws NotExistException;

    List<Stage> getStages(String username, int expId) throws NotExistException;

    List<Participant> getExperimentees(String username, int expId) throws NotExistException;

    List<ManagementUserToExperiment> getAllies(String username, int expId) throws NotExistException;

    List<GradingTask> getGradingTasks(String username, int expId) throws NotExistException;

    List<Stage> getPersonalStages(String username, int expId, int taskId) throws NotExistException;

    List<Stage> getEvaluationStages(String username, int expId, int taskId) throws NotExistException;

    List<Grader> getTaskGraders(String username, int expId, int taskId) throws NotExistException;

    List<Participant> getTaskExperimentees(String username, int expId, int taskId) throws NotExistException;
}
