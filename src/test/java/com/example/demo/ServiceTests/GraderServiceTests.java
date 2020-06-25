package com.example.demo.ServiceTests;

import com.example.demo.BusinessLayer.CreatorBusiness;
import com.example.demo.BusinessLayer.DataCache;
import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.Results.QuestionnaireResult;
import com.example.demo.BusinessLayer.Entities.Stages.InfoStage;
import com.example.demo.BusinessLayer.Entities.Stages.QuestionnaireStage;
import com.example.demo.BusinessLayer.Exceptions.*;
import com.example.demo.BusinessLayer.ExperimenteeBusiness;
import com.example.demo.DBAccess;
import com.example.demo.ServiceLayer.GraderService;
import com.example.demo.Utils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
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
public class GraderServiceTests {
    private final GraderService graderService;
    private final CreatorBusiness creatorBusiness;
    private final ExperimenteeBusiness experimenteeBusiness;
    private final DataCache cache;
    private final DBAccess db;
    private Grader grader;
    private Experimentee expee;
    private Experiment experiment;
    private GradingTask task;
    private UUID graderCode;

    @Autowired
    public GraderServiceTests(GraderService graderService, CreatorBusiness creatorBusiness, ExperimenteeBusiness experimenteeBusiness, DataCache cache, DBAccess db) {
        this.graderService = graderService;
        this.creatorBusiness = creatorBusiness;
        this.experimenteeBusiness = experimenteeBusiness;
        this.cache = cache;
        this.db = db;
    }

