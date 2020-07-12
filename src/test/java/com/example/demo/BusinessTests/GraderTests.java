package com.example.demo.BusinessTests;

import com.example.demo.BusinessLayer.CreatorBusiness;
import com.example.demo.BusinessLayer.DataCache;
import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.Results.Result;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.*;
import com.example.demo.BusinessLayer.ExperimenteeBusiness;
import com.example.demo.BusinessLayer.GraderBusiness;
import com.example.demo.DBAccess;
import com.example.demo.Utils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Sql({"/create_tests_database.sql"})
@SpringBootTest
public class GraderTests {

    private GraderBusiness graderBusiness;
    private CreatorBusiness creatorBusiness;
    private ExperimenteeBusiness experimenteeBusiness;
    private DataCache cache;
    private DBAccess db;
    private Grader grader;
    private ManagementUser manager;
    private Experimentee expee;
    private Experiment experiment;
    private GradingTask task;
    private Participant graderExpeeParticipant;
    private UUID graderCode;

    @Autowired
    public GraderTests(GraderBusiness graderBusiness, CreatorBusiness creatorBusiness, ExperimenteeBusiness experimenteeBusiness, DataCache cache, DBAccess db) {
        this.graderBusiness = graderBusiness;
        this.creatorBusiness = creatorBusiness;
        this.experimenteeBusiness = experimenteeBusiness;
        this.cache = cache;
        this.db = db;
    }

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
        Utils.fillInExp(experimenteeBusiness, expee.getAccessCode(), true);
    }

    @Test
    public void loginTest() {
        UUID someCode = UUID.randomUUID();
        assertFalse(graderBusiness.beginGrading(someCode));
//        assertNull(db.getGraderToGradingTaskByCode(someCode));
        assertTrue(graderBusiness.beginGrading(graderCode));
//        assertNotNull(db.getGraderToGradingTaskByCode(graderCode));
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

        assertEquals(expees + expeesForGrader, graderBusiness.getParticipantsByTask(graderCode).size());
    }

    @Test
    public void getParticipantResults() throws CodeException, FormatException, NotExistException, NotInReachException {
        List<Result> expeeRes = graderBusiness.getExpeeRes(task.getAssignedGradingTasks().get(0).getGraderAccessCode(),
                expee.getParticipant().getParticipantId());
        assertEquals(2, expeeRes.size());
    }

    @Test
    @Transactional
    public void currNextStageTest() throws ExpEndException, CodeException, NotExistException {

        Stage s = graderBusiness.getCurrentStage(graderCode, expee.getParticipant().getParticipantId());
        assertEquals("info", s.getType());

        s = graderBusiness.getNextStage(graderCode, expee.getParticipant().getParticipantId());
        assertEquals("questionnaire", s.getType());

        assertThrows(ExpEndException.class, () -> {
            graderBusiness.getNextStage(graderCode, expee.getParticipant().getParticipantId());
        });

        assertThrows(ExpEndException.class, () -> {
            graderBusiness.getCurrentStage(graderCode, expee.getParticipant().getParticipantId());
        });

    }

    // all fill In stage related tests would be positive tests only
    // no need for negative because they been tested in the expeeTests
    @Test
    public void fillStageNoGrader() {
        //not exist code should fail
        UUID someCode = UUID.randomUUID();
        assertThrows(CodeException.class, () -> {
            graderBusiness.fillInStage(someCode, expee.getParticipant().getParticipantId(), new JSONObject());
        });
//            assertNull(db.getExperimenteeByCode(someCode));
    }

    @Test
    public void fillPersonal() throws ExpEndException, ParseException, FormatException, NotExistException, CodeException, NotInReachException {
        graderBusiness.getNextStage(graderCode, -1);//pass info
        graderBusiness.fillInStage(graderCode, -1, Utils.getPersonalAnswers());

        assertThrows(ExpEndException.class, () -> {
            graderBusiness.getNextStage(graderCode, -1);
        });

        assertThrows(ExpEndException.class, () -> {
            graderBusiness.getCurrentStage(graderCode, -1);
        });

        assertThrows(ExpEndException.class, () -> {
            graderBusiness.fillInStage(graderCode, -1, new JSONObject());
        });
    }

    @Test
    public void getStageAndResult() throws ExpEndException, ParseException, FormatException, NotExistException, CodeException, NotInReachException {
        graderBusiness.getNextStage(graderCode, -1);//pass info
        graderBusiness.fillInStage(graderCode, -1, Utils.getPersonalAnswers());

        Stage s = graderBusiness.getStage(graderCode, -1, 0);
        assertEquals("info", s.getType());
        Result r = graderBusiness.getResult(graderCode, -1, 1);
        assertNotNull(r);
        System.out.println(r.getAsMap().toString());
        assertEquals(1, ((List) r.getAsMap().get("answers")).size());
    }

    @Test
    @Transactional
    public void grader2Expee() throws ExpEndException, ParseException, FormatException, NotExistException, CodeException, NotInReachException, ExistException {
        String expee_mail = "different@post.bgu.ac.il";
        creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), expee_mail);
        creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), grader.getGraderEmail(), expee_mail);

        List<Participant> participants = graderBusiness.getParticipantsByTask(graderCode);

        int pid = participants.get(0).getParticipantId();
        graderBusiness.getNextStage(graderCode, pid);//pass info
        graderBusiness.fillInStage(graderCode, pid, Utils.getGradingAnswers(List.of("first ans", "second ans")));

        int finalPid = pid;
        assertThrows(ExpEndException.class, () -> {
            graderBusiness.getNextStage(graderCode, finalPid);
        });

        graderBusiness.submitGradingResults(graderCode, pid);
        assertTrue(graderBusiness.isSubmitted(graderCode, pid));

        pid = participants.get(1).getParticipantId();
        assertFalse(graderBusiness.isSubmitted(graderCode, pid));

        graderBusiness.getNextStage(graderCode, pid);// pass info
        graderBusiness.fillInStage(graderCode, pid, Utils.getGradingAnswers(List.of("and1", "ans2")));

        int finalPid1 = pid;
        assertThrows(ExpEndException.class, () -> {
            graderBusiness.getNextStage(graderCode, finalPid1);
        });
        graderBusiness.submitGradingResults(graderCode, pid);
        assertTrue(graderBusiness.isSubmitted(graderCode, pid));
    }
}
