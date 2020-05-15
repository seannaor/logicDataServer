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
    @Autowired
    private ExperimenteeBusiness experimenteeBusiness;
    @Autowired
    private CreatorBusiness creatorBusiness;
    @Autowired
    private DataCache cache;
    @Autowired
    private DBAccess db;

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
            ans.put(2,2);
            experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
            Assert.fail();
        } catch (FormatException ignore) {
            Assert.assertEquals(db.getNumerOfAnswers(), numOfAnswers);
        } catch (Exception e) {
            Assert.fail();
        }

        // fill in questions (second) stage, good format should pass
        try {
            JSONObject ans = new JSONObject();
            ans.put("stageType","questionnaire");
            ans.put(1,"WTF!!!");
            ans.put(2,"3");
            experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
            Assert.assertEquals(db.getNumerOfAnswers(), numOfAnswers + 2);
            experimenteeBusiness.getNextStage(expee.getAccessCode());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }

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
        try {
            JSONObject ans = new JSONObject();
            ans.put("stageType","code");
            ans.put("userCode","return -1");
            experimenteeBusiness.fillInStage(expee.getAccessCode(), ans);
            Assert.assertEquals(db.getNumerOfCodeResults(), numOfCodeRes + 1);
        } catch (Exception e) {
            Assert.fail();
        }

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
}
