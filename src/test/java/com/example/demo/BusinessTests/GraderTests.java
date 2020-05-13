package com.example.demo.BusinessTests;

import com.example.demo.BusinessLayer.*;
import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Exceptions.*;
import com.example.demo.DBAccess;
import com.example.demo.Utils;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.UUID;

@SpringBootTest
public class GraderTests {
    @Autowired
    private GraderBusiness graderBusiness;
    @Autowired
    private CreatorBusiness creatorBusiness;
    @Autowired
    private DataCache cache;
    @Autowired
    private DBAccess db;

    private Grader grader;
    private ManagementUser manager;
    private Experiment experiment;
    private UUID graderCode;

//    public GraderTests() {
//        creatorBusiness = new CreatorBusiness();
//        graderBusiness = new GraderBusiness();
//        cache = DataCache.getInstance();
//    }

    @BeforeEach
    private void init() throws NotExistException, FormatException, ExistException {
        cache.setCache();
        db.deleteData();
        manager = new ManagementUser("smorad", "sm_pass", "smorad@post.bgu.ac.il");
        cache.addManager(manager);
        List<JSONObject> stages = Utils.buildStages();
        creatorBusiness.addExperiment(manager.getBguUsername(), "The Experiment", stages);
        experiment = manager.getExperimentByName("The Experiment");

        grader = new Grader("grader@post.bgu.ac.il",experiment);
        cache.addGrader(grader);

        //fuck this grading task, TODO: build a real one
        GradingTask gt= new GradingTask("test", experiment,experiment,experiment);
        cache.addGradingTask(gt);
        cache.addGraderToGradingTask(gt,grader);
        graderCode = cache.getGraderToGradingTask(grader, gt).getGraderAccessCode();
    }

    @Test
    public void loginTest(){
        Assert.assertFalse(graderBusiness.beginGrading(UUID.randomUUID()));
        Assert.assertTrue(graderBusiness.beginGrading(graderCode));
    }

    @Test
    public void getParticipants(){
        //Assert.fail();
        //TODO: implement
    }

    @Test
    public void getParticipantResults(){
        //Assert.fail();
        //TODO: implement
    }
}
