package com.example.demo.BusinessTests;

import com.example.demo.BusinessLayer.*;
import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToParticipant;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.Results.Result;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.*;
import com.example.demo.DBAccess;
import com.example.demo.Utils;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Sql({"/create_database.sql"})
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
    private Participant graderExpeeParticipant;
    private UUID graderCode;


    @BeforeEach
    private void init() throws NotExistException, FormatException, ExistException, CodeException, ExpEndException, NotInReachException, ParseException {
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
    public void loginTest() {
        UUID someCode = UUID.randomUUID();
        Assert.assertFalse(graderBusiness.beginGrading(someCode));
//        Assert.assertNull(db.getGraderToGradingTaskByCode(someCode));
        Assert.assertTrue(graderBusiness.beginGrading(graderCode));
//        Assert.assertNotNull(db.getGraderToGradingTaskByCode(graderCode));
    }

    @Test
    public void getParticipants() throws CodeException, NotExistException, ExistException {
        int expeesForGrader = graderBusiness.getParticipantsByTask(graderCode).size();

        int expees = 5;
        for (int i = 0; i < expees; i++) {
            String mail = "expee" + i + "@post.bgu.ac.il";
            creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), mail);
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(),
                    task.getGradingTaskId(), grader.getGraderEmail(), mail);

        }

        Assert.assertEquals(expees + expeesForGrader, graderBusiness.getParticipantsByTask(graderCode).size());
    }

    @Test
    public void getParticipantResults() throws CodeException, FormatException, NotExistException, NotInReachException {
        List<Result> expeeRes = graderBusiness.getExpeeRes(task.getAssignedGradingTasks().get(0).getGraderAccessCode(),
                expee.getParticipant().getParticipantId());
        Assert.assertEquals(2, expeeRes.size());
    }


    @Test
    @Transactional
    public void currNextStageTest() throws NotInReachException, ExpEndException, CodeException, ParseException, FormatException, NotExistException, ExistException {
        String code = creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), "test@post");
        creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), grader.getGraderEmail(), "test@post");
        Utils.fillInExp(experimenteeBusiness, UUID.fromString(code));
        List<Participant> participants = graderBusiness.getParticipantsByTask(graderCode);

        for (Participant p : participants) {
            Stage s = graderBusiness.getCurrentStage(graderCode, p.getParticipantId());
            Assert.assertEquals("questionnaire", s.getType());
            try {
                graderBusiness.getNextStage(graderCode, p.getParticipantId());
                Assert.fail();
            } catch (ExpEndException ignore) {}
        }
    }

    @Test
    @Transactional
    public void fillStage() {
//
//        //not exist code should fail
//        UUID someCode = UUID.randomUUID();
//        try {
//            graderBusiness.fillInStage(someCode,expee.getParticipant().getParticipantId(), new JSONObject());
//            Assert.fail();
//        } catch (CodeException ignore) {
//            Assert.assertTrue(db.getExperimenteeByCode(someCode) == null);
//        } catch (Exception e) {
//            Assert.fail();
//        }
//
//        // pass info (first) stage
//        try {
//            graderBusiness.getNextStage(expee.getAccessCode(),expee.getParticipant().getParticipantId());
//        } catch (Exception e) {
//            Assert.fail();
//        }
//
//        // fill in questions (second) stage, fucked format should fail
//        long numOfAnswers = db.getNumerOfAnswers();
//        try {
//            JSONObject ans = new JSONObject();
//            ans.put("stageType","questionnaire");
//            JSONObject ans1 = new JSONObject();
//            ans1.put("answer", 2);
//            ans.put(2, ans1);
//            graderBusiness.fillInStage(expee.getAccessCode(),expee.getParticipant().getParticipantId(), ans);
//            Assert.fail();
//        } catch (FormatException ignore) {
//            Assert.assertEquals(db.getNumerOfAnswers(), numOfAnswers);
//        } catch (Exception e) {
//            Assert.fail();
//        }
//
//        // fill in questions (second) stage, good format should pass
//        fillInQuestionnaire(graderCode,graderExpeeParticipant);
//        Assert.assertEquals(db.getNumerOfAnswers(), numOfAnswers + 2);
//
//        // fill in code (third) stage, fucked format should fail
//        long numOfCodeRes = db.getNumerOfCodeResults();
//        try {
//            JSONObject ans = new JSONObject();
//            ans.put("stageType","code");
//            graderBusiness.fillInStage(expee.getAccessCode(),expee.getParticipant().getParticipantId(), ans);
//            Assert.fail();
//        } catch (FormatException ignore) {
//            Assert.assertEquals(db.getNumerOfCodeResults(), numOfCodeRes);
//        } catch (Exception e) {
//            Assert.fail();
//        }
//
//        // fill in code (third) stage, good format should pass
//        fillInCode(graderCode,graderExpeeParticipant);
//        Assert.assertEquals(db.getNumerOfCodeResults(), numOfCodeRes + 1);
//
//        // fill in tagging (last) stage, fucked format should fail
//        long numOfTagRes = db.getNumberOfTagResults();
//        try {
//            JSONObject ans = new JSONObject();
//            ans.put("stageType","tagging");
//            graderBusiness.fillInStage(expee.getAccessCode(),expee.getParticipant().getParticipantId(), ans);
//            Assert.fail();
//        } catch (FormatException ignore) {
//            Assert.assertEquals(numOfTagRes,db.getNumberOfTagResults());
//        } catch (Exception e) {
//            Assert.fail();
//        }
//
//        // fill in code (third) stage, good format should pass
//        fillInTagging(graderExpeeParticipant);
//        Assert.assertEquals(db.getNumberOfTagResults(), numOfTagRes + 3);
//
//        // end of experiment
//        try{
//            graderBusiness.getNextStage(expee.getAccessCode(),expee.getParticipant().getParticipantId());
//            Assert.fail();
//        }catch (ExpEndException ignore){
//            Assert.assertTrue(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().isDone());
//        }
//        catch (Exception e){
//            Assert.fail();
//        }
    }

    private void fillInTagging(Participant p) {
        try {
            Utils.fillInTagging(graderBusiness, p);
//            experimenteeBusiness.getNextStage(expee.getAccessCode());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
    }

    private void fillInQuestionnaire(UUID code, Participant p) {
        try {
            Utils.fillInQuestionnaire(graderBusiness, p);
            graderBusiness.getNextStage(code, p.getParticipantId());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
    }

    void fillInCode(UUID code, Participant p) {
        try {
            Utils.fillInCode(graderBusiness, p);
            graderBusiness.getNextStage(code, p.getParticipantId());
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
