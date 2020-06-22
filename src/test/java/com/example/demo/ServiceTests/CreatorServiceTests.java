package com.example.demo.ServiceTests;

import com.example.demo.BusinessLayer.CreatorBusiness;
import com.example.demo.BusinessLayer.DataCache;
import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.Exceptions.ExistException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import com.example.demo.DBAccess;
import com.example.demo.ServiceLayer.CreatorService;
import com.example.demo.Utils;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Sql({"/create_database.sql"})
@SpringBootTest
public class CreatorServiceTests {
    private CreatorService creatorService;
    private CreatorBusiness creatorBusiness;
    private DataCache cache;
    private DBAccess db;

    @Autowired
    public CreatorServiceTests(CreatorService creatorService, CreatorBusiness creatorBusiness, DataCache cache, DBAccess db) {
        this.creatorService = creatorService;
        this.creatorBusiness = creatorBusiness;
        this.cache = cache;
        this.db = db;
    }

    private ManagementUser manager;
    private ManagementUser ally;
    private Experiment experiment;
    private Experimentee expee;
    private GradingTask task;
    private Grader grader;
    private String graderCode;

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
        Assert.assertFalse(creatorService.researcherLogin(manager.getBguUsername(), "not the password"));
        // real password - should pass
        Assert.assertTrue(creatorService.researcherLogin(manager.getBguUsername(), manager.getBguPassword()));
        Assert.assertEquals(db.getManagementUserByName(manager.getBguUsername()).getBguPassword(), manager.getBguPassword());
        // username not exist - should fail
        Assert.assertFalse(creatorService.researcherLogin("some not exist username", "a password"));
    }

    @Test
    public void createExperimentTest() {
        // username not exist - should fail
        Map<String,Object> ansWrong = creatorService.createExperiment("something", "something");
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        Map<String,Object> ansRight = creatorService.createExperiment(manager.getBguUsername(), "something");
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        int expId = (Integer) ansRight.get("id");
        int toValidateId = -1;
        try {
            for(Experiment exp : creatorBusiness.getExperiments(manager.getBguUsername())) {
                if(exp.getExperimentName().equals("something")) {
                    toValidateId = exp.getExperimentId();
                    break;
                }
            }
        } catch (NotExistException e) { Assert.fail(); }
        Assert.assertEquals(expId, toValidateId);
    }

    @Test
    public void addStageToExperimentNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.addStageToExperiment("something", experiment.getExperimentId(), new JSONObject());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // experiment does not exist - should fail
        ansWrong = creatorService.addStageToExperiment(manager.getBguUsername(), 9090, new JSONObject());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        //bad stage representation
        ansWrong = creatorService.addStageToExperiment(manager.getBguUsername(), experiment.getExperimentId(), new JSONObject());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
    }

    @Test
    public void addStageToExperimentPositiveTest() {
        JSONObject stage = new JSONObject();
        stage.put("type", "info");
        stage.put("info", "some information and stuff");
        int stagesBefore = experiment.getStages().size();
        Map<String, Object> ansRight = creatorService.addStageToExperiment(manager.getBguUsername(), experiment.getExperimentId(), stage);
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        Assert.assertEquals(experiment.getStages().size(), stagesBefore+1);
    }

    @Test
    public void addExperimentNegativeTest() {
        List<JSONObject> stages1 = Utils.buildStages();
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.addExperiment("something", "something", stages1);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        //bad stages representation
        List<JSONObject> stages2 = new ArrayList<>();
        stages2.add(new JSONObject());
        ansWrong = creatorService.addExperiment(manager.getBguUsername(), "something", stages2);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
    }

    @Test
    public void addExperimentPositiveTest() {
        List<JSONObject> stages = Utils.buildStages();
        Map<String, Object> ansRight = creatorService.addExperiment(manager.getBguUsername(), "something", stages);
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        int expId = (Integer) ansRight.get("id");
        int toValidateId = -1;
        try {
            for(Experiment exp : creatorBusiness.getExperiments(manager.getBguUsername()))
                if(exp.getExperimentName().equals("something")) {
                    toValidateId = exp.getExperimentId();
                    break;
                }
        } catch (NotExistException e) { Assert.fail(); }
        Assert.assertEquals(expId, toValidateId);
    }

    @Test
    public void addGradingTaskNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.addGradingTask("something", experiment.getExperimentId(), "some grading task", List.of(), List.of(), List.of());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // experiment id does not exists
        ansWrong = creatorService.addGradingTask(manager.getBguUsername(), 9090, "some grading task", List.of(), List.of(), List.of());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // bad grading experiment
        List<JSONObject> gradingStages = new ArrayList<>();
        gradingStages.add(new JSONObject());
        ansWrong = creatorService.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "some grading task", gradingStages, List.of(), List.of());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // bad stages visible for grader
        ansWrong = creatorService.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "some grading task", List.of(), List.of(1,2,3,4,5,6,7,8,9), List.of());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
    }

    @Test
    public void addGradingTaskPositiveTest() {
        List<JSONObject> gradingExp = List.of(Utils.getStumpInfoStage());
        List<JSONObject> personalExp = List.of(Utils.getStumpQuestionsStage());
        Map<String, Object> ansRight = creatorService.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "some grading task", gradingExp, List.of(1, 2, 3), personalExp);
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        int gradingTaskId = (Integer) ansRight.get("id");
        boolean found = false;
        try {
            for (GradingTask gt : creatorBusiness.getGradingTasks(manager.getBguUsername(), experiment.getExperimentId())) {
                if (gt.getGradingTaskId() == gradingTaskId) {
                    found = true;
                    Assert.assertEquals(gt.getStages().size(), 3);
                    Assert.assertEquals(gt.getGeneralExperiment().getStages().size(), personalExp.size());
                    Assert.assertEquals(gt.getGradingExperiment().getStages().size(), gradingExp.size());
                }
            }
        } catch (NotExistException e) { Assert.fail(); }
        if(!found)
            Assert.fail();
    }

    @Test
    public void addToPersonalNegativeTest() {
        // username not exist - should fail
        JSONObject stage = Utils.getStumpInfoStage();
        Map<String, Object> ansWrong = creatorService.addToPersonal("something", experiment.getExperimentId(), task.getGradingTaskId(), stage);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // experiment id does not exists
        ansWrong = creatorService.addToPersonal(manager.getBguUsername(), 9090, task.getGradingTaskId(), stage);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // grading task does not exists
        ansWrong = creatorService.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), 9090, stage);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // bad stage representation
        ansWrong = creatorService.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), new JSONObject());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
    }

    @Test
    public void addToPersonalPositiveTest() {
        JSONObject stage = Utils.getStumpInfoStage();
        int personalStages = task.getGeneralExperiment().getStages().size();
        Map<String, Object> ansRight = creatorService.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), stage);
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        Assert.assertEquals(task.getGeneralExperiment().getStages().size(), personalStages+1);
        try {
            Assert.assertEquals(task.getGeneralExperiment().getStage(personalStages).getType(), "info");
        } catch (NotExistException e) { Assert.fail(); }
    }

    @Test
    public void setStagesToCheckNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.setStagesToCheck("something", experiment.getExperimentId(), task.getGradingTaskId(), List.of(1,2,3));
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // experiment id does not exists
        ansWrong = creatorService.setStagesToCheck(manager.getBguUsername(), 9090, task.getGradingTaskId(), List.of(1,2,3));
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // grading task does not exists
        ansWrong = creatorService.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), 9090, List.of(1,2,3));
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid stages on stages to check - stages that are no exists
        ansWrong = creatorService.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), List.of(1,2,3,4,5,6));
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid stages on stages to check - stages without results
        ansWrong = creatorService.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), List.of(0,1,2,3));
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
    }

    @Test
    public void setStagesToCheckPositiveTest() {
        Map<String, Object> ansRight = creatorService.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), List.of(1,2,3));
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        Assert.assertEquals(task.getStages().size(), 3);
        // check same stages without info
        try {
            Assert.assertTrue(task.getStages().get(0).equals(experiment.getStage(1)));
            Assert.assertTrue(task.getStages().get(1).equals(experiment.getStage(2)));
            Assert.assertTrue(task.getStages().get(2).equals(experiment.getStage(3)));
        } catch (NotExistException e) { Assert.fail(); }
    }

    @Test
    public void setAllyPermissionsNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.setAlliePermissions("something", experiment.getExperimentId(), ally.getUserEmail(), "view", List.of("some permission"));
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // experiment id does not exists
        ansWrong = creatorService.setAlliePermissions(manager.getBguUsername(), 9090, ally.getUserEmail(), "view", List.of("some permission"));
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid ally mail
        ansWrong = creatorService.setAlliePermissions(manager.getBguUsername(), experiment.getExperimentId(), "Hello, it's me", "view", List.of("some permission"));
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
    }

    @Test
    public void setAllyPermissionsPositiveTest() {
        Map<String, Object> ansRight = creatorService.setAlliePermissions(manager.getBguUsername(), experiment.getExperimentId(), ally.getUserEmail(), "view", List.of("some permission"));
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        ManagementUser updatedAlly = null;
        try {
            updatedAlly = cache.getManagerByEMail(ally.getBguUsername());
        } catch (NotExistException e) { Assert.fail(); }
        Assert.assertEquals(updatedAlly.getPermissions().size(), 1);
        Assert.assertEquals(updatedAlly.getPermissions().get(0).getPermissionName(), "some permission");
    }

    @Test
    public void addGraderToGradingTaskNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.addGraderToGradingTask("something", experiment.getExperimentId(), task.getGradingTaskId(), "newgrader@gmail.com");
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // experiment id does not exists
        ansWrong = creatorService.addGraderToGradingTask(manager.getBguUsername(), 9090, task.getGradingTaskId(), "newgrader@gmail.com");
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid ally mail
        ansWrong = creatorService.addGraderToGradingTask(manager.getBguUsername(), experiment.getExperimentId(), 9090, "newgrader@gmail.com");
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
    }

    @Test
    public void addGraderToGradingTaskPositiveTest() {
        Map<String, Object> ansRight = creatorService.addGraderToGradingTask(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), "newgrader@gmail.com");
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        String newGraderCode = (String) ansRight.get("code");
        try {
            Assert.assertEquals(cache.getTaskByCode(UUID.fromString(newGraderCode)).getGrader().getGraderEmail(), "newgrader@gmail.com");
        } catch (CodeException e) { Assert.fail(); }
    }

    @Test
    public void addExperimenteeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.addExperimentee("something", experiment.getExperimentId(), "newexpee@gmail.com");
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // experiment id does not exists
        ansWrong = creatorService.addExperimentee(manager.getBguUsername(), 9090,"newexpee@gmail.com");
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        Map<String, Object> ansRight = creatorService.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(),"newexpee@gmail.com");
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        String newExpeeCode = (String) ansRight.get("code");
        try {
            Assert.assertEquals(cache.getExpeeByCode(UUID.fromString(newExpeeCode)).getExperimenteeEmail(), "newexpee@gmail.com");
        } catch (CodeException e) { Assert.fail(); }
    }

    @Test
    public void addExpeeToGraderNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.addExpeeToGrader("something", experiment.getExperimentId(), task.getGradingTaskId(), grader.getGraderEmail(), expee.getExperimenteeEmail());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // experiment id does not exists
        ansWrong = creatorService.addExpeeToGrader(manager.getBguUsername(), 9090, task.getGradingTaskId(), grader.getGraderEmail(), expee.getExperimenteeEmail());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // grading task id does not exist
        ansWrong = creatorService.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), 9090, grader.getGraderEmail(), expee.getExperimenteeEmail());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // grader does not exist
        ansWrong = creatorService.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), "IdontKnowU@gmail.com", expee.getExperimenteeEmail());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // expee does not exist
        ansWrong = creatorService.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), grader.getGraderEmail(),"IdontKnowU@gmail.com");
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
    }

    @Test
    public void addExpeeToGraderPositiveTest() {
        int expeesOfGraderBefore = 0;
        try {
            expeesOfGraderBefore = cache.getTaskByCode(UUID.fromString(graderCode)).getGraderToParticipants().size();
        } catch (CodeException e) { Assert.fail(); }
        Map<String, Object> ansRight = creatorService.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), grader.getGraderEmail(), expee.getExperimenteeEmail());
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        try {
            int expeesOfGraderAfter = cache.getTaskByCode(UUID.fromString(graderCode)).getGraderToParticipants().size();
            Assert.assertEquals(expeesOfGraderAfter, expeesOfGraderBefore+1);
            Assert.assertEquals(cache.getTaskByCode(UUID.fromString(graderCode)).getGraderToParticipants().get(expeesOfGraderBefore).getExpeeParticipant().getParticipantId(), expee.getParticipant().getParticipantId());
        } catch (CodeException e) { Assert.fail(); }
    }

    @Test
    public void getExperimentsTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.getExperiments("something");
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        Assert.assertTrue(ansWrong.get("experiments") == null);
        Map<String, Object> ansRight = creatorService.getExperiments(manager.getBguUsername());
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        List<Integer> experiments = (List<Integer>) ansRight.get("experiments");
        try {
            List<Experiment> currentExps = creatorBusiness.getExperiments(manager.getBguUsername());
            List<Integer> currentExpsId = new ArrayList<>();
            currentExps.forEach(exp -> currentExpsId.add(exp.getExperimentId()));
            Assert.assertEquals(experiments, currentExpsId);
        } catch (NotExistException e) { Assert.fail(); }
    }

    @Test
    public void getStagesTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.getStages("something", experiment.getExperimentId());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid experiment id
        ansWrong = creatorService.getStages(manager.getBguUsername(), 9090);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        Map<String, Object> ansRight = creatorService.getStages(manager.getBguUsername(), experiment.getExperimentId());
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        List<Map<String, Object>> stages = (List<Map<String, Object>>) ansRight.get("stages");
        Assert.assertEquals(stages.size(), experiment.getStages().size());
        try {
            Assert.assertEquals(stages.get(0), experiment.getStage(0).getAsMap());
            Assert.assertEquals(stages.get(1), experiment.getStage(1).getAsMap());
        } catch (NotExistException e) { Assert.fail(); }
    }

    @Test
    public void getExperimenteesTest() {
        try {
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(),experiment.getExperimentId(),task.getGradingTaskId(),grader.getGraderEmail(),expee.getExperimenteeEmail());
        } catch (NotExistException | ExistException e) { Assert.fail(); }
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.getExperimentees("something", experiment.getExperimentId());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid experiment id
        ansWrong = creatorService.getExperimentees(manager.getBguUsername(), 9090);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        Map<String, Object> ansRight = creatorService.getExperimentees(manager.getBguUsername(), experiment.getExperimentId());
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        List<Integer> expees = (List<Integer>) ansRight.get("experimentees");
        Assert.assertEquals(expees, List.of(expee.getParticipant().getParticipantId()));
    }

    @Test
    public void getAlliesTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.getAllies("something", experiment.getExperimentId());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid experiment id
        ansWrong = creatorService.getAllies(manager.getBguUsername(), 9090);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        Map<String, Object> ansRight = creatorService.getAllies(manager.getBguUsername(), experiment.getExperimentId());
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        List<Map<String, String>> allies = (List<Map<String, String>>) ansRight.get("allies");
        Assert.assertEquals(allies, List.of(Map.of("username", manager.getBguUsername(), "role", "creator")));
        try {
            creatorBusiness.setAlliePermissions(manager.getBguUsername(), experiment.getExperimentId(), ally.getUserEmail(), "view",List.of("some permission"));
        } catch (NotExistException e) { Assert.fail(); }
        ansRight = creatorService.getAllies(manager.getBguUsername(), experiment.getExperimentId());
        List<Map<String, String>> newAllies = (List<Map<String, String>>) ansRight.get("allies");
        Assert.assertEquals(newAllies, List.of(Map.of("username", manager.getBguUsername(), "role", "creator"), Map.of("username", ally.getBguUsername(), "role", "view")));
    }

    @Test
    public void getGradingTasksTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.getGradingTasks("something", experiment.getExperimentId());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid experiment id
        ansWrong = creatorService.getGradingTasks(manager.getBguUsername(), 9090);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        Map<String, Object> ansRight = creatorService.getGradingTasks(manager.getBguUsername(), experiment.getExperimentId());
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        List<Integer> gradingTasks = (List<Integer>) ansRight.get("tasks");
        try {
            List<GradingTask> currentTasks = creatorBusiness.getGradingTasks(manager.getBguUsername(), experiment.getExperimentId());
            List<Integer> currentTasksId = new ArrayList<>();
            currentTasks.forEach(exp -> currentTasksId.add(exp.getGradingTaskId()));
            Assert.assertEquals(gradingTasks, currentTasksId);
        } catch (NotExistException e) { Assert.fail(); }
    }

    @Test
    public void getPersonalStagesNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.getPersonalStages("something", experiment.getExperimentId(), task.getGradingTaskId());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid experiment id
        ansWrong = creatorService.getPersonalStages(manager.getBguUsername(), 9090, task.getGradingTaskId());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid grading task id
        ansWrong = creatorService.getPersonalStages(manager.getBguUsername(), experiment.getExperimentId(), 9090);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
    }

    @Test
    public void getPersonalStagesPositiveTest() {
        try {
            creatorBusiness.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), Utils.getStumpInfoStage());
        } catch (NotExistException | FormatException e) { Assert.fail(); }
        Map<String, Object> ansRight = creatorService.getPersonalStages(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId());
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        List<Map<String, Object>> personalStages = (List<Map<String, Object>>) ansRight.get("stages");
        Assert.assertEquals(personalStages.get(0).get("text"), Utils.getStumpInfoStage().get("info"));
    }

    @Test
    public void getEvaluationStagesNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.getEvaluationStages("something", experiment.getExperimentId(), task.getGradingTaskId());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid experiment id
        ansWrong = creatorService.getEvaluationStages(manager.getBguUsername(), 9090, task.getGradingTaskId());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid grading task id
        ansWrong = creatorService.getEvaluationStages(manager.getBguUsername(), experiment.getExperimentId(), 9090);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
    }

    @Test
    public void getEvaluationStagesPositiveTest() {
        try {
            creatorBusiness.addToResultsExp(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), Utils.getStumpInfoStage());
        } catch (NotExistException | FormatException e) { Assert.fail(); }
        Map<String, Object> ansRight = creatorService.getEvaluationStages(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId());
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        List<Map<String, Object>> personalStages = (List<Map<String, Object>>) ansRight.get("stages");
        Assert.assertEquals(personalStages.get(0).get("text"), Utils.getStumpInfoStage().get("info"));
    }

    @Test
    public void getTaskGradersNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.getTaskGraders("something", experiment.getExperimentId(), task.getGradingTaskId());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid experiment id
        ansWrong = creatorService.getTaskGraders(manager.getBguUsername(), 9090, task.getGradingTaskId());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid grading task id
        ansWrong = creatorService.getTaskGraders(manager.getBguUsername(), experiment.getExperimentId(), 9090);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
    }

    @Test
    public void getTaskGradersPositiveTest() {
        Map<String, Object> ansRight = creatorService.getTaskGraders(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId());
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        List<Grader> graders = (List<Grader>) ansRight.get("graders");
        Assert.assertEquals(task.getAssignedGradingTasks().size(), graders.size());
        Assert.assertEquals(graders.get(0), grader);
    }

    @Test
    public void getTaskExperimenteesNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.getTaskExperimentees("something", experiment.getExperimentId(), task.getGradingTaskId());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid experiment id
        ansWrong = creatorService.getTaskExperimentees(manager.getBguUsername(), 9090, task.getGradingTaskId());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid grading task id
        ansWrong = creatorService.getTaskExperimentees(manager.getBguUsername(), experiment.getExperimentId(), 9090);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
    }

    @Test
    public void getTaskExperimenteesPositiveTest() {
        try {
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), grader.getGraderEmail(), expee.getExperimenteeEmail());
        } catch (NotExistException | ExistException e) { Assert.fail(); }
        Map<String, Object> ansRight = creatorService.getTaskExperimentees(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId());
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        List<Integer> expees = (List<Integer>) ansRight.get("experimentees");
        try {
            Assert.assertEquals(cache.getGraderToGradingTask(grader, task).getGraderToParticipants().size(), expees.size());
        } catch (NotExistException e) { Assert.fail(); }
        Assert.assertEquals(expees.get(0).intValue(), expee.getParticipant().getParticipantId());
    }
}
