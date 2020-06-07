package com.example.demo.ServiceTests;

import com.example.demo.BusinessLayer.CreatorBusiness;
import com.example.demo.BusinessLayer.DataCache;
import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
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

@Sql({"/create_database.sql"})
@SpringBootTest
public class creatorServiceTests {
    private CreatorService creatorService;
    private CreatorBusiness creatorBusiness;
    private DataCache cache;
    private DBAccess db;

    @Autowired
    public creatorServiceTests(CreatorService creatorService, CreatorBusiness creatorBusiness, DataCache cache, DBAccess db) {
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
    public void createExperimentTest() throws NotExistException {
        // username not exist - should fail
        Map<String,Object> ansWrong = creatorService.createExperiment("some shit", "some shit");
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        Map<String,Object> ansRight = creatorService.createExperiment(manager.getBguUsername(), "some shit");
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        int expId = (Integer) ansRight.get("id");
        int toValidateId = -1;
        for(Experiment exp : creatorBusiness.getExperiments(manager.getBguUsername())) {
            if(exp.getExperimentName().equals("some shit")) {
                toValidateId = exp.getExperimentId();
                break;
            }
        }
        Assert.assertEquals(expId, toValidateId);
    }

    @Test
    public void addStageToExperimentNegativeTest() {
        // username not exist - should fail
        Map<String, Object> ansWrong = creatorService.addStageToExperiment("some shit", experiment.getExperimentId(), new JSONObject());
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
        Map<String, Object> ansWrong = creatorService.addExperiment("some shit", "some shit", stages1);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        //bad stages representation
        List<JSONObject> stages2 = new ArrayList<>();
        stages2.add(new JSONObject());
        ansWrong = creatorService.addExperiment(manager.getBguUsername(), "some shit", stages2);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
    }

    @Test
    public void addExperimentPositiveTest() throws NotExistException {
        List<JSONObject> stages = Utils.buildStages();
        Map<String, Object> ansRight = creatorService.addExperiment(manager.getBguUsername(), "some shit", stages);
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        int expId = (Integer) ansRight.get("id");
        int toValidateId = -1;
        for(Experiment exp : creatorBusiness.getExperiments(manager.getBguUsername()))
            if(exp.getExperimentName().equals("some shit")) {
                toValidateId = exp.getExperimentId();
                break;
            }
        Assert.assertEquals(expId, toValidateId);
    }





}
