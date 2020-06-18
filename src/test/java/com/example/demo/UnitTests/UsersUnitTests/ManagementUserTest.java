package com.example.demo.UnitTests.UsersUnitTests;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ManagementUserTest {
    private ManagementUser man;
    private Experiment exp;

    @BeforeEach
    private void init() throws FormatException {
        man = new ManagementUser("sean", "seanaaaa", "seaa@1!.asd");
        exp = new Experiment("Experiment Name", man);
    }

    @Test
    public void getExperimentTest() throws NotExistException {
        try {
            man.getExperiment(666);
            Assert.fail();
        } catch (NotExistException ignored) {}

        Assert.assertEquals(man.getExperiment(exp.getExperimentId()), exp);
    }

    @Test
    public void getExperimentByNameTest() throws NotExistException {
        try {
            man.getExperimentByName("666");
            Assert.fail();
        } catch (NotExistException ignored) {}

        Assert.assertEquals(man.getExperimentByName(exp.getExperimentName()), exp);
    }

    @Test
    public void removeManagementUserToExperimentByIdTest() {
        Assert.assertEquals(1,man.getManagementUserToExperiments().size());
        man.removeManagementUserToExperimentById(exp);
        Assert.assertEquals(0,man.getManagementUserToExperiments().size());
    }

    @Test
    public void getExperimentsTest() {
        Assert.assertEquals(1,man.getExperimentes().size());
    }
}