    @BeforeEach
    private void init() throws NotExistException, FormatException, ExistException, CodeException, ParseException, ExpEndException, NotInReachException {
        cache.setCache();
        db.deleteData();
        ManagementUser manager = new ManagementUser("smorad", "sm_pass", "smorad@post.bgu.ac.il");
        cache.addManager(manager);
        experiment = Utils.buildExp(creatorBusiness, manager);
        String code = creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), "gili@post.bgu.ac.il");
        expee = cache.getExpeeByCode(UUID.fromString(code));
        task = Utils.buildSimpleGradingTask(creatorBusiness, cache, manager, experiment);
        creatorBusiness.addGraderToGradingTask(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), "grader@post.bgu.ac.il");
        grader = cache.getGraderByEMail("grader@post.bgu.ac.il");
        graderCode = cache.getGraderToGradingTask(grader, task).getGraderAccessCode();
        creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), grader.getGraderEmail(), expee.getExperimenteeEmail());
        Participant graderExpeeParticipant = task.getGraderToGradingTask(grader).getGraderParticipant(expee.getParticipant().getParticipantId());
        Utils.fillInExp(experimenteeBusiness, expee.getAccessCode(), true);
    }

    @Test
    public void beginGradingTest() {
        // some random wrong access code, should fail
        assertNotEquals("OK", graderService.beginGrading(UUID.randomUUID().toString()).get("response"));
        assertEquals("OK", graderService.beginGrading(graderCode.toString()).get("response"));
    }

    @Test
    public void fillInStageTest() throws NotExistException, NotInReachException {
        // some random wrong access code, should fail
        Map ansWrong = graderService.fillInStage(UUID.randomUUID().toString(), expee.getParticipant().getParticipantId(), new JSONObject());
        assertNotEquals("OK", ansWrong.get("response"));
        // nothing to fill in for info stage
        Map<String, Object> ansRight = graderService.fillInStage(graderCode.toString(), expee.getParticipant().getParticipantId(), Map.of());
        assertNotEquals("OK", ansRight.get("response"));
        //filling questionnaire stage
        graderService.getNextStage(graderCode.toString(), expee.getParticipant().getParticipantId());
        ansRight = graderService.fillInStage(graderCode.toString(), expee.getParticipant().getParticipantId(), Map.of("data", Map.of("answers", List.of("hi", "bye"))));
        Participant graderParticipant = cache.getGraderToGradingTask(grader, task).getGraderToParticipants().get(0).getGraderParticipant();
        assertEquals(((QuestionnaireResult) graderParticipant.getResult(1)).getAnswers().size(), 2);
        assertEquals(((QuestionnaireResult) graderParticipant.getResult(1)).getAnswers().get(0).getAnswer(), "hi");
    }

    @Test
    public void getNextStageTest() throws NotExistException {
        // some random wrong access code, should fail
        Map<String, Object> ansWrong = graderService.getNextStage(UUID.randomUUID().toString(), expee.getParticipant().getParticipantId());
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid pid
        ansWrong = graderService.getNextStage(graderCode.toString(), 9090);
        assertNotEquals("OK", ansWrong.get("response"));
        Map<String, Object> ansRight = graderService.getNextStage(graderCode.toString(), expee.getParticipant().getParticipantId());
        List<JSONObject> questionsOfStage = ((List<JSONObject>) ((Map<String, Object>) ansRight.get("stage")).get("questions"));
        assertEquals(questionsOfStage.size(), ((QuestionnaireStage) experiment.getStage(1)).getQuestions().size());
    }

    @Test
    public void getCurrentStageTest() throws NotExistException {
        // some random wrong access code, should fail
        Map<String, Object> ansWrong = graderService.getCurrentStage(UUID.randomUUID().toString(), expee.getParticipant().getParticipantId());
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid pid
        ansWrong = graderService.getCurrentStage(graderCode.toString(), 9090);
        assertNotEquals("OK", ansWrong.get("response"));
        Map<String, Object> ansRight = graderService.getCurrentStage(graderCode.toString(), expee.getParticipant().getParticipantId());
        Map<String, Object> stageMap = (Map<String, Object>) ansRight.get("stage");
        assertEquals(stageMap.get("text"), ((InfoStage) experiment.getStage(0)).getInfo());
        graderService.getNextStage(graderCode.toString(), expee.getParticipant().getParticipantId());
        ansRight = graderService.getCurrentStage(graderCode.toString(), expee.getParticipant().getParticipantId());
        List<JSONObject> questionsOfStage = ((List<JSONObject>) ((Map<String, Object>) ansRight.get("stage")).get("questions"));
        assertEquals(questionsOfStage.size(), ((QuestionnaireStage) experiment.getStage(1)).getQuestions().size());
    }

    @Test
    public void getStageBadInputsTest() {
        // some random wrong access code, should fail
        Map<String, Object> ansWrong = graderService.getStage(UUID.randomUUID().toString(), expee.getParticipant().getParticipantId(), 0);
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid stage index
        ansWrong = graderService.getStage(graderCode.toString(), 9090, 0);
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid stage index
        ansWrong = graderService.getStage(graderCode.toString(), expee.getParticipant().getParticipantId(), 900);
        assertNotEquals("OK", ansWrong.get("response"));
    }

    @Test
    public void getStageTest() throws NotExistException {
        Map<String, Object> ansRight = graderService.getStage(graderCode.toString(), expee.getParticipant().getParticipantId(), 0);
        assertEquals(((Map<String, Object>) ansRight.get("stage")).get("text"), ((InfoStage) experiment.getStage(0)).getInfo());
        //did not reach stage 1 yet
        ansRight = graderService.getStage(graderCode.toString(), expee.getParticipant().getParticipantId(), 1);
        assertNotEquals("OK", ansRight.get("response"));
        graderService.getNextStage(graderCode.toString(), expee.getParticipant().getParticipantId());
        ansRight = graderService.getStage(graderCode.toString(), expee.getParticipant().getParticipantId(), 1);
        List<JSONObject> questionsOfStage = ((List<JSONObject>) ((Map<String, Object>) ansRight.get("stage")).get("questions"));
        assertEquals(questionsOfStage.size(), ((QuestionnaireStage) experiment.getStage(1)).getQuestions().size());
    }

    @Test
    public void getStageWithResultTest() {
        graderService.getNextStage(graderCode.toString(), expee.getParticipant().getParticipantId());
        graderService.fillInStage(graderCode.toString(), expee.getParticipant().getParticipantId(), Map.of("data", Map.of("answers", List.of("hi", "bye"))));

        Map<String, Object> ansRight = graderService.getStage(graderCode.toString(), expee.getParticipant().getParticipantId(), 1);
        assertNotNull(ansRight.get("results"));
        List<String> answers = (List<String>) ((Map<String, Object>) ansRight.get("results")).get("answers");
        assertEquals(2, answers.size());
    }

    @Test
    public void getExperimenteesTest() throws NotExistException {
        // some random wrong access code, should fail
        Map<String, Object> ansWrong = graderService.getExperimentees(UUID.randomUUID().toString());
        assertNotEquals("OK", ansWrong.get("response"));
        Map<String, Object> ansRight = graderService.getExperimentees(graderCode.toString());
        List<Integer> experimentees = (List<Integer>) (ansRight.get("experimentees"));
        assertEquals(experimentees.size(), cache.getGraderToGradingTask(grader, task).getGraderToParticipants().size());
        assertEquals(experimentees.get(0).intValue(), cache.getGraderToGradingTask(grader, task).getGraderToParticipants().get(0).getExpeeParticipant().getParticipantId());
    }

    @Test
    public void getExperimenteeResultsTest() throws NotInReachException {
        // some random wrong access code, should fail
        Map<String, Object> ansWrong = graderService.getExperimenteeResults(UUID.randomUUID().toString(), expee.getParticipant().getParticipantId());
        assertNotEquals("OK", ansWrong.get("response"));
        // invalid pid
        ansWrong = graderService.getExperimenteeResults(graderCode.toString(), 9090);
        assertNotEquals("OK", ansWrong.get("response"));
        Map<String, Object> ansRight = graderService.getExperimenteeResults(graderCode.toString(), expee.getParticipant().getParticipantId());
        List<Map<String, Object>> results = (List<Map<String, Object>>) (ansRight.get("results"));
        // expee has already finished the experiment (since we got results), grader can only see results that are visible to him (which are in the gradingTask)
        assertEquals(results.size(), task.getStages().size());
        assertEquals(results.get(0), expee.getParticipant().getResult(1).getAsMap());
    }

}
