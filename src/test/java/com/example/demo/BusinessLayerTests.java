package com.example.demo;

import com.example.demo.BusinessLayer.*;
import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import net.minidev.json.JSONObject;
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
    private Grader grader = new Grader();

    @BeforeEach
    private void init() {
        cache.flash();
        manager = new ManagementUser("smorad", "sm_pass", "smorad@post.bgu.ac.il");
        expee = new Experimentee("giliCode", "gili@post.bgu.ac.il");
        cache.addManager(manager);
        cache.addExperimentee(expee);
        List<JSONObject> stages = Utils.buildStages();
        creatorBusiness.addExperiment(manager.getBguUsername(), "The Experiment", stages);
        experiment =manager.getExperimentByName("The Experiment");
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
        Assert.assertTrue(creatorBusiness.addExperiment(manager.getBguUsername(), expName, stages));
        Assert.assertEquals(manager.getExperiments().size(), expNum+1);
        Experiment exp = (Experiment) manager.getExperimentByName(expName);
        Assert.assertTrue(exp.getManagementUsers().contains(manager));

        //same name should fail
        Assert.assertFalse(creatorBusiness.addExperiment(manager.getBguUsername(), expName, new ArrayList<>()));
        //creator name not exist should fail
        Assert.assertFalse(creatorBusiness.addExperiment("not exist name", expName, new ArrayList<>()));

    }

    @Test
    public void createExperiment() {
        String expName = "testExp";

        //new experiment should pass
        int expNum = manager.getExperiments().size();
        Assert.assertTrue(creatorBusiness.createExperiment(manager.getBguUsername(), expName));
        Assert.assertEquals(manager.getExperiments().size(), expNum+1);
        Experiment exp = (Experiment) manager.getExperimentByName(expName);
        Assert.assertTrue(exp.getManagementUsers().contains(manager));
        Assert.assertEquals(exp.getStages().size(), 0);

        //same name should fail
        Assert.assertFalse(creatorBusiness.createExperiment(manager.getBguUsername(), expName));
        //creator name not exist should fail
        Assert.assertFalse(creatorBusiness.createExperiment("not exist name", expName));
    }

    @Test
    public void addStage() {
        String expName = "testExp";
        creatorBusiness.createExperiment(manager.getBguUsername(), expName);
        Experiment exp = (Experiment) manager.getExperimentByName(expName);
        exp.setExperimentId(666);
        List<JSONObject> stages = Utils.buildStages();

        // adding legal stages should pass
        int i = 1;
        for (JSONObject jStage : stages) {
            Assert.assertTrue(creatorBusiness.addStageToExperiment(manager.getBguUsername(), exp.getExperimentId(), jStage));
            Assert.assertEquals(exp.getStages().size(), i);
            i++;
        }

        //creator name not exist should fail
        Assert.assertFalse(creatorBusiness.addStageToExperiment("not exist name", exp.getExperimentId(), stages.get(0)));

        //experiment id not exist
        Assert.assertFalse(creatorBusiness.addStageToExperiment(manager.getBguUsername(), -1, stages.get(0)));

        //not a valid stage
        Assert.assertFalse(creatorBusiness.addStageToExperiment(manager.getBguUsername(), exp.getExperimentId(), new JSONObject()));

    }

    @Test
    public void addGradingTask(){

        //not exist manager
        Assert.assertFalse(creatorBusiness.addGradingTask("not exist",experiment.getExperimentId(),"grading task",new ArrayList<>(),new ArrayList<>(),new ArrayList<>()));
        //not exist experiment
        Assert.assertFalse(creatorBusiness.addGradingTask(manager.getBguUsername(),-1,"grading task",new ArrayList<>(),new ArrayList<>(),new ArrayList<>()));
        //not legal personal & result experiments
        List<JSONObject> stagesIlegal = new ArrayList<>();
        stagesIlegal.add(new JSONObject());
        Assert.assertFalse(creatorBusiness.addGradingTask(manager.getBguUsername(),experiment.getExperimentId(),"grading task",stagesIlegal,new ArrayList<>(),stagesIlegal));
        //illegal indexes to check
        //Assert.assertFalse(creatorBusiness.addGradingTask(manager.getBguUsername(),experiment.getExperimentId(),"grading task",new ArrayList<>(),List.of(-1,-2,-3),new ArrayList<>()));

        Assert.assertTrue(creatorBusiness.addGradingTask(manager.getBguUsername(),experiment.getExperimentId(),"grading task",new ArrayList<>(),List.of(2),new ArrayList<>()));
    }


}
