package com.example.demo.BusinessTests;

import com.example.demo.BusinessLayer.*;
import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Exceptions.*;
import com.example.demo.Utils;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

@SpringBootTest
public class GraderTests {

    private IGraderBusiness graderBusiness;
    private ICreatorBusiness creatorBusiness;
    private DataCache cache;

    private Grader grader;
    private ManagementUser manager;
    private Experiment experiment;

    public GraderTests() {
        creatorBusiness = new CreatorBusiness();
        graderBusiness = new GraderBusiness();
        cache = DataCache.getInstance();
    }

    @BeforeEach
    private void init() throws NotExistException, FormatException, ExistException {
        cache.flash();
        manager = new ManagementUser("smorad", "sm_pass", "smorad@post.bgu.ac.il");
        cache.addManager(manager);
        List<JSONObject> stages = Utils.buildStages();
        creatorBusiness.addExperiment(manager.getBguUsername(), "The Experiment", stages);
        experiment = manager.getExperimentByName("The Experiment");

        grader = new Grader("grader@post.bgu.ac.il",experiment);
        cache.addGrader(grader);

        //fuck this grading task, TODO: build a real one
        GradingTask gt= new GradingTask(experiment,experiment,experiment);
        cache.addGraderToGradingTask(gt,grader,"accessCode");
    }

    @Test
    public void loginTest(){
        Assert.assertFalse(graderBusiness.beginGrading("not exist"));

        Assert.assertTrue(graderBusiness.beginGrading("accessCode"));
    }

    @Test
    public void getParticipants(){
        Assert.fail();
        //TODO: implement
    }

    @Test
    public void getParticipantResults(){
        Assert.fail();
        //TODO: implement
    }
}
