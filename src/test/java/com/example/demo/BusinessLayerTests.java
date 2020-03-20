package com.example.demo;

import com.example.demo.BusinessLayer.*;
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

    private ICreatorBusiness creatorBusiness= new CreatorBusiness();
    private IGraderBusiness graderBusiness=new GraderBusiness();
    private IExperimenteeBusiness experimenteeBusiness = new ExperimenteeBusiness();
    private DataCache cache=  DataCache.getInstance();

    private ManagementUser manager =new ManagementUser("smorad","sm_pass","smorad@post.bgu.ac.il");;
    private Experimentee expee = new Experimentee("giliCode","gili@post.bgu.ac.il");
    private Grader grader = new Grader();

    @BeforeEach
    private void init(){
        cache.flash();
        cache.addManager(manager);

    }

    @Test // manager login
    public void researcherLoginTest() {
        Assert.assertFalse(creatorBusiness.researcherLogin(manager.getBguUsername(),"not the password"));
        Assert.assertTrue(creatorBusiness.researcherLogin(manager.getBguUsername(),manager.getBguPassword()));
    }

    @Test
    public void addAllExperiment(){
        String expName = "testExp";
        List<JSONObject> stages =new ArrayList<>();
        

    }


}
