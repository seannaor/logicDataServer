package com.example.demo.BusinessTests;

import com.example.demo.BusinessLayer.*;
import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.Results.Result;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
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

        graderExpeeParticipant = task.getGraderToGradingTask(grader).getGraderParticipant(expee.getParticipant().getParticipantId());
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

        Stage s = graderBusiness.getCurrentStage(graderCode, expee.getParticipant().getParticipantId());
        Assert.assertEquals("info", s.getType());

        s = graderBusiness.getNextStage(graderCode, expee.getParticipant().getParticipantId());
        Assert.assertEquals("questionnaire", s.getType());

        try {
            graderBusiness.getNextStage(graderCode, expee.getParticipant().getParticipantId());
            Assert.fail();
        } catch (ExpEndException ignore) {
        }

        try {
            graderBusiness.getCurrentStage(graderCode, expee.getParticipant().getParticipantId());
            Assert.fail();
        } catch (ExpEndException ignore) {
        }

    }

    // all fill In stage related tests would be positive tests only
    // no need for negative because they been tested in the expeeTests
    @Test
    public void fillStageNoGrader() throws NotInReachException, NotExistException, ExpEndException, ParseException, FormatException {
        //not exist code should fail
        UUID someCode = UUID.randomUUID();
        try {
            graderBusiness.fillInStage(someCode, expee.getParticipant().getParticipantId(), new JSONObject());
            Assert.fail();
        } catch (CodeException ignore) {
//            Assert.assertNull(db.getExperimenteeByCode(someCode));
        }
    }

    @Test
    public void fillPersonal() throws ExpEndException, ParseException, FormatException, NotExistException, CodeException, NotInReachException {
        graderBusiness.getNextStage(graderCode,-1);//pass info
        graderBusiness.fillInStage(graderCode,-1,Utils.getPersonalAnswers());

        try {
            graderBusiness.getNextStage(graderCode, -1);
            Assert.fail();
        } catch (ExpEndException ignore) {
        }

        try {
            graderBusiness.getCurrentStage(graderCode, -1);
            Assert.fail();
        } catch (ExpEndException ignore) {
        }

        try {
            graderBusiness.fillInStage(graderCode, -1,new JSONObject());
            Assert.fail();
        } catch (ExpEndException ignore) {
        }
    }

    @Test
    @Transactional
    public void grade2Expee() throws ExpEndException, ParseException, FormatException, NotExistException, CodeException, NotInReachException, ExistException {
        String expee_mail= "different@post.bgu.ac.il";
        creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), expee_mail);
        creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), grader.getGraderEmail(), expee_mail);

        List<Participant> participants = graderBusiness.getParticipantsByTask(graderCode);

        int pid = participants.get(0).getParticipantId();
        graderBusiness.getNextStage(graderCode,pid);//pass info
        graderBusiness.fillInStage(graderCode,pid,Utils.getGradingAnswers(List.of("this student is stupid","fuck shit")));

        try {
            graderBusiness.getNextStage(graderCode, pid);
            Assert.fail();
        } catch (ExpEndException ignore) {
        }

        graderBusiness.submitGradingResults(graderCode,pid);
        Assert.assertTrue(graderBusiness.isSubmitted(graderCode,pid));

        pid = participants.get(1).getParticipantId();
        Assert.assertFalse(graderBusiness.isSubmitted(graderCode,pid));

        graderBusiness.getNextStage(graderCode,pid);// pass info
        graderBusiness.fillInStage(graderCode,pid,Utils.getGradingAnswers(List.of("this student is smart","List.of(\"this student is stupid\",\"fuck shit\")")));

        try {
            graderBusiness.getNextStage(graderCode, pid);
            Assert.fail();
        } catch (ExpEndException ignore) {
        }
        graderBusiness.submitGradingResults(graderCode,pid);
        Assert.assertTrue(graderBusiness.isSubmitted(graderCode,pid));
    }
}
