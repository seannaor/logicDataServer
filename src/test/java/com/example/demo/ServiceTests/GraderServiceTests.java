package com.example.demo.ServiceTests;

import com.example.demo.BusinessLayer.CreatorBusiness;
import com.example.demo.BusinessLayer.DataCache;
import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.Stages.InfoStage;
import com.example.demo.BusinessLayer.Entities.Stages.QuestionnaireStage;
import com.example.demo.BusinessLayer.Exceptions.*;
import com.example.demo.BusinessLayer.ExperimenteeBusiness;
import com.example.demo.DBAccess;
import com.example.demo.ServiceLayer.GraderService;
import com.example.demo.Utils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Sql({"/create_database.sql"})
@SpringBootTest
public class GraderServiceTests {
    private GraderService graderService;
    private CreatorBusiness creatorBusiness;
    private ExperimenteeBusiness experimenteeBusiness;
    private DataCache cache;
    private DBAccess db;

    @Autowired
    public GraderServiceTests(GraderService graderService, CreatorBusiness creatorBusiness, ExperimenteeBusiness experimenteeBusiness, DataCache cache, DBAccess db) {
        this.graderService = graderService;
        this.creatorBusiness = creatorBusiness;
        this.experimenteeBusiness = experimenteeBusiness;
        this.cache = cache;
        this.db = db;
    }

    private Grader grader;
    private ManagementUser manager;
    private Experimentee expee;
    private Experiment experiment;
    private GradingTask task;
    private Participant graderExpeeParticipant;
    private UUID graderCode;

    @BeforeEach
    private void init() throws NotExistException, FormatException, ExistException, CodeException, ParseException, ExpEndException, NotInReachException {
        cache.setCache();
        db.deleteData();
        manager = new ManagementUser("smorad", "sm_pass", "smorad@post.bgu.ac.il");
        cache.addManager(manager);
        experiment = Utils.buildExp(creatorBusiness, manager);
        String code = creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), "gili@post.bgu.ac.il");
        expee = cache.getExpeeByCode(UUID.fromString(code));
        task = Utils.buildSimpleGradingTask(creatorBusiness, cache, manager, experiment);
        creatorBusiness.addGraderToGradingTask(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), "grader@post.bgu.ac.il");
        grader = cache.getGraderByEMail("grader@post.bgu.ac.il");
        graderCode = cache.getGraderToGradingTask(grader, task).getGraderAccessCode();
        creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), grader.getGraderEmail(), expee.getExperimenteeEmail());
        graderExpeeParticipant = task.graderToGradingTask(grader).getGraderParticipant(expee.getParticipant().getParticipantId());
        Utils.fillInExp(experimenteeBusiness, expee.getAccessCode());
    }

    @Test
    public void beginGradingTest() {
        // some random wrong access code, should fail
        Assert.assertFalse(graderService.beginGrading(UUID.randomUUID().toString()).get("response").equals("OK"));
        Assert.assertTrue(graderService.beginGrading(graderCode.toString()).get("response").equals("OK"));
    }

    @Test
    public void getNextStageTest() throws NotExistException {
        // some random wrong access code, should fail
        Map<String,Object> ansWrong = graderService.getNextStage(UUID.randomUUID().toString(), expee.getParticipant().getParticipantId());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid pid
        ansWrong = graderService.getNextStage(graderCode.toString(),9090);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        Map<String,Object> ansRight = graderService.getNextStage(graderCode.toString(), expee.getParticipant().getParticipantId());
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        List<JSONObject> questionsOfStage = ((List<JSONObject>)((Map<String, Object>)ansRight.get("stage")).get("questions"));
        Assert.assertEquals(questionsOfStage.size(), ((QuestionnaireStage)experiment.getStage(1)).getQuestions().size());
    }

    @Test
    public void getCurrentStageTest() throws NotExistException {
        // some random wrong access code, should fail
        Map<String,Object> ansWrong = graderService.getCurrentStage(UUID.randomUUID().toString(), expee.getParticipant().getParticipantId());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid pid
        ansWrong = graderService.getCurrentStage(graderCode.toString(),9090);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        Map<String,Object> ansRight = graderService.getCurrentStage(graderCode.toString(), expee.getParticipant().getParticipantId());
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        Assert.assertEquals(((Map<String, Object>)ansRight.get("stage")).get("text"), ((InfoStage)experiment.getStage(0)).getInfo());
        graderService.getNextStage(graderCode.toString(), expee.getParticipant().getParticipantId());
        ansRight = graderService.getCurrentStage(graderCode.toString(), expee.getParticipant().getParticipantId());
        List<JSONObject> questionsOfStage = ((List<JSONObject>)((Map<String, Object>)ansRight.get("stage")).get("questions"));
        Assert.assertEquals(questionsOfStage.size(), ((QuestionnaireStage)experiment.getStage(1)).getQuestions().size());
    }

    @Test
    public void getStageBadInputsTest() {
        // some random wrong access code, should fail
        Map<String, Object> ansWrong = graderService.getStage(UUID.randomUUID().toString(), expee.getParticipant().getParticipantId(), 0);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid stage index
        ansWrong = graderService.getStage(graderCode.toString(), 9090, 0);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid stage index
        ansWrong = graderService.getStage(graderCode.toString(), expee.getParticipant().getParticipantId(), 900);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
    }


    @Test
    public void getStageTest() throws NotExistException {
        Map<String, Object> ansRight = graderService.getStage(graderCode.toString(), expee.getParticipant().getParticipantId(), 0);
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        Assert.assertEquals(((Map<String, Object>) ansRight.get("stage")).get("text"), ((InfoStage) experiment.getStage(0)).getInfo());
        //did not reach stage 1 yet
        ansRight = graderService.getStage(graderCode.toString(), expee.getParticipant().getParticipantId(), 1);
        Assert.assertFalse(ansRight.get("response").equals("OK"));
        graderService.getNextStage(graderCode.toString(),expee.getParticipant().getParticipantId());
        ansRight = graderService.getStage(graderCode.toString(), expee.getParticipant().getParticipantId(), 1);
        List<JSONObject> questionsOfStage = ((List<JSONObject>)((Map<String, Object>)ansRight.get("stage")).get("questions"));
        Assert.assertEquals(questionsOfStage.size(), ((QuestionnaireStage)experiment.getStage(1)).getQuestions().size());
    }

}
