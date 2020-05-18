package com.example.demo.BusinessTests;

import com.example.demo.BusinessLayer.*;
import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradersGTToParticipants;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.Results.Result;
import com.example.demo.BusinessLayer.Exceptions.*;
import com.example.demo.DBAccess;
import com.example.demo.Utils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@SpringBootTest
public class GraderTests {

    private GraderBusiness graderBusiness;

    private CreatorBusiness creatorBusiness;

    private ExperimenteeBusiness experimenteeBusiness;

    private DataCache cache;

    private DBAccess db;

    @Autowired
    public GraderTests(GraderBusiness graderBusiness, CreatorBusiness creatorBusiness, ExperimenteeBusiness experimenteeBusiness, DataCache cache, DBAccess db) {
        this.graderBusiness = graderBusiness;
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
    private UUID graderCode;


    @BeforeEach
    private void init() throws NotExistException, FormatException, ExistException, CodeException, ExpEndException, NotInReachException, ParseException {
        cache.setCache();
        db.deleteData();

        manager = new ManagementUser("smorad", "sm_pass", "smorad@post.bgu.ac.il");
        cache.addManager(manager);

        buildExp();

        expee = new Experimentee("gili@post", experiment);
        cache.addExperimentee(expee);

        grader = new Grader("grader@post.bgu.ac.il", experiment);
        cache.addGrader(grader);

        buildGradingTask();

        cache.addExpeeToGradingTask(task, grader, new GradersGTToParticipants(cache.getGraderToGradingTask(grader, task), expee.getParticipant()));

        fillInExp();
    }

    private void fillInExp() throws CodeException, ExpEndException, ParseException, FormatException, NotInReachException {
        // pass info (first) stage
        experimenteeBusiness.getNextStage(expee.getAccessCode());

        // fill in questions (second) stage, good format should pass
        fillInQuestionnaire(expee);

        // fill in code (third) stage, good format should pass
        fillInCode(expee);
    }

    private void fillInQuestionnaire(Experimentee expee) throws NotInReachException, ExpEndException, CodeException, ParseException, FormatException {
        JSONObject ans = new JSONObject();
        ans.put("stageType", "questionnaire");
        JSONObject ans1 = new JSONObject();
        ans1.put("answer", "WTF!!!");
        ans.put("1", ans1);
        JSONObject ans2 = new JSONObject();
        ans2.put("answer", 3);
        ans.put("2", ans2);
        experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
        experimenteeBusiness.getNextStage(expee.getAccessCode());

    }

    private void fillInCode(Experimentee expee) throws NotInReachException, ExpEndException, CodeException, ParseException, FormatException {
        JSONObject ans = new JSONObject();
        ans.put("stageType", "code");
        ans.put("userCode", "return -1");
        experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
    }

    private void buildGradingTask() throws ExistException, NotExistException, FormatException {
        task = new GradingTask("test", experiment, experiment, experiment);
        task.setStagesByIdx(List.of(1,2));
        cache.addGradingTask(task);
        cache.addGraderToGradingTask(task, grader);
        graderCode = cache.getGraderToGradingTask(grader, task).getGraderAccessCode();
    }

    private void buildExp() throws NotExistException, FormatException, ExistException {
        List<JSONObject> stages = Utils.buildStages();
        creatorBusiness.addExperiment(manager.getBguUsername(), "The Experiment", stages);
        experiment = manager.getExperimentByName("The Experiment");
    }

    @Test
    public void loginTest() {
        UUID someCode = UUID.randomUUID();
        Assert.assertFalse(graderBusiness.beginGrading(someCode));
        Assert.assertTrue(db.getGraderToGradingTaskByCode(someCode) == null);
        Assert.assertTrue(graderBusiness.beginGrading(graderCode));
        Assert.assertTrue(db.getGraderToGradingTaskByCode(graderCode) != null);
    }

    @Test
    public void getParticipants() {
        int expeesForGrader = 0;
        try {
            expeesForGrader = graderBusiness.getParticipantsByTask(graderCode).size();
        } catch (CodeException e) {
            Assert.fail();
        }

        int expees = 5;
        for (int i = 0; i < expees; i++) {
            try {
                String mail = "expee" + i + "@post.bgu.ac.il";
                creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), mail);
                creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(),
                        task.getGradingTaskId(), grader.getGraderEmail(), mail);
            } catch (Exception e) {
                Assert.fail();
            }
        }

        try {
            Assert.assertEquals(expees + expeesForGrader, graderBusiness.getParticipantsByTask(graderCode).size());
        } catch (CodeException e) {
            Assert.fail();
        }
    }

    @Test
    public void getParticipantResults() {
        List<Result> expeeRes;
        try {
            expeeRes=graderBusiness.getExpeeRes(task.getAssignedGradingTasks().get(0).getGraderAccessCode(),expee.getParticipant().getParticipantId());
            Assert.assertEquals(2,expeeRes.size());
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
