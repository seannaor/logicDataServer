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
import com.example.demo.BusinessLayer.ExperimenteeBusiness;
import com.example.demo.DBAccess;
import com.example.demo.ServiceLayer.ExperimenteeService;
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

import static org.junit.jupiter.api.Assertions.*;

@Sql({"/create_database.sql"})
@SpringBootTest
public class ExperimenteeServiceTests {
    private final ExperimenteeService experimenteeService;
    private final ExperimenteeBusiness experimenteeBusiness;
    private final CreatorBusiness creatorBusiness;
    private final DataCache cache;
    private final DBAccess db;

    @Autowired
    public ExperimenteeServiceTests(ExperimenteeService experimenteeService, CreatorBusiness creatorBusiness, DataCache cache, DBAccess db, ExperimenteeBusiness experimenteeBusiness) {
        this.experimenteeService = experimenteeService;
        this.creatorBusiness = creatorBusiness;
        this.cache = cache;
        this.db = db;
        this.experimenteeBusiness = experimenteeBusiness;
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
        assertFalse(experimenteeService.beginParticipation(UUID.randomUUID().toString()));
        assertTrue(experimenteeService.beginParticipation(expee.getAccessCode().toString()));
    }

    @Test
    public void getNextStageTest() throws NotExistException {
        // some random wrong access code, should fail
        Map<String, Object> ansWrong = experimenteeService.getNextStage(UUID.randomUUID().toString());
        assertTrue(ansWrong.containsKey("Error"));
        Map<String, Object> ansRight = experimenteeService.getNextStage(expee.getAccessCode().toString());
        assertTrue(ansRight.containsKey("type"));
        assertEquals(ansRight.get("type"), "questionnaire");
        List<JSONObject> questionsOfStage = ((List<JSONObject>) ((Map<String, Object>) ansRight.get("stage")).get("questions"));
        assertEquals(questionsOfStage.size(), ((QuestionnaireStage) experiment.getStage(1)).getQuestions().size());
        assertEquals(experimenteeService.getNextStage(expee.getAccessCode().toString()).get("type"), "code");
    }

    @Test
    public void getStageWithResultsTest() throws ExpEndException, ParseException, NotExistException, FormatException, CodeException, NotInReachException {
        Utils.fillInExp(experimenteeBusiness, expee.getAccessCode(),false);
        Map<String, Object> ansRight = experimenteeService.getCurrentStage(expee.getAccessCode().toString());
        assertNotNull(ansRight.get("result"));

        ansRight = experimenteeService.getStageAt(expee.getAccessCode().toString(),1);
        assertNotNull(ansRight.get("results"));
    }

    @Test
    public void getCurrentStageTest() throws NotExistException {
        // some random wrong access code, should fail
        Map<String, Object> ansWrong = experimenteeService.getCurrentStage(UUID.randomUUID().toString());
        assertNotEquals("OK", ansWrong.get("response"));
        Map<String, Object> ansRight = experimenteeService.getCurrentStage(expee.getAccessCode().toString());
        assertEquals("OK", ansRight.get("response"));
        assertEquals(ansRight.get("type"), "info");
        assertEquals(((Map<String, Object>) ansRight.get("stage")).get("text"), ((InfoStage) experiment.getStage(0)).getInfo());
        experimenteeService.getNextStage(expee.getAccessCode().toString());
        assertEquals(experimenteeService.getCurrentStage(expee.getAccessCode().toString()).get("type"), "questionnaire");
    }

