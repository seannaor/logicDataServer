package com.example.demo.BusinessTests;

import com.example.demo.BusinessLayer.CreatorBusiness;
import com.example.demo.BusinessLayer.DataCache;
import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.Result;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.*;
import com.example.demo.BusinessLayer.ExperimenteeBusiness;
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
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Sql({"/create_database.sql"})
@SpringBootTest
public class ExpeeTests {

    private ExperimenteeBusiness experimenteeBusiness;
    private CreatorBusiness creatorBusiness;
    private DataCache cache;
    private DBAccess db;

    @Autowired
    public ExpeeTests(ExperimenteeBusiness experimenteeBusiness, CreatorBusiness creatorBusiness, DataCache cache, DBAccess db) {
        this.experimenteeBusiness = experimenteeBusiness;
        this.creatorBusiness = creatorBusiness;
        this.cache = cache;
        this.db = db;
    }

    private ManagementUser manager;
    private Experiment experiment;
    private Experimentee expee;

    @BeforeEach
    private void init() throws NotExistException, FormatException, ExistException, CodeException {
        cache.setCache();
        db.deleteData();

        manager = new ManagementUser("smorad", "sm_pass", "smorad@post.bgu.ac.il");
        cache.addManager(manager);

        experiment = Utils.buildExp(creatorBusiness, manager);

        String code = creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), "gili@post.bgu.ac.il");
        expee = cache.getExpeeByCode(UUID.fromString(code));
    }

    @Test
    public void loginTest() {
        //not exist code should return false
        UUID someCode = UUID.randomUUID();
        assertFalse(experimenteeBusiness.beginParticipation(someCode));
        //real code should return true
        assertTrue(experimenteeBusiness.beginParticipation(expee.getAccessCode()));
    }

    @Test
    public void currStageFailTest() throws NotExistException, ExpEndException {
        //not exist code should fail
        UUID someCode = UUID.randomUUID();
        assertThrows(CodeException.class, () -> {
            experimenteeBusiness.getCurrentStage(someCode);
        });
    }

    @Test
    public void nextStageFailTest() throws NotExistException, ExpEndException {
        //not exist code should fail
        UUID someCode = UUID.randomUUID();
        assertThrows(CodeException.class, () -> {
            experimenteeBusiness.getNextStage(someCode);
        });
    }

    @Test
    @Transactional
    public void currStageTest() throws NotExistException, CodeException, ExpEndException {
        // real code should get us the first stage - info
        Stage first = experimenteeBusiness.getCurrentStage(expee.getAccessCode());
        assertEquals(first.getType(), "info");
        assertEquals(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().getCurrStage().getType(), "info");
    }

    @Test
    @Transactional
    public void nextStageTest() throws NotExistException, CodeException, ExpEndException {
        // real code should get us the next stages

        Stage s = experimenteeBusiness.getNextStage(expee.getAccessCode());
        assertEquals(s.getType(), "questionnaire");
        assertEquals(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().getCurrStage().getType(), "questionnaire");

        s = experimenteeBusiness.getNextStage(expee.getAccessCode());
        assertEquals(s.getType(), "code");
        assertEquals(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().getCurrStage().getType(), "code");

        s = experimenteeBusiness.getNextStage(expee.getAccessCode());
        assertEquals(s.getType(), "tagging");
        assertEquals(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().getCurrStage().getType(), "tagging");
    }

    @Test
    public void endExperimentTest() throws NotExistException, CodeException, ExpEndException, ParseException, FormatException, NotInReachException {
        nextStageFor(3, expee.getAccessCode());

        assertThrows(ExpEndException.class, () -> {
            // end of exp - next should fail
            experimenteeBusiness.getNextStage(expee.getAccessCode());
        });
        Participant sameExpee = db.getExperimenteeByCode(expee.getAccessCode()).getParticipant();
        assertTrue(sameExpee.isDone());

        assertThrows(ExpEndException.class, () -> {
            // current stage should fail because next was activated first
            experimenteeBusiness.getCurrentStage(expee.getAccessCode());
        });

        assertThrows(ExpEndException.class, () -> {
            experimenteeBusiness.fillInStage(expee.getAccessCode(), new JSONObject());
        });
    }

    @Test
    public void fillStageFail() throws NotExistException, NotInReachException, ParseException, ExpEndException, FormatException {
        //not exist code should fail
        UUID someCode = UUID.randomUUID();
        assertThrows(CodeException.class, () -> {
            experimenteeBusiness.fillInStage(someCode, new JSONObject());
        });

        assertNull(expee.getResult(0));
    }

    @Test
    public void fillQuestionsFail() throws NotExistException, CodeException, ExpEndException, NotInReachException, ParseException {
        experimenteeBusiness.getNextStage(expee.getAccessCode());

        // fill in questions (second) stage, fucked format should fail
        long numOfAnswers = db.getNumerOfAnswers();
        assertThrows(FormatException.class, () -> {
            JSONObject ans = new JSONObject();
            ans.put("stageType", "questionnaire");
            JSONObject ans1 = new JSONObject();
            ans1.put("answer", 2);
            ans.put(2, ans1);
            experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
        });
        assertEquals(db.getNumerOfAnswers(), numOfAnswers);
        assertNull(expee.getResult(1));
    }

    @Test
    public void fillCodeFail() throws NotExistException, CodeException, ExpEndException, NotInReachException, ParseException {
        nextStageFor(2, expee.getAccessCode());
        // fill in code (third) stage, fucked format should fail
        long numOfCodeRes = db.getNumerOfCodeResults();
        assertThrows(FormatException.class, () -> {
            JSONObject ans = new JSONObject();
            ans.put("stageType", "code");
            experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
        });
        assertEquals(db.getNumerOfCodeResults(), numOfCodeRes);
        assertNull(expee.getResult(2));
    }

    @Test
    public void fillTaggingFail() throws NotExistException, CodeException, ExpEndException, NotInReachException, ParseException {
        nextStageFor(3, expee.getAccessCode());
        // fill in tagging (last) stage, fucked format should fail
        long numOfTagRes = db.getNumberOfTagResults();
        assertThrows(FormatException.class, () -> {
            experimenteeBusiness.fillInStage(expee.getAccessCode(), new HashMap<>());
        });
        assertEquals(numOfTagRes, db.getNumberOfTagResults());
        assertNull(expee.getResult(3));
    }

    @Test
    @Transactional
    public void fillInTagging() throws NotExistException, CodeException, ExpEndException, NotInReachException, ParseException, FormatException {
        nextStageFor(3, expee.getAccessCode());
        long numOfTagRes = db.getNumberOfTagResults();

        //fill tagging dont really answer this stage, sill got weird problem there
        int numofTags = Utils.fillInTagging(experimenteeBusiness, expee.getAccessCode());

        assertEquals("tagging", expee.getResult(3).getStage().getType());
        assertEquals(numOfTagRes + numofTags, db.getNumberOfTagResults());
    }

    @Test
    public void fillInQuestionnaire() throws NotExistException, CodeException, ExpEndException, NotInReachException, ParseException, FormatException {
        long numOfAnswers = db.getNumerOfAnswers();
        experimenteeBusiness.getNextStage(expee.getAccessCode());

        int numOfquestionsAnswered = Utils.fillInQuestionnaire(experimenteeBusiness, expee.getAccessCode());
        experimenteeBusiness.getNextStage(expee.getAccessCode());

        assertEquals("questionnaire", expee.getResult(1).getStage().getType());
        assertEquals(numOfAnswers + numOfquestionsAnswered, db.getNumerOfAnswers());
    }

    @Test
    public void fillInCode() throws NotExistException, CodeException, ExpEndException, NotInReachException, ParseException, FormatException {
        long numOfCodeRes = db.getNumerOfCodeResults();
        nextStageFor(2, expee.getAccessCode());

        Utils.fillInCode(experimenteeBusiness, expee.getAccessCode());
        experimenteeBusiness.getNextStage(expee.getAccessCode());

        assertEquals("code", expee.getResult(2).getStage().getType());
        assertEquals(numOfCodeRes + 1, db.getNumerOfCodeResults());
    }

    @Test
    public void getStageTest() throws ParseException, CodeException, NotExistException, FormatException, ExpEndException, NotInReachException {
        nextStageFor(3, expee.getAccessCode());
        Utils.fillInTagging(experimenteeBusiness, expee.getAccessCode());

        Stage s = experimenteeBusiness.getStage(expee.getAccessCode(),3);
        assertEquals(expee.getExperiment().getStages().get(3),s);

        Result r = experimenteeBusiness.getResult(expee.getAccessCode(),3);
        assertNotNull(r);
    }

    private void nextStageFor(int i, UUID code) throws NotExistException, CodeException, ExpEndException {
        for (int j = 0; j < i; j++) experimenteeBusiness.getNextStage(code);
    }
}
