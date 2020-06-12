package com.example.demo.ServiceTests;

import com.example.demo.BusinessLayer.CreatorBusiness;
import com.example.demo.BusinessLayer.DataCache;
import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import com.example.demo.BusinessLayer.Entities.Results.CodeResult;
import com.example.demo.BusinessLayer.Entities.Results.QuestionnaireResult;
import com.example.demo.BusinessLayer.Entities.Results.TaggingResult;
import com.example.demo.BusinessLayer.Entities.Stages.InfoStage;
import com.example.demo.BusinessLayer.Entities.Stages.QuestionnaireStage;
import com.example.demo.BusinessLayer.Exceptions.*;
import com.example.demo.DBAccess;
import com.example.demo.ServiceLayer.ExperimenteeService;
import com.example.demo.Utils;
import org.json.simple.JSONObject;
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
public class ExperimenteeServiceTests {
    private ExperimenteeService experimenteeService;
    private CreatorBusiness creatorBusiness;
    private DataCache cache;
    private DBAccess db;

    @Autowired
    public ExperimenteeServiceTests(ExperimenteeService experimenteeService, CreatorBusiness creatorBusiness, DataCache cache, DBAccess db) {
        this.experimenteeService = experimenteeService;
        this.creatorBusiness = creatorBusiness;
        this.cache = cache;
        this.db = db;
    }

    private Experiment experiment;
    private Experimentee expee;