    @Test
    public void getStageAtTest() throws NotExistException {
        // some random wrong access code, should fail
        Map<String, Object> ansWrong = experimenteeService.getStageAt(UUID.randomUUID().toString(), 0);
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid stage index
        Map<String, Object> ansWrong1 = experimenteeService.getStageAt(expee.getAccessCode().toString(), 7);
        assertNotEquals("OK", ansWrong1.get("response"));
        Map<String, Object> ansRight = experimenteeService.getStageAt(expee.getAccessCode().toString(), 0);
        assertEquals("OK", ansRight.get("response"));
        assertEquals(((Map<String, Object>) ansRight.get("stage")).get("text"), ((InfoStage) experiment.getStage(0)).getInfo());
        // expee 1 did not reach stage 1
        Map<String, Object> ansWrong2 = experimenteeService.getStageAt(expee.getAccessCode().toString(), 1);
        assertNotEquals("OK", ansWrong2.get("response"));
        experimenteeService.getNextStage(expee.getAccessCode().toString());
        Map<String, Object> ansRight1 = experimenteeService.getStageAt(expee.getAccessCode().toString(), 1);
        assertEquals("OK", ansRight1.get("response"));
        List<JSONObject> questionsOfStage = ((List<JSONObject>) ((Map<String, Object>) ansRight1.get("stage")).get("questions"));
        assertEquals(questionsOfStage.size(), ((QuestionnaireStage) experiment.getStage(1)).getQuestions().size());
    }

    @Test
    public void reachableStagesTest() throws NotExistException {
        // some random wrong access code, should fail
        Map<String, Object> ansWrong = experimenteeService.reachableStages(UUID.randomUUID().toString());
        assertNull(ansWrong.get("stages"));
        // only first info stage is reachable
        Map<String, Object> ansRight = experimenteeService.reachableStages(expee.getAccessCode().toString());
        assertEquals(((List<Map<String, Object>>) ansRight.get("stages")).size(), 1);
        assertEquals(((List<Map<String, Object>>) ansRight.get("stages")).get(0).get("type"), "info");
        experimenteeService.getNextStage(expee.getAccessCode().toString());
        Map<String, Object> ansRight1 = experimenteeService.reachableStages(expee.getAccessCode().toString());
        assertEquals(((List<Map<String, Object>>) ansRight1.get("stages")).size(), 2);
        assertEquals(((List<Map<String, Object>>) ansRight1.get("stages")).get(0).get("type"), "info");
        assertEquals(((List<Map<String, Object>>) ansRight1.get("stages")).get(1).get("type"), "questionnaire");
        List<JSONObject> questions = ((List<JSONObject>) ((Map<String, Object>) ((List<Map<String, Object>>) ansRight1.get("stages")).get(1).get("stage")).get("questions"));
        assertEquals(questions.size(), ((QuestionnaireStage) experiment.getStage(1)).getQuestions().size());
    }

    @Test
    public void fillInStageTest() throws NotInReachException {
        //filling info does nothing (no result) and moves to next stage
        Map<String, Object> ans = experimenteeService.fillInStage(expee.getAccessCode().toString(), Map.of());
        assertNull(ans.get("result"));
        assertEquals("questionnaire", ans.get("type"));
        //filling questionnaire stage
        ans = experimenteeService.fillInStage(expee.getAccessCode().toString(), Map.of("data", Map.of("answers", List.of("a lot!", "22"))));
        assertEquals(((QuestionnaireResult) expee.getParticipant().getResult(1)).getAnswers().size(), 2);
        assertEquals("code", ans.get("type"));
        //filling code stage
        ans = experimenteeService.fillInStage(expee.getAccessCode().toString(), Map.of("data", Map.of("code", "return -1")));
        assertEquals(((CodeResult) expee.getParticipant().getResult(2)).getUserCode(), "return -1");
        assertEquals("tagging", ans.get("type"));
        //filling tagging
        ans = experimenteeService.fillInStage(expee.getAccessCode().toString(), Map.of("data", Map.of("tagging", getTags())));
        assertEquals(((TaggingResult) expee.getParticipant().getResult(3)).getTags().size(), 3);
        assertEquals("complete", ans.get("type"));
    }

    @Test
    public void fillInStageFailTest(){
        experimenteeService.getNextStage(expee.getAccessCode().toString());
        Map<String, Object> ans =experimenteeService.fillInStage(expee.getAccessCode().toString(), Map.of("data", Map.of()));
        assertTrue(ans.containsKey("Error"));
    }

    private JSONObject getTags() {
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
