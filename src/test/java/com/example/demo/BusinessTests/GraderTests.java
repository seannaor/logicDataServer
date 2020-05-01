package com.example.demo.BusinessTests;

import com.example.demo.BusinessLayer.*;
import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import com.example.demo.BusinessLayer.Exceptions.ExistException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import com.example.demo.Utils;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.constraints.AssertFalse;
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
        experiment.setExperimentId(1);

        grader = new Grader("grader@post.bgu.ac.il",experiment);
        cache.addGrader(grader);

        //fuck this grading task, TODO: build a real one
        GradingTask gt= new GradingTask("test", experiment,experiment,experiment);
        cache.addGraderToGradingTask(gt,grader,"accessCode");
    }

    @Test
    public void loginTest(){
        Assert.assertFalse(graderBusiness.beginGrading("not exist"));

        Assert.assertTrue(graderBusiness.beginGrading("accessCode"));
    }

}
