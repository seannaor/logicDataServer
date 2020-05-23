package com.example.demo.BusinessTests;

import com.example.demo.BusinessLayer.*;
import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.*;
import com.example.demo.DBAccess;
import com.example.demo.Utils;
import org.json.simple.JSONObject;
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

        experiment = Utils.buildExp(creatorBusiness,manager);

        String code = creatorBusiness.addExperimentee(manager.getBguUsername(),experiment.getExperimentId(),"gili@post.bgu.ac.il");
        expee = cache.getExpeeByCode(UUID.fromString(code));
    }

    @Test
    public void loginFailTest(){
        //not exist code should fail
        UUID someCode = UUID.randomUUID();
        try {
            experimenteeBusiness.beginParticipation(someCode);
            Assert.fail();
        }
        catch (CodeException ignore) {}
        catch (ExpEndException e){Assert.fail();}
    }

    @Test
    public void loginTest() {
        // real code should get us the first stage - info
        try {
            Stage first = experimenteeBusiness.beginParticipation(expee.getAccessCode());
            Assert.assertEquals(first.getType(), "info");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
//    @Transactional
    public void currStageFailTest(){
        //not exist code should fail
        UUID someCode = UUID.randomUUID();
        try {
            experimenteeBusiness.getCurrentStage(someCode);
            Assert.fail();
        } catch (CodeException ignore) {
//            Assert.assertNull(db.getExperimenteeByCode(someCode));
        } catch (ExpEndException e) {
            Assert.fail();
        }
    }

    @Test
//    @Transactional
    public void nextStageFailTest(){
        //not exist code should fail
        UUID someCode = UUID.randomUUID();
        try {
            experimenteeBusiness.getNextStage(someCode);
            Assert.fail();
        } catch (CodeException ignore) {
//            Assert.assertNull(db.getExperimenteeByCode(someCode));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
//    @Transactional
    public void currStageTest() {
        // real code should get us the first stage - info
        try {
            Stage first = experimenteeBusiness.getCurrentStage(expee.getAccessCode());
            Assert.assertEquals(first.getType(), "info");
//            Assert.assertEquals(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().getCurrStage().getType(), "info");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
//    @Transactional
    public void currNextStageTest() {
        // real code should get us the next stages
        try {
            Stage s = experimenteeBusiness.getNextStage(expee.getAccessCode());
            Assert.assertEquals(s.getType(), "questionnaire");
//            Assert.assertEquals(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().getCurrStage().getType(), "questionnaire");

            s = experimenteeBusiness.getNextStage(expee.getAccessCode());
            Assert.assertEquals(s.getType(), "code");
//            Assert.assertEquals(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().getCurrStage().getType(), "code");

            s = experimenteeBusiness.getNextStage(expee.getAccessCode());
            Assert.assertEquals(s.getType(), "tagging");
//            Assert.assertEquals(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().getCurrStage().getType(), "tagging");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void endExperimentTest() throws CodeException, ExpEndException {
        nextStageFor(3,expee.getAccessCode());

        // end of exp - next should fail
        try {
            experimenteeBusiness.getNextStage(expee.getAccessCode());
            Assert.fail();
        } catch (ExpEndException ignore) {
//            Assert.assertTrue(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().isDone());
        } catch (Exception e) {
            Assert.fail();
        }

        // current stage should fail because next was activated first
        try {
            experimenteeBusiness.getCurrentStage(expee.getAccessCode());
            Assert.fail();
        } catch (ExpEndException ignore) {
//            Assert.assertTrue(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().isDone());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void fillStageFail() throws NotInReachException {
        //not exist code should fail
        UUID someCode = UUID.randomUUID();
        try {
            experimenteeBusiness.fillInStage(someCode, new JSONObject());
            Assert.fail();
        } catch (CodeException ignore) {
//            Assert.assertTrue(db.getExperimenteeByCode(someCode) == null);
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertNull(expee.getResult(0));
    }

    @Test
    public void fillQuestionsFail() throws CodeException, ExpEndException, NotInReachException {
        experimenteeBusiness.getNextStage(expee.getAccessCode());

        // fill in questions (second) stage, fucked format should fail
//        long numOfAnswers = db.getNumerOfAnswers();
        try {
            JSONObject ans = new JSONObject();
            ans.put("stageType","questionnaire");
            JSONObject ans1 = new JSONObject();
            ans1.put("answer", 2);
            ans.put(2, ans1);
            experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
            Assert.fail();
        } catch (FormatException ignore) {
//            Assert.assertEquals(db.getNumerOfAnswers(), numOfAnswers);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertNull(expee.getResult(1));
    }

    @Test
    public void fillCodeFail() throws CodeException, ExpEndException, NotInReachException {
        nextStageFor(2,expee.getAccessCode());

        // fill in code (third) stage, fucked format should fail
//        long numOfCodeRes = db.getNumerOfCodeResults();
        try {
            JSONObject ans = new JSONObject();
            ans.put("stageType","code");
            experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
            Assert.fail();
        } catch (FormatException ignore) {
//            Assert.assertEquals(db.getNumerOfCodeResults(), numOfCodeRes);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertNull(expee.getResult(2));
    }

    @Test
    public void fillTaggingFail() throws CodeException, ExpEndException, NotInReachException {
        nextStageFor(3,expee.getAccessCode());
        // fill in tagging (last) stage, fucked format should fail
//        long numOfTagRes = db.getNumberOfTagResults();
        try {
            JSONObject ans = new JSONObject();
            ans.put("stageType","tagging");
            experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
            Assert.fail();
        } catch (FormatException ignore) {
//            Assert.assertEquals(numOfTagRes,db.getNumberOfTagResults());
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertNull(expee.getResult(3));
    }

    @Test
    public void fillInTagging() throws CodeException, ExpEndException, NotInReachException {
        nextStageFor(3,expee.getAccessCode());
        try {
            //fill tagging dont really answer this stage, sill got weird problem there
            Utils.fillInTagging(experimenteeBusiness,expee.getAccessCode());
//            experimenteeBusiness.getNextStage(expee.getAccessCode());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
        Assert.assertTrue(expee.getResult(3).getAsJson().get("source stage").equals("tagging"));
    }

    @Test
    public void fillInQuestionnaire() throws CodeException, ExpEndException, NotInReachException {
        experimenteeBusiness.getNextStage(expee.getAccessCode());
        try {
            Utils.fillInQuestionnaire(experimenteeBusiness,expee.getAccessCode());
            experimenteeBusiness.getNextStage(expee.getAccessCode());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
        Assert.assertTrue(expee.getResult(1).getAsJson().get("source stage").equals("questionnaire"));
    }

    @Test
    public void fillInCode() throws CodeException, ExpEndException, NotInReachException {
        nextStageFor(2,expee.getAccessCode());
        try {
            Utils.fillInCode(experimenteeBusiness,expee.getAccessCode());
            experimenteeBusiness.getNextStage(expee.getAccessCode());
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertTrue(expee.getResult(2).getAsJson().get("source stage").equals("code"));
    }

    private void nextStageFor(int i,UUID code) throws CodeException, ExpEndException {
        for(int j=0;j<i;j++) experimenteeBusiness.getNextStage(code);
    }
}