    @BeforeEach
    private void init() throws NotExistException, FormatException, ExistException, CodeException {
        cache.setCache();
        db.deleteData();

        ManagementUser manager = new ManagementUser("smorad", "sm_pass", "smorad@post.bgu.ac.il");
        cache.addManager(manager);

        experiment = Utils.buildExp(creatorBusiness, manager);

        String code = creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), "gili@post.bgu.ac.il");
        expee = cache.getExpeeByCode(UUID.fromString(code));
    }
    @Test
    public void loginTest() {
        // some random wrong access code, should fail
        Assert.assertFalse(experimenteeService.beginParticipation(UUID.randomUUID().toString()));
        Assert.assertTrue(experimenteeService.beginParticipation(expee.getAccessCode().toString()));
    }

    @Test
    public void getNextStageTest() throws NotExistException {
        // some random wrong access code, should fail
        Map<String,Object> ansWrong = experimenteeService.getNextStage(UUID.randomUUID().toString());
        Assert.assertTrue(ansWrong.containsKey("Error"));
        Map<String,Object> ansRight = experimenteeService.getNextStage(expee.getAccessCode().toString());
        Assert.assertTrue(ansRight.containsKey("type"));
        Assert.assertEquals(ansRight.get("type"), "questionnaire");
        List<JSONObject> questionsOfStage = ((List<JSONObject>)((Map<String, Object>)ansRight.get("stage")).get("questions"));
        Assert.assertEquals(questionsOfStage.size(), ((QuestionnaireStage)experiment.getStage(1)).getQuestions().size());
        Assert.assertEquals(experimenteeService.getNextStage(expee.getAccessCode().toString()).get("type"), "code");
    }

    @Test
    public void getCurrentStageTest() throws NotExistException {
        // some random wrong access code, should fail
        Map<String,Object> ansWrong = experimenteeService.getCurrentStage(UUID.randomUUID().toString());
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        Map<String,Object> ansRight = experimenteeService.getCurrentStage(expee.getAccessCode().toString());
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        Assert.assertEquals(ansRight.get("type"), "info");
        Assert.assertEquals(((Map<String,Object>)ansRight.get("stage")).get("text"), ((InfoStage)experiment.getStage(0)).getInfo());
        experimenteeService.getNextStage(expee.getAccessCode().toString());
        Assert.assertEquals(experimenteeService.getCurrentStage(expee.getAccessCode().toString()).get("type"), "questionnaire");
    }

    @Test
    public void getStageAtTest() throws NotExistException {
        // some random wrong access code, should fail
        Map<String, Object> ansWrong = experimenteeService.getStageAt(UUID.randomUUID().toString(), 0);
        Assert.assertFalse(ansWrong.get("response").equals("OK"));
        // invalid stage index
        Map<String, Object> ansWrong1 = experimenteeService.getStageAt(expee.getAccessCode().toString(), 7);
        Assert.assertFalse(ansWrong1.get("response").equals("OK"));
        Map<String, Object> ansRight = experimenteeService.getStageAt(expee.getAccessCode().toString(), 0);
        Assert.assertTrue(ansRight.get("response").equals("OK"));
        Assert.assertEquals(((Map<String, Object>) ansRight.get("stage")).get("text"), ((InfoStage) experiment.getStage(0)).getInfo());
        // expee 1 did not reach stage 1
        Map<String, Object> ansWrong2 = experimenteeService.getStageAt(expee.getAccessCode().toString(), 1);
        Assert.assertFalse(ansWrong2.get("response").equals("OK"));
        experimenteeService.getNextStage(expee.getAccessCode().toString());
        Map<String, Object> ansRight1 = experimenteeService.getStageAt(expee.getAccessCode().toString(), 1);
        Assert.assertTrue(ansRight1.get("response").equals("OK"));
        List<JSONObject> questionsOfStage = ((List<JSONObject>)((Map<String, Object>)ansRight1.get("stage")).get("questions"));
        Assert.assertEquals(questionsOfStage.size(), ((QuestionnaireStage)experiment.getStage(1)).getQuestions().size());
    }

    @Test
    public void reachableStagesTest() throws NotExistException {
        // some random wrong access code, should fail
        Map<String, Object> ansWrong = experimenteeService.reachableStages(UUID.randomUUID().toString());
        Assert.assertTrue(ansWrong.get("stages") == null);
        // only first info stage is reachable
        Map<String, Object> ansRight = experimenteeService.reachableStages(expee.getAccessCode().toString());
        Assert.assertEquals(((List<Map<String,Object>>)ansRight.get("stages")).size(), 1);
        Assert.assertEquals(((List<Map<String,Object>>)ansRight.get("stages")).get(0).get("type"), "info");
        experimenteeService.getNextStage(expee.getAccessCode().toString());
        Map<String, Object> ansRight1 = experimenteeService.reachableStages(expee.getAccessCode().toString());
        Assert.assertEquals(((List<Map<String,Object>>)ansRight1.get("stages")).size(), 2);
        Assert.assertEquals(((List<Map<String,Object>>)ansRight1.get("stages")).get(0).get("type"), "info");
        Assert.assertEquals(((List<Map<String,Object>>)ansRight1.get("stages")).get(1).get("type"), "questionnaire");
        List<JSONObject> questions =  ((List<JSONObject>)((Map<String,Object>)((List<Map<String,Object>>)ansRight1.get("stages")).get(1).get("stage")).get("questions"));
        Assert.assertEquals(questions.size(), ((QuestionnaireStage)experiment.getStage(1)).getQuestions().size());
    }

    @Test
    public void fillInStageTest() throws NotInReachException {
        //filling info does nothing (no result) and moves to next stage
        Map<String,Object> ans = experimenteeService.fillInStage(expee.getAccessCode().toString(), Map.of());
        Assert.assertTrue(ans.get("result") == null);
        Assert.assertTrue(ans.get("type").equals("questionnaire"));
        //filling questionnaire stage
        ans = experimenteeService.fillInStage(expee.getAccessCode().toString(), Map.of("data",Map.of("answers",List.of("a lot!","22"))));
        Assert.assertEquals(((QuestionnaireResult)expee.getParticipant().getResult(1)).getAnswers().size(), 2);
        Assert.assertTrue(ans.get("type").equals("code"));
        //filling code stage
        ans = experimenteeService.fillInStage(expee.getAccessCode().toString(), Map.of("data",Map.of("code","return -1")));
        Assert.assertEquals(((CodeResult)expee.getParticipant().getResult(2)).getUserCode(), "return -1");
        Assert.assertTrue(ans.get("type").equals("tagging"));
        //filling tagging
        ans = experimenteeService.fillInStage(expee.getAccessCode().toString(), Map.of("data",Map.of("tagging", getTags())));
        Assert.assertEquals(((TaggingResult)expee.getParticipant().getResult(3)).getTags().size(), 3);
        Assert.assertTrue(ans.get("type").equals("complete"));
    }

    private JSONObject getTags () {
        JSONObject ans = new JSONObject();
        JSONObject tag1 = new JSONObject();
        tag1.put("start_loc", 0);
        tag1.put("length", 10);
        ans.put(0, tag1);
        JSONObject tag2 = new JSONObject();
        tag2.put("start_loc", 0);
        tag2.put("length", 10);
        ans.put(1, tag2);
        JSONObject tag3 = new JSONObject();
        tag3.put("start_loc", 0);
        tag3.put("length", 10);
        ans.put(2, tag3);
        return ans;
    }

}
