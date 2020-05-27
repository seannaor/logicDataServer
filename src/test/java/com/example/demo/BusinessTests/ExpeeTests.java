package com.example.demo.BusinessTests;

import com.example.demo.BusinessLayer.*;
import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import com.example.demo.BusinessLayer.Entities.Participant;
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
import java.util.HashMap;
import java.util.UUID;

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
        Assert.assertFalse(experimenteeBusiness.beginParticipation(someCode));
        //real code should return true
        Assert.assertTrue(experimenteeBusiness.beginParticipation(expee.getAccessCode()));
    }

    @Test
//    @Transactional
    public void currStageFailTest() throws NotExistException, ExpEndException {
        //not exist code should fail
        UUID someCode = UUID.randomUUID();
        try {
            experimenteeBusiness.getCurrentStage(someCode);
            Assert.fail();
        } catch (CodeException ignore) {
        }
    }

    @Test
//    @Transactional
    public void nextStageFailTest() throws NotExistException, ExpEndException {
        //not exist code should fail
        UUID someCode = UUID.randomUUID();
        try {
            experimenteeBusiness.getNextStage(someCode);
            Assert.fail();
        } catch (CodeException ignore) {
        }
    }

    @Test
    @Transactional
    public void currStageTest() throws NotExistException, CodeException, ExpEndException {
        // real code should get us the first stage - info
        Stage first = experimenteeBusiness.getCurrentStage(expee.getAccessCode());
        Assert.assertEquals(first.getType(), "info");
        Assert.assertEquals(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().getCurrStage().getType(), "info");
    }

    @Test
    @Transactional
    public void nextStageTest() throws NotExistException, CodeException, ExpEndException {
        // real code should get us the next stages

        Stage s = experimenteeBusiness.getNextStage(expee.getAccessCode());
        Assert.assertEquals(s.getType(), "questionnaire");
        Assert.assertEquals(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().getCurrStage().getType(), "questionnaire");

        s = experimenteeBusiness.getNextStage(expee.getAccessCode());
        Assert.assertEquals(s.getType(), "code");
        Assert.assertEquals(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().getCurrStage().getType(), "code");

        s = experimenteeBusiness.getNextStage(expee.getAccessCode());
        Assert.assertEquals(s.getType(), "tagging");
        Assert.assertEquals(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().getCurrStage().getType(), "tagging");
    }

    @Test
    public void endExperimentTest() throws NotExistException, CodeException, ExpEndException, ParseException, FormatException, NotInReachException {
        nextStageFor(3, expee.getAccessCode());

        // end of exp - next should fail
        try {
            experimenteeBusiness.getNextStage(expee.getAccessCode());
            Assert.fail();
        } catch (ExpEndException ignore) {
            Participant sameExpee = db.getExperimenteeByCode(expee.getAccessCode()).getParticipant();
            Assert.assertTrue(sameExpee.isDone());
        }

        // current stage should fail because next was activated first
        try {
            experimenteeBusiness.getCurrentStage(expee.getAccessCode());
            Assert.fail();
        } catch (ExpEndException ignore) {
        }

        try {
            experimenteeBusiness.fillInStage(expee.getAccessCode(),new JSONObject());
            Assert.fail();
        } catch (ExpEndException ignore) {
        }
    }

    @Test
    public void fillStageFail() throws NotExistException, NotInReachException, ParseException, ExpEndException, FormatException {
        //not exist code should fail
        UUID someCode = UUID.randomUUID();
        try {
            experimenteeBusiness.fillInStage(someCode, new JSONObject());
            Assert.fail();
        } catch (CodeException ignore) {
        }

        Assert.assertNull(expee.getResult(0));
    }

    @Test
    public void fillQuestionsFail() throws NotExistException, CodeException, ExpEndException, NotInReachException, ParseException {
        experimenteeBusiness.getNextStage(expee.getAccessCode());

        // fill in questions (second) stage, fucked format should fail
        long numOfAnswers = db.getNumerOfAnswers();
        try {
            JSONObject ans = new JSONObject();
            ans.put("stageType", "questionnaire");
            JSONObject ans1 = new JSONObject();
            ans1.put("answer", 2);
            ans.put(2, ans1);
            experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
            Assert.fail();
        } catch (FormatException ignore) {
            Assert.assertEquals(db.getNumerOfAnswers(), numOfAnswers);
        }

        Assert.assertNull(expee.getResult(1));
    }

    @Test
    public void fillCodeFail() throws NotExistException, CodeException, ExpEndException, NotInReachException, ParseException {
        nextStageFor(2, expee.getAccessCode());

        // fill in code (third) stage, fucked format should fail
        long numOfCodeRes = db.getNumerOfCodeResults();
        try {
            JSONObject ans = new JSONObject();
            ans.put("stageType", "code");
            experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
            Assert.fail();
        } catch (FormatException ignore) {
            Assert.assertEquals(db.getNumerOfCodeResults(), numOfCodeRes);
        }

        Assert.assertNull(expee.getResult(2));
    }

    @Test
    public void fillTaggingFail() throws NotExistException, CodeException, ExpEndException, NotInReachException, ParseException {
        nextStageFor(3, expee.getAccessCode());
        // fill in tagging (last) stage, fucked format should fail
        long numOfTagRes = db.getNumberOfTagResults();
        try {
            experimenteeBusiness.fillInStage(expee.getAccessCode(), new HashMap<>());
            Assert.fail();
        } catch (FormatException ignore) {
            Assert.assertEquals(numOfTagRes, db.getNumberOfTagResults());
        }

        Assert.assertNull(expee.getResult(3));
    }

    @Test
    @Transactional
    public void fillInTagging() throws NotExistException, CodeException, ExpEndException, NotInReachException, ParseException, FormatException {
        nextStageFor(3, expee.getAccessCode());
        long numOfTagRes = db.getNumberOfTagResults();

        //fill tagging dont really answer this stage, sill got weird problem there
        int numofTags = Utils.fillInTagging(experimenteeBusiness, expee.getAccessCode());

        Assert.assertEquals("tagging", expee.getResult(3).getStage().getType());
        Assert.assertEquals(numOfTagRes + numofTags, db.getNumberOfTagResults());
    }

    @Test
    public void fillInQuestionnaire() throws NotExistException, CodeException, ExpEndException, NotInReachException, ParseException, FormatException {
        long numOfAnswers = db.getNumerOfAnswers();
        experimenteeBusiness.getNextStage(expee.getAccessCode());

        int numOfquestionsAnswered = Utils.fillInQuestionnaire(experimenteeBusiness, expee.getAccessCode());
        experimenteeBusiness.getNextStage(expee.getAccessCode());

        Assert.assertEquals("questionnaire", expee.getResult(1).getStage().getType());
        Assert.assertEquals(numOfAnswers + numOfquestionsAnswered, db.getNumerOfAnswers());
    }

    @Test
    public void fillInCode() throws NotExistException, CodeException, ExpEndException, NotInReachException, ParseException, FormatException {
        long numOfCodeRes = db.getNumerOfCodeResults();
        nextStageFor(2, expee.getAccessCode());

        Utils.fillInCode(experimenteeBusiness, expee.getAccessCode());
        experimenteeBusiness.getNextStage(expee.getAccessCode());

        Assert.assertEquals("code", expee.getResult(2).getStage().getType());
        Assert.assertEquals(numOfCodeRes+1, db.getNumerOfCodeResults());
    }

    private void nextStageFor(int i, UUID code) throws NotExistException, CodeException, ExpEndException {
        for (int j = 0; j < i; j++) experimenteeBusiness.getNextStage(code);
    }
}
