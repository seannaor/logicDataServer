package com.example.demo;

import com.example.demo.BusinessLayer.*;
import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import com.example.demo.BusinessLayer.Exceptions.ExistException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import org.aspectj.weaver.ast.Not;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.BusinessLayer.DataCache.*;

@SpringBootTest
public class BusinessLayerTests {

    private ICreatorBusiness creatorBusiness = new CreatorBusiness();
    private IGraderBusiness graderBusiness = new GraderBusiness();
    private IExperimenteeBusiness experimenteeBusiness = new ExperimenteeBusiness();
    private DataCache cache = DataCache.getInstance();

    private ManagementUser manager;
    private Experiment experiment;
    private Experimentee expee;
    //private Grader grader = new Grader();

    @BeforeEach
    private void init() throws NotExistException, FormatException, ExistException {
        cache.flash();
        manager = new ManagementUser("smorad", "sm_pass", "smorad@post.bgu.ac.il");
        expee = new Experimentee("giliCode", "gili@post.bgu.ac.il");
        cache.addManager(manager);
        cache.addExperimentee(expee);
        List<JSONObject> stages = Utils.buildStages();
        creatorBusiness.addExperiment(manager.getBguUsername(), "The Experiment", stages);
        experiment = manager.getExperimentByName("The Experiment");
        experiment.setExperimentId(1);
    }

    @Test // manager login
    public void researcherLoginTest() {
        Assert.assertFalse(creatorBusiness.researcherLogin(manager.getBguUsername(), "not the password"));
        Assert.assertTrue(creatorBusiness.researcherLogin(manager.getBguUsername(), manager.getBguPassword()));
    }

    @Test
    public void addAllExperiment() {
        String expName = "testExp";
        List<JSONObject> stages = Utils.buildStages();

        //new experiment should pass
        int expNum = manager.getExperiments().size();
        try {
            creatorBusiness.addExperiment(manager.getBguUsername(), expName, stages);
            Assert.assertEquals(manager.getExperiments().size(), expNum + 1);
            Experiment exp = manager.getExperimentByName(expName);
            Assert.assertTrue(exp.getManagementUsers().contains(manager));
        } catch (Exception e) {
            Assert.fail();
        }

        try {
            //same name should fail
            creatorBusiness.addExperiment(manager.getBguUsername(), expName, new ArrayList<>());
            Assert.fail();
        } catch (FormatException | NotExistException e) {
            Assert.fail();
        } catch (ExistException e) {
        }

        try {
            //creator name not exist should fail
            creatorBusiness.addExperiment("not exist name", expName, new ArrayList<>());
            Assert.fail();
        } catch (FormatException | ExistException e) {
            Assert.fail();
        } catch (NotExistException e) {
        }
    }

    @Test
    public void createExperiment() {
        String expName = "testExp";

        //new experiment should pass
        int expNum = manager.getExperiments().size();
        try {
            creatorBusiness.createExperiment(manager.getBguUsername(), expName);
            Assert.assertEquals(manager.getExperiments().size(), expNum + 1);
            Experiment exp = manager.getExperimentByName(expName);
            Assert.assertTrue(exp.getManagementUsers().contains(manager));
            Assert.assertEquals(exp.getStages().size(), 0);
        } catch (Exception e) {
            Assert.fail();
        }

        try {
            //same name should fail
            creatorBusiness.createExperiment(manager.getBguUsername(), expName);
            Assert.fail();
        } catch (ExistException ignore) {
        } catch (NotExistException e) {
            Assert.fail();
        }

        try {
            //creator name not exist should fail
            creatorBusiness.createExperiment("not exist name", expName);
            Assert.fail();
        } catch (NotExistException e) {
        } catch (ExistException e) {
            Assert.fail();
        }
    }

    @Test
    public void addStage() {
        String expName = "testExp";
        Experiment exp = new Experiment();
        List<JSONObject> stages = Utils.buildStages();
        try {
            creatorBusiness.createExperiment(manager.getBguUsername(), expName);
            exp = manager.getExperimentByName(expName);
            exp.setExperimentId(666);

            // adding legal stages should pass
            int i = 1;
            for (JSONObject jStage : stages) {
                creatorBusiness.addStageToExperiment(manager.getBguUsername(), exp.getExperimentId(), jStage);
                Assert.assertEquals(exp.getStages().size(), i);
                i++;
            }
        } catch (Exception e) {
            Assert.fail();
        }

        //VVVVVVVV should fails VVVVVVVV

        try {
            //creator name not exist
            creatorBusiness.addStageToExperiment("not exist name", exp.getExperimentId(), stages.get(0));
            Assert.fail();
        } catch (FormatException | ExistException e) {
            Assert.fail();
        } catch (NotExistException ignore) {
        }


        try {
            //experiment id not exist
            creatorBusiness.addStageToExperiment(manager.getBguUsername(), -1, stages.get(0));
            Assert.fail();
        } catch (FormatException | ExistException e) {
            Assert.fail();
        } catch (NotExistException ignore) {
        }


        try {
            //not a valid stage
            creatorBusiness.addStageToExperiment(manager.getBguUsername(), exp.getExperimentId(), new JSONObject());
            Assert.fail();
        } catch (NotExistException | ExistException e) {
            Assert.fail();
        } catch (FormatException ignore) {
        }
    }

    @Test
    public void addGradingTask() {
        try {
            //not exist manager
            creatorBusiness.addGradingTask("not exist", experiment.getExperimentId(), "grading task", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            Assert.fail();
        } catch (NotExistException ignored) {
        } catch (FormatException e) {
            Assert.fail();
        }

        try {
            //not exist experiment
            creatorBusiness.addGradingTask(manager.getBguUsername(), -1, "grading task", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            Assert.fail();
        } catch (NotExistException ignored) {
        } catch (FormatException e) {
            Assert.fail();
        }


        try {
            //not legal personal & result experiments
            List<JSONObject> stagesIllegal = List.of(new JSONObject());
            creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "grading task", stagesIllegal, new ArrayList<>(), stagesIllegal);
            Assert.fail();
        } catch (FormatException ignored) {
        } catch (NotExistException e) {
            Assert.fail();
        }


        //illegal indexes to check
        //Assert.assertFalse(creatorBusiness.addGradingTask(manager.getBguUsername(),experiment.getExperimentId(),"grading task",new ArrayList<>(),List.of(-1,-2,-3),new ArrayList<>()));

        try {
            creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "grading task", new ArrayList<>(), List.of(2), new ArrayList<>());
        } catch (Exception fail) {
            Assert.fail();
        }
    }
}