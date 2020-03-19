package com.example.demo;

import com.example.demo.BusinessLayer.*;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.example.demo.BusinessLayer.DataCache.*;

@SpringBootTest
public class BusinessLayerTests {

    private ICreatorBusiness creatorBusiness= new CreatorBusiness();
    private IGraderBusiness graderBusiness=new GraderBusiness();
    private IExperimenteeBusiness experimenteeBusiness = new ExperimenteeBusiness();
    private DataCache cache=  DataCache.getInstance();

    private ManagementUser manager =new ManagementUser("smorad","sm_pass","smorad@post.bgu.ac.il");;
    private Experimentee expee ;//= new Experimentee();
    private Grader grader;

    @BeforeEach
    private void init(){
        cache.flash();
        cache.addManager(manager);
    }

    @Test // manager login
    public void test() {
        Assert.assertFalse(creatorBusiness.researcherLogin(manager.getBguUsername(),"not the password"));
        Assert.assertTrue(creatorBusiness.researcherLogin(manager.getBguUsername(),manager.getBguPassword()));
    }
}
