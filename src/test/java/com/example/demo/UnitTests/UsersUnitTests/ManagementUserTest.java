package com.example.demo.UnitTests.UsersUnitTests;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import com.example.demo.BusinessLayer.Entities.ManagementUserToExperiment;
import com.example.demo.BusinessLayer.Entities.Permission;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertThrows(NotExistException.class,()->{
            man.getExperiment(666);
        });

        Assert.assertEquals(man.getExperiment(exp.getExperimentId()), exp);
    }

    @Test
    public void getExperimentByNameTest() throws NotExistException {
        assertThrows(NotExistException.class,()->{
            man.getExperimentByName("666");
        });

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

    @Test
    public void settersGettersTest(){
        String str = "manager";
        man.setBguUsername(str);
        Assert.assertEquals(str,man.getBguUsername());

        str = "manager@mail";
        man.setUserEmail(str);
        Assert.assertEquals(str,man.getUserEmail());

        str = "123456";
        man.setBguPassword(str);
        Assert.assertEquals(str,man.getBguPassword());
    }

    @Test
    public void permissionsTest(){
        int permissionsCount = man.getPermissions().size();

        man.addPermission(new Permission("PERMISSION"));

        Assert.assertEquals(permissionsCount+1,man.getPermissions().size());
    }

    @Test
    public void removeManager(){
        int expsCount = man.getManagementUserToExperiments().size();
        ManagementUserToExperiment manager2Exp = man.getManagementUserToExperiments().get(0);
        man.removeManagementUserToExperiment(manager2Exp);

        Assert.assertEquals(expsCount-1,man.getManagementUserToExperiments().size());

        man.removeManagementUserToExperiment(manager2Exp);
        Assert.assertEquals(expsCount-1,man.getManagementUserToExperiments().size());
    }

    @Test
    public void addManager(){
        int expsCount = man.getManagementUserToExperiments().size();
        ManagementUserToExperiment manager2Exp = man.getManagementUserToExperiments().get(0);
        man.addManagementUserToExperiment(manager2Exp);

        Assert.assertEquals(expsCount,man.getManagementUserToExperiments().size());
    }
}
