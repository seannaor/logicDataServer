package com.example.demo.UnitTests;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import com.example.demo.BusinessLayer.Entities.ManagementUserToExperiment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ManagementUserToExperimentUnitTest {
    private ManagementUserToExperiment manager2experiment;

    @BeforeEach
    public void init(){
        ManagementUser manager = new ManagementUser("manager","password","mail");
        Experiment experiment = new Experiment();
        experiment.setExperimentId(0);
        manager2experiment = new ManagementUserToExperiment(manager,experiment,"CREATOR");
    }

    @Test
    public void setGetManagerTest(){
        ManagementUser manager = new ManagementUser("new","new","new");
        manager2experiment.setManagementUser(manager);
        assertEquals(manager,manager2experiment.getManagementUser());
    }

    @Test
    public void setGetExperimentTest(){
        Experiment experiment = new Experiment();
        experiment.setExperimentId(10);
        manager2experiment.setExperiment(experiment);
        assertEquals(experiment,manager2experiment.getExperiment());
    }

    @Test
    public void setGetRoleTest(){
     String role = "GUEST";
        manager2experiment.setRole(role);
        assertEquals(role,manager2experiment.getRole());
    }

}
