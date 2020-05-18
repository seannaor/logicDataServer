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

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

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
    private void init() throws NotExistException, FormatException, ExistException {
        cache.setCache();
        db.deleteData();
        manager = new ManagementUser("smorad", "sm_pass", "smorad@post.bgu.ac.il");
        cache.addManager(manager);
        List<JSONObject> stages = Utils.buildStages();
        creatorBusiness.addExperiment(manager.getBguUsername(), "The Experiment", stages);
        experiment = manager.getExperimentByName("The Experiment");
        expee = new Experimentee("gili@post.bgu.ac.il", experiment);
        cache.addExperimentee(expee);
    }

    @Test
    public void loginTest() {
        //not exist code should fail
        UUID someCode = UUID.randomUUID();
        try {
            Stage first = experimenteeBusiness.beginParticipation(someCode);
        } catch (CodeException ignore) {
            Assert.assertTrue(db.getExperimenteeByCode(someCode) == null);
        }
        catch (ExpEndException e){Assert.fail();}

        // real code should get us the first stage - info
        try {
            Stage first = experimenteeBusiness.beginParticipation(expee.getAccessCode());
            Assert.assertEquals(first.getType(), "info");
            Assert.assertTrue(db.getExperimenteeByCode(expee.getAccessCode()) != null);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    @Transactional
    public void currNextStageTest() {
        //not exist code should fail
        UUID someCode = UUID.randomUUID();
        try {
            experimenteeBusiness.getCurrentStage(someCode);
            Assert.fail();
        } catch (CodeException ignore) {
            Assert.assertTrue(db.getExperimenteeByCode(someCode) == null);
        } catch (ExpEndException e) {
            Assert.fail();
        }

        //not exist code should fail
        try {
            experimenteeBusiness.getNextStage(someCode);
            Assert.fail();
        } catch (CodeException ignore) {
            Assert.assertTrue(db.getExperimenteeByCode(someCode) == null);
        } catch (Exception e) {
            Assert.fail();
        }

        // real code should get us the first stage - info
        try {
            Stage first = experimenteeBusiness.getCurrentStage(expee.getAccessCode());
            Assert.assertEquals(first.getType(), "info");
            Assert.assertEquals(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().getCurrStage().getType(), "info");
        } catch (Exception e) {
            Assert.fail();
        }

        // real code should get us the next stages
        try {
            Stage s = experimenteeBusiness.getNextStage(expee.getAccessCode());
            Assert.assertEquals(s.getType(), "questionnaire");
            Assert.assertEquals(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().getCurrStage().getType(), "questionnaire");

            s = experimenteeBusiness.getNextStage(expee.getAccessCode());
            Assert.assertEquals(s.getType(), "code");
            Assert.assertEquals(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().getCurrStage().getType(), "code");

            s = experimenteeBusiness.getNextStage(expee.getAccessCode());
            Assert.assertEquals(s.getType(), "tagging");
            Assert.assertEquals(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().getCurrStage().getType(), "tagging");
        } catch (Exception e) {
            Assert.fail();
        }

        // end of exp - curr and next should fail
        try {
            experimenteeBusiness.getNextStage(expee.getAccessCode());
            Assert.fail();
        } catch (ExpEndException ignore) {
            Assert.assertTrue(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().isDone());
        } catch (Exception e) {
            Assert.fail();
        }
        try {
            experimenteeBusiness.getCurrentStage(expee.getAccessCode());
            Assert.fail();
        } catch (ExpEndException ignore) {
            Assert.assertTrue(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().isDone());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    @Transactional
    public void fillStage() {

        //not exist code should fail
        UUID someCode = UUID.randomUUID();
        try {
            experimenteeBusiness.fillInStage(someCode, new JSONObject());
            Assert.fail();
        } catch (CodeException ignore) {
            Assert.assertTrue(db.getExperimenteeByCode(someCode) == null);
        } catch (Exception e) {
            Assert.fail();
        }

        // pass info (first) stage
        try {
            experimenteeBusiness.getNextStage(expee.getAccessCode());
        } catch (Exception e) {
            Assert.fail();
        }

        // fill in questions (second) stage, fucked format should fail
        long numOfAnswers = db.getNumerOfAnswers();
        try {
            JSONObject ans = new JSONObject();
            ans.put("stageType","questionnaire");
            JSONObject ans1 = new JSONObject();
            ans1.put("answer", 2);
            ans.put(2, ans1);
            experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
            Assert.fail();
        } catch (FormatException ignore) {
            Assert.assertEquals(db.getNumerOfAnswers(), numOfAnswers);
        } catch (Exception e) {
            Assert.fail();
        }

        // fill in questions (second) stage, good format should pass
        fillInQuestionnaire(expee);
        Assert.assertEquals(db.getNumerOfAnswers(), numOfAnswers + 2);

        // fill in code (third) stage, fucked format should fail
        long numOfCodeRes = db.getNumerOfCodeResults();
        try {
            JSONObject ans = new JSONObject();
            ans.put("stageType","code");
            experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
            Assert.fail();
        } catch (FormatException ignore) {
            Assert.assertEquals(db.getNumerOfCodeResults(), numOfCodeRes);
        } catch (Exception e) {
            Assert.fail();
        }

        // fill in code (third) stage, good format should pass
        fillInCode(expee);
        Assert.assertEquals(db.getNumerOfCodeResults(), numOfCodeRes + 1);

        // fill in tagging (last) stage, fucked format should fail
        long numOfTagRes = db.getNumberOfTagResults();
        try {
            JSONObject ans = new JSONObject();
            ans.put("stageType","tagging");
            experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
            Assert.fail();
        } catch (FormatException ignore) {
            Assert.assertEquals(numOfTagRes,db.getNumberOfTagResults());
        } catch (Exception e) {
            Assert.fail();
        }

        // fill in code (third) stage, good format should pass
        fillInTagging(expee);
        Assert.assertEquals(db.getNumberOfTagResults(), numOfTagRes + 3);

        // end of experiment
        try{
            experimenteeBusiness.getNextStage(expee.getAccessCode());
            Assert.fail();
        }catch (ExpEndException ignore){
            Assert.assertTrue(db.getExperimenteeByCode(expee.getAccessCode()).getParticipant().isDone());
        }
        catch (Exception e){
            Assert.fail();
        }
    }

    private void fillInTagging(Experimentee expee) {
        try {
            JSONObject ans = new JSONObject();
            ans.put("stageType","tagging");

            JSONObject tag1 = new JSONObject();
            tag1.put("start_loc",0);
            tag1.put("length",10);
            ans.put(0,tag1);

            JSONObject tag2 = new JSONObject();
            tag2.put("start_loc",0);
            tag2.put("length",10);
            ans.put(1,tag2);

            JSONObject tag3 = new JSONObject();
            tag3.put("start_loc",0);
            tag3.put("length",10);
            ans.put(2,tag3);
            experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
//            experimenteeBusiness.getNextStage(expee.getAccessCode());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
    }

    void fillInQuestionnaire(Experimentee expee){
        try {
            JSONObject ans = new JSONObject();
            ans.put("stageType","questionnaire");
            JSONObject ans1 = new JSONObject();
            ans1.put("answer", "WTF!!!");
            ans.put("1", ans1);
            JSONObject ans2 = new JSONObject();
            ans2.put("answer", 3);
            ans.put("2", ans2);
            experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
            experimenteeBusiness.getNextStage(expee.getAccessCode());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
    }

    private void fillInCode(Experimentee expee){
        try {
            JSONObject ans = new JSONObject();
            ans.put("stageType","code");
            ans.put("userCode","return -1");
            experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
            experimenteeBusiness.getNextStage(expee.getAccessCode());
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
