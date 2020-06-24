package com.example.demo.ServiceTests;

import com.example.demo.BusinessLayer.CreatorBusiness;
import com.example.demo.BusinessLayer.DataCache;
import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.Exceptions.ExistException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import com.example.demo.DBAccess;
import com.example.demo.ServiceLayer.CreatorService;
import com.example.demo.Utils;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Sql({"/create_database.sql"})
@SpringBootTest
public class CreatorServiceTests {
    private final CreatorService creatorService;
    private final CreatorBusiness creatorBusiness;
    private final DataCache cache;
    private final DBAccess db;
    private ManagementUser manager;
    private ManagementUser ally;
    private Experiment experiment;
    private Experimentee expee;
    private GradingTask task;
    private Grader grader;
    private String graderCode;
    @Autowired
    public CreatorServiceTests(CreatorService creatorService, CreatorBusiness creatorBusiness, DataCache cache, DBAccess db) {
        this.creatorService = creatorService;
        this.creatorBusiness = creatorBusiness;
        this.cache = cache;
        this.db = db;
    }

    @BeforeEach
    private void init() throws NotExistException, FormatException, ExistException {
        db.deleteData();
        cache.setCache();
        manager = new ManagementUser("smorad", "sm_pass", "smorad@post.bgu.ac.il");
        cache.addManager(manager);
        experiment = Utils.buildExp(creatorBusiness, manager);
        creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), "gili@post.bgu.ac.il");
        expee = cache.getExpeeByMailAndExp("gili@post.bgu.ac.il", experiment.getExperimentId());
        int gradingTaskId = creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(),
                "The Grading Task", new ArrayList<>(), List.of(), new ArrayList<>());
        task = cache.getGradingTaskById(manager.getBguUsername(), experiment.getExperimentId(), gradingTaskId);
        graderCode = creatorBusiness.addGraderToGradingTask(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), "grader@post.bgu.ac.il");
        grader = cache.getGraderByEMail("grader@post.bgu.ac.il");
        String ally_mail = "ally@post.bgu.ac.il";
        creatorBusiness.addAlly(manager.getBguUsername(), ally_mail, List.of());
        ally = cache.getManagerByEMail(ally_mail);
        ally.setBguPassword("ally_pass");
    }

    @Test
    public void researcherLoginTest() {
        // not the user password - should fail
        assertFalse(creatorService.researcherLogin(manager.getBguUsername(), "not the password"));
        // real password - should pass
        assertTrue(creatorService.researcherLogin(manager.getBguUsername(), manager.getBguPassword()));
        assertEquals(db.getManagementUserByName(manager.getBguUsername()).getBguPassword(), manager.getBguPassword());
        // username not exist - should fail
        assertFalse(creatorService.researcherLogin("some not exist username", "a password"));
    }

    @Test
    public void createExperimentTest() throws NotExistException {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.createExperiment("something", "something");
        assertNotEquals("OK", ansWrong.get("response"));
        Map<String, Object> ansRight = creatorService.createExperiment(manager.getBguUsername(), "something");
        assertEquals("OK", ansRight.get("response"));
        int expId = (Integer) ansRight.get("id");
        int toValidateId = -1;
        for (Experiment exp : creatorBusiness.getExperiments(manager.getBguUsername())) {
            if (exp.getExperimentName().equals("something")) {
                toValidateId = exp.getExperimentId();
                break;
            }
        }

        assertEquals(expId, toValidateId);
    }

    @Test
    public void addStageToExperimentNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.addStageToExperiment("something", experiment.getExperimentId(), new JSONObject());
        assertNotEquals("OK", ansWrong.get("response"));
        // experiment does not exist - should fail
        ansWrong = creatorService.addStageToExperiment(manager.getBguUsername(), 9090, new JSONObject());
        assertNotEquals("OK", ansWrong.get("response"));
        //bad stage representation
        ansWrong = creatorService.addStageToExperiment(manager.getBguUsername(), experiment.getExperimentId(), new JSONObject());
        assertNotEquals("OK", ansWrong.get("response"));
    }

    @Test
    public void addStageToExperimentPositiveTest() {
        JSONObject stage = new JSONObject();
        stage.put("type", "info");
        stage.put("info", "some information and stuff");
        int stagesBefore = experiment.getStages().size();
        Map<String, Object> ansRight = creatorService.addStageToExperiment(
                manager.getBguUsername(), experiment.getExperimentId(), stage);
        assertEquals("OK", ansRight.get("response"));
        assertEquals(experiment.getStages().size(), stagesBefore + 1);
    }

    @Test
    public void addExperimentNegativeTest() {
        List<JSONObject> stages1 = Utils.buildStages();
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.addExperiment("something", "something", stages1);
        assertNotEquals("OK", ansWrong.get("response"));
        //bad stages representation
        List<JSONObject> stages2 = new ArrayList<>();
        stages2.add(new JSONObject());
        ansWrong = creatorService.addExperiment(manager.getBguUsername(), "something", stages2);
        assertNotEquals("OK", ansWrong.get("response"));
    }

    @Test
    public void addExperimentPositiveTest() throws NotExistException {
        List<JSONObject> stages = Utils.buildStages();
        Map<String, Object> ansRight = creatorService.addExperiment(manager.getBguUsername(), "something", stages);
        assertEquals("OK", ansRight.get("response"));
        int toValidateId = -1;
        for (Experiment exp : creatorBusiness.getExperiments(manager.getBguUsername()))
            if (exp.getExperimentName().equals("something")) {
                toValidateId = exp.getExperimentId();
                break;
            }
    }

    @Test
    public void addGradingTaskNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.addGradingTask("something", experiment.getExperimentId(), "some grading task", List.of(), List.of(), List.of());
        assertNotEquals("OK", ansWrong.get("response"));
        // experiment id does not exists
        ansWrong = creatorService.addGradingTask(manager.getBguUsername(), 9090, "some grading task", List.of(), List.of(), List.of());
        assertNotEquals("OK", ansWrong.get("response"));
        // bad grading experiment
        List<JSONObject> gradingStages = new ArrayList<>();
        gradingStages.add(new JSONObject());
        ansWrong = creatorService.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "some grading task", gradingStages, List.of(), List.of());
        assertNotEquals("OK", ansWrong.get("response"));
        // bad stages visible for grader
        ansWrong = creatorService.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "some grading task", List.of(), List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), List.of());
        assertNotEquals("OK", ansWrong.get("response"));
    }

    @Test
    public void addGradingTaskPositiveTest() throws NotExistException {
        List<JSONObject> gradingExp = List.of(Utils.getStumpInfoStage());
        List<JSONObject> personalExp = List.of(Utils.getStumpQuestionsStage());
        Map<String, Object> ansRight = creatorService.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "some grading task", gradingExp, List.of(1, 2, 3), personalExp);
        assertEquals("OK", ansRight.get("response"));
        int gradingTaskId = (Integer) ansRight.get("id");
        boolean found = false;
        for (GradingTask gt : creatorBusiness.getGradingTasks(manager.getBguUsername(), experiment.getExperimentId())) {
            if (gt.getGradingTaskId() == gradingTaskId) {
                found = true;
                assertEquals(gt.getStages().size(), 3);
                assertEquals(gt.getGeneralExperiment().getStages().size(), personalExp.size());
                assertEquals(gt.getGradingExperiment().getStages().size(), gradingExp.size());
            }
        }
        if (!found)
            fail();
    }

    @Test
    public void addToPersonalNegativeTest() {
        // username not exist - should fail
        JSONObject stage = Utils.getStumpInfoStage();
        Map<String, Object> ansWrong = creatorService.addToPersonal("something", experiment.getExperimentId(), task.getGradingTaskId(), stage);
        assertNotEquals("OK", ansWrong.get("response"));
        // experiment id does not exists
        ansWrong = creatorService.addToPersonal(manager.getBguUsername(), 9090, task.getGradingTaskId(), stage);
        assertNotEquals("OK", ansWrong.get("response"));
        // grading task does not exists
        ansWrong = creatorService.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), 9090, stage);
        assertNotEquals("OK", ansWrong.get("response"));
        // bad stage representation
        ansWrong = creatorService.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), new JSONObject());
        assertNotEquals("OK", ansWrong.get("response"));
    }

    @Test
    public void addToPersonalPositiveTest() throws NotExistException {
        JSONObject stage = Utils.getStumpInfoStage();
        int personalStages = task.getGeneralExperiment().getStages().size();
        Map<String, Object> ansRight = creatorService.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), stage);
        assertEquals("OK", ansRight.get("response"));
        assertEquals(task.getGeneralExperiment().getStages().size(), personalStages + 1);
        assertEquals(task.getGeneralExperiment().getStage(personalStages).getType(), "info");
    }

    @Test
    public void addToResultsExpNegativeTest() {
        // username not exist - should fail
        JSONObject stage = Utils.getStumpInfoStage();
        Map<String, Object> ansWrong = creatorService.addToResultsExp("something", experiment.getExperimentId(), task.getGradingTaskId(), stage);
        assertNotEquals("OK", ansWrong.get("response"));
        // experiment id does not exists
        ansWrong = creatorService.addToResultsExp(manager.getBguUsername(), 9090, task.getGradingTaskId(), stage);
        assertNotEquals("OK", ansWrong.get("response"));
        // grading task does not exists
        ansWrong = creatorService.addToResultsExp(manager.getBguUsername(), experiment.getExperimentId(), 9090, stage);
        assertNotEquals("OK", ansWrong.get("response"));
        // bad stage representation
        ansWrong = creatorService.addToResultsExp(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), new JSONObject());
        assertNotEquals("OK", ansWrong.get("response"));
    }

    @Test
    public void addToResultsExpPositiveTest() throws NotExistException {
        JSONObject stage = Utils.getStumpInfoStage();
        int resultsStages = task.getGradingExperiment().getStages().size();
        Map<String, Object> ansRight = creatorService.addToResultsExp(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), stage);
        assertEquals("OK", ansRight.get("response"));
        assertEquals(resultsStages + 1, task.getGradingExperiment().getStages().size());
        assertEquals(task.getGradingExperiment().getStage(resultsStages).getType(), "info");
    }

    @Test
    public void setStagesToCheckNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.setStagesToCheck("something", experiment.getExperimentId(), task.getGradingTaskId(), List.of(1, 2, 3));
        assertNotEquals("OK", ansWrong.get("response"));
        // experiment id does not exists
        ansWrong = creatorService.setStagesToCheck(manager.getBguUsername(), 9090, task.getGradingTaskId(), List.of(1, 2, 3));
        assertNotEquals("OK", ansWrong.get("response"));
        // grading task does not exists
        ansWrong = creatorService.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), 9090, List.of(1, 2, 3));
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid stages on stages to check - stages that are no exists
        ansWrong = creatorService.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), List.of(1, 2, 3, 4, 5, 6));
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid stages on stages to check - stages without results
        ansWrong = creatorService.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), List.of(0, 1, 2, 3));
        assertNotEquals("OK", ansWrong.get("response"));
    }

    @Test
    public void setStagesToCheckPositiveTest() throws NotExistException {
        Map<String, Object> ansRight = creatorService.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), List.of(1, 2, 3));
        assertEquals("OK", ansRight.get("response"));
        assertEquals(task.getStages().size(), 3);
        // check same stages without info
        assertEquals(task.getStages().get(0), experiment.getStage(1));
        assertEquals(task.getStages().get(1), experiment.getStage(2));
        assertEquals(task.getStages().get(2), experiment.getStage(3));
    }

    @Test
    public void setAllyPermissionsNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.setAlliePermissions("something", experiment.getExperimentId(), ally.getUserEmail(), "view", List.of("some permission"));
        assertNotEquals("OK", ansWrong.get("response"));
        // experiment id does not exists
        ansWrong = creatorService.setAlliePermissions(manager.getBguUsername(), 9090, ally.getUserEmail(), "view", List.of("some permission"));
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid ally mail
        ansWrong = creatorService.setAlliePermissions(manager.getBguUsername(), experiment.getExperimentId(), "Hello, it's me", "view", List.of("some permission"));
        assertNotEquals("OK", ansWrong.get("response"));
    }

    @Test
    public void setAllyPermissionsPositiveTest() throws NotExistException {
        Map<String, Object> ansRight = creatorService.setAlliePermissions(manager.getBguUsername(), experiment.getExperimentId(), ally.getUserEmail(), "view", List.of("some permission"));
        assertEquals("OK", ansRight.get("response"));
        ManagementUser updatedAlly = cache.getManagerByEMail(ally.getBguUsername());
        assertEquals(updatedAlly.getPermissions().size(), 1);
        assertEquals(updatedAlly.getPermissions().get(0).getPermissionName(), "some permission");
    }

    @Test
    public void addGraderToGradingTaskNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.addGraderToGradingTask("something", experiment.getExperimentId(), task.getGradingTaskId(), "newgrader@gmail.com");
        assertNotEquals("OK", ansWrong.get("response"));
        // experiment id does not exists
        ansWrong = creatorService.addGraderToGradingTask(manager.getBguUsername(), 9090, task.getGradingTaskId(), "newgrader@gmail.com");
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid ally mail
        ansWrong = creatorService.addGraderToGradingTask(manager.getBguUsername(), experiment.getExperimentId(), 9090, "newgrader@gmail.com");
        assertNotEquals("OK", ansWrong.get("response"));
    }

    @Test
    public void addGraderToGradingTaskPositiveTest() throws CodeException {
        Map<String, Object> ansRight = creatorService.addGraderToGradingTask(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), "newgrader@gmail.com");
        assertEquals("OK", ansRight.get("response"));
        String newGraderCode = (String) ansRight.get("code");
        assertEquals(cache.getTaskByCode(UUID.fromString(newGraderCode)).getGrader().getGraderEmail(), "newgrader@gmail.com");
    }

    @Test
    public void addExperimenteeTest() throws CodeException {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.addExperimentee("something", experiment.getExperimentId(), "newexpee@gmail.com");
        assertNotEquals("OK", ansWrong.get("response"));
        // experiment id does not exists
        ansWrong = creatorService.addExperimentee(manager.getBguUsername(), 9090, "newexpee@gmail.com");
        assertNotEquals("OK", ansWrong.get("response"));
        Map<String, Object> ansRight = creatorService.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), "newexpee@gmail.com");
        assertEquals("OK", ansRight.get("response"));
        String newExpeeCode = (String) ansRight.get("code");
        assertEquals(cache.getExpeeByCode(UUID.fromString(newExpeeCode)).getExperimenteeEmail(), "newexpee@gmail.com");
    }

    @Test
    public void addExperimenteesTest() throws CodeException {
        // list has 2 identical mails
        Map<String, Object>  ansWrong = creatorService.addExperimentees(manager.getBguUsername(), 9090, List.of("newexpee@gmail.com","newexpee@gmail.com"));
        assertNotEquals("OK", ansWrong.get("response"));

        Map<String, Object> ansRight = creatorService.addExperimentees(manager.getBguUsername(), experiment.getExperimentId(), List.of("newexpee@gmail.com","newexpee1@gmail.com"));
        assertEquals("OK", ansRight.get("response"));
        String newExpeeCode = ((List<String>) ansRight.get("codes")).get(0);
        assertEquals(cache.getExpeeByCode(UUID.fromString(newExpeeCode)).getExperimenteeEmail(), "newexpee@gmail.com");
    }

    @Test
    public void addExpeeToGraderNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.addExpeeToGrader("something", experiment.getExperimentId(), task.getGradingTaskId(), grader.getGraderEmail(), expee.getExperimenteeEmail());
        assertNotEquals("OK", ansWrong.get("response"));
        // experiment id does not exists
        ansWrong = creatorService.addExpeeToGrader(manager.getBguUsername(), 9090, task.getGradingTaskId(), grader.getGraderEmail(), expee.getExperimenteeEmail());
        assertNotEquals("OK", ansWrong.get("response"));
        // grading task id does not exist
        ansWrong = creatorService.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), 9090, grader.getGraderEmail(), expee.getExperimenteeEmail());
        assertNotEquals("OK", ansWrong.get("response"));
        // grader does not exist
        ansWrong = creatorService.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), "IdontKnowU@gmail.com", expee.getExperimenteeEmail());
        assertNotEquals("OK", ansWrong.get("response"));
        // expee does not exist
        ansWrong = creatorService.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), grader.getGraderEmail(), "IdontKnowU@gmail.com");
        assertNotEquals("OK", ansWrong.get("response"));
    }

    @Test
    public void addExpeeToGraderPositiveTest() throws CodeException {
        int expeesOfGraderBefore = cache.getTaskByCode(UUID.fromString(graderCode)).getGraderToParticipants().size();
        Map<String, Object> ansRight = creatorService.addExpeeToGrader(manager.getBguUsername(),
                experiment.getExperimentId(), task.getGradingTaskId(), grader.getGraderEmail(), expee.getExperimenteeEmail());
        assertEquals("OK", ansRight.get("response"));
        int expeesOfGraderAfter = cache.getTaskByCode(UUID.fromString(graderCode)).getGraderToParticipants().size();
        assertEquals(expeesOfGraderAfter, expeesOfGraderBefore + 1);
        assertEquals(cache.getTaskByCode(UUID.fromString(graderCode)).getGraderToParticipants().get(
                expeesOfGraderBefore).getExpeeParticipant().getParticipantId(), expee.getParticipant().getParticipantId());
    }

    @Test
    public void getExperimentsTest() throws NotExistException {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.getExperiments("something");
        assertNotEquals("OK", ansWrong.get("response"));
        assertNull(ansWrong.get("experiments"));
        Map<String, Object> ansRight = creatorService.getExperiments(manager.getBguUsername());
        assertEquals("OK", ansRight.get("response"));
        List<Integer> experiments = (List<Integer>) ansRight.get("experiments");
        List<Experiment> currentExps = creatorBusiness.getExperiments(manager.getBguUsername());
        List<Integer> currentExpsId = new ArrayList<>();
        currentExps.forEach(exp -> currentExpsId.add(exp.getExperimentId()));
        assertEquals(experiments, currentExpsId);
    }

    @Test
    public void getStagesTest() throws NotExistException {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.getStages("something", experiment.getExperimentId());
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid experiment id
        ansWrong = creatorService.getStages(manager.getBguUsername(), 9090);
        assertNotEquals("OK", ansWrong.get("response"));
        Map<String, Object> ansRight = creatorService.getStages(manager.getBguUsername(), experiment.getExperimentId());
        assertEquals("OK", ansRight.get("response"));
        List<Map<String, Object>> stages = (List<Map<String, Object>>) ansRight.get("stages");
        assertEquals(stages.size(), experiment.getStages().size());
        assertEquals(stages.get(0), experiment.getStage(0).getAsMap());
        assertEquals(stages.get(1), experiment.getStage(1).getAsMap());
    }

    @Test
    public void getExperimenteesTest() throws NotExistException, ExistException {
        creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), grader.getGraderEmail(), expee.getExperimenteeEmail());
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.getExperimentees("something", experiment.getExperimentId());
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid experiment id
        ansWrong = creatorService.getExperimentees(manager.getBguUsername(), 9090);
        assertNotEquals("OK", ansWrong.get("response"));
        Map<String, Object> ansRight = creatorService.getExperimentees(manager.getBguUsername(), experiment.getExperimentId());
        assertEquals("OK", ansRight.get("response"));
        List<Integer> expees = (List<Integer>) ansRight.get("experimentees");
        assertEquals(expees, List.of(expee.getParticipant().getParticipantId()));
    }

    @Test
    public void getAlliesTest() throws NotExistException {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.getAllies("something", experiment.getExperimentId());
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid experiment id
        ansWrong = creatorService.getAllies(manager.getBguUsername(), 9090);
        assertNotEquals("OK", ansWrong.get("response"));
        Map<String, Object> ansRight = creatorService.getAllies(manager.getBguUsername(), experiment.getExperimentId());
        assertEquals("OK", ansRight.get("response"));
        List<Map<String, String>> allies = (List<Map<String, String>>) ansRight.get("allies");
        assertEquals(allies, List.of(Map.of("username", manager.getBguUsername(), "role", "creator")));
        creatorBusiness.setAlliePermissions(manager.getBguUsername(), experiment.getExperimentId(), ally.getUserEmail(), "view", List.of("some permission"));
        ansRight = creatorService.getAllies(manager.getBguUsername(), experiment.getExperimentId());
        List<Map<String, String>> newAllies = (List<Map<String, String>>) ansRight.get("allies");
        assertEquals(newAllies, List.of(Map.of("username", manager.getBguUsername(), "role", "creator"), Map.of("username", ally.getBguUsername(), "role", "view")));
    }

    @Test
    public void getGradingTasksTest() throws NotExistException {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.getGradingTasks("something", experiment.getExperimentId());
        assertNotEquals("OK", ansWrong.get("response"));

        // invalid experiment id
        ansWrong = creatorService.getGradingTasks(manager.getBguUsername(), 9090);
        assertNotEquals("OK", ansWrong.get("response"));
        Map<String, Object> ansRight = creatorService.getGradingTasks(manager.getBguUsername(), experiment.getExperimentId());
        assertEquals("OK", ansRight.get("response"));
        List<Integer> gradingTasks = (List<Integer>) ansRight.get("tasks");
        List<GradingTask> currentTasks = creatorBusiness.getGradingTasks(manager.getBguUsername(), experiment.getExperimentId());
        List<Integer> currentTasksId = new ArrayList<>();
        currentTasks.forEach(exp -> currentTasksId.add(exp.getGradingTaskId()));
        assertEquals(gradingTasks, currentTasksId);
    }

    @Test
    public void getPersonalStagesNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.getPersonalStages("something", experiment.getExperimentId(), task.getGradingTaskId());
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid experiment id
        ansWrong = creatorService.getPersonalStages(manager.getBguUsername(), 9090, task.getGradingTaskId());
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid grading task id
        ansWrong = creatorService.getPersonalStages(manager.getBguUsername(), experiment.getExperimentId(), 9090);
        assertNotEquals("OK", ansWrong.get("response"));
    }

    @Test
    public void getPersonalStagesPositiveTest() throws NotExistException, FormatException {
        creatorBusiness.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), Utils.getStumpInfoStage());
        Map<String, Object> ansRight = creatorService.getPersonalStages(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId());
        assertEquals("OK", ansRight.get("response"));
        List<Map<String, Object>> personalStages = (List<Map<String, Object>>) ansRight.get("stages");
        assertEquals(personalStages.get(0).get("text"), Utils.getStumpInfoStage().get("info"));
    }

    @Test
    public void getEvaluationStagesNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.getEvaluationStages("something", experiment.getExperimentId(), task.getGradingTaskId());
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid experiment id
        ansWrong = creatorService.getEvaluationStages(manager.getBguUsername(), 9090, task.getGradingTaskId());
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid grading task id
        ansWrong = creatorService.getEvaluationStages(manager.getBguUsername(), experiment.getExperimentId(), 9090);
        assertNotEquals("OK", ansWrong.get("response"));
    }

    @Test
    public void getEvaluationStagesPositiveTest() throws NotExistException, FormatException {
        creatorBusiness.addToResultsExp(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), Utils.getStumpInfoStage());
        Map<String, Object> ansRight = creatorService.getEvaluationStages(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId());
        assertEquals("OK", ansRight.get("response"));
        List<Map<String, Object>> personalStages = (List<Map<String, Object>>) ansRight.get("stages");
        assertEquals(personalStages.get(0).get("text"), Utils.getStumpInfoStage().get("info"));
    }

    @Test
    public void getTaskGradersNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.getTaskGraders("something", experiment.getExperimentId(), task.getGradingTaskId());
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid experiment id
        ansWrong = creatorService.getTaskGraders(manager.getBguUsername(), 9090, task.getGradingTaskId());
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid grading task id
        ansWrong = creatorService.getTaskGraders(manager.getBguUsername(), experiment.getExperimentId(), 9090);
        assertNotEquals("OK", ansWrong.get("response"));
    }

    @Test
    public void getTaskGradersPositiveTest() {
        Map<String, Object> ansRight = creatorService.getTaskGraders(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId());
        assertEquals("OK", ansRight.get("response"));
        List<Grader> graders = (List<Grader>) ansRight.get("graders");
        assertEquals(task.getAssignedGradingTasks().size(), graders.size());
        assertEquals(graders.get(0), grader);
    }

    @Test
    public void getTaskExperimenteesNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.getTaskExperimentees("something", experiment.getExperimentId(), task.getGradingTaskId());
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid experiment id
        ansWrong = creatorService.getTaskExperimentees(manager.getBguUsername(), 9090, task.getGradingTaskId());
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid grading task id
        ansWrong = creatorService.getTaskExperimentees(manager.getBguUsername(), experiment.getExperimentId(), 9090);
        assertNotEquals("OK", ansWrong.get("response"));
    }

    @Test
    public void getTaskExperimenteesPositiveTest() throws NotExistException, ExistException {
        creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), grader.getGraderEmail(), expee.getExperimenteeEmail());
        Map<String, Object> ansRight = creatorService.getTaskExperimentees(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId());
        assertEquals("OK", ansRight.get("response"));
        List<Integer> expees = (List<Integer>) ansRight.get("experimentees");

        assertEquals(cache.getGraderToGradingTask(grader, task).getGraderToParticipants().size(), expees.size());
        assertEquals(expees.get(0).intValue(), expee.getParticipant().getParticipantId());
    }
}
