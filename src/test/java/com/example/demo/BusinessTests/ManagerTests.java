package com.example.demo.BusinessTests;

import com.example.demo.BusinessLayer.*;
import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Exceptions.*;
import com.example.demo.DBAccess;
import com.example.demo.Utils;
import org.aspectj.apache.bcel.classfile.Code;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ManagerTests {
    @Autowired
    private CreatorBusiness creatorBusiness;
    @Autowired
    private DataCache cache;
    @Autowired
    private DBAccess db;

    private ManagementUser manager;
    private Experiment experiment;
    private Experimentee expee;
    private int gradingTaskId;
    private GradingTask gt;

//    public ManagerTests() {
//        creatorBusiness = new CreatorBusiness();
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
        expee = new Experimentee( "123", "gili@post.bgu.ac.il",experiment);
        cache.addExperimentee(expee);
        gradingTaskId = creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "The Grading Task", new ArrayList<>(), List.of(2), new ArrayList<>());
        gt = cache.getGradingTaskById(manager.getBguUsername(), experiment.getExperimentId(), gradingTaskId);
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
        int expNum = manager.getManagementUserToExperiments().size();
        try {
            creatorBusiness.addExperiment(manager.getBguUsername(), expName, stages);
            Assert.assertEquals(manager.getManagementUserToExperiments().size(), expNum + 1);
            Experiment exp = manager.getExperimentByName(expName);
            Assert.assertTrue(exp.containsManger(manager));
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
        int expNum = manager.getManagementUserToExperiments().size();
        try {
            creatorBusiness.createExperiment(manager.getBguUsername(), expName);
            Assert.assertEquals(manager.getManagementUserToExperiments().size(), expNum + 1);
            Experiment exp = manager.getExperimentByName(expName);
            Assert.assertTrue(exp.containsManger(manager));
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
        } catch (FormatException f) {
            Assert.fail();
        } catch (NotExistException ignore) {
        }


        try {
            //experiment id not exist
            creatorBusiness.addStageToExperiment(manager.getBguUsername(), -1, stages.get(0));
            Assert.fail();
        } catch (FormatException f) {
            Assert.fail();
        } catch (NotExistException ignore) {
        }


        try {
            //not a valid stage
            creatorBusiness.addStageToExperiment(manager.getBguUsername(), exp.getExperimentId(), new JSONObject());
            Assert.fail();
        } catch (NotExistException f) {
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


        try {
            //illegal indexes to check
            creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "grading task", new ArrayList<>(), List.of(-1, -2, -3), new ArrayList<>());
            Assert.fail();
        } catch (FormatException ignored) {
            Assert.fail();
        } catch (NotExistException ignore) {
        }


        try {
            int id = creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "grading task", new ArrayList<>(), List.of(2), new ArrayList<>());
            cache.getGradingTaskById(manager.getBguUsername(), experiment.getExperimentId(),id);
        } catch (Exception fail) {
            Assert.fail();
        }
    }

    @Test
    public void addStageToGradingTask() {
        //addToPersonal and addToResultsExp are the same so testing on personal exp is sufficient
        try {
            //not exist manager
            creatorBusiness.addToPersonal("not exist", experiment.getExperimentId(), gt.getGradingTaskId(), Utils.getStumpInfoStage());
            Assert.fail();
        } catch (NotExistException ignored) {
        } catch (FormatException e) {
            Assert.fail();
        }

        try {
            //not exist experiment
            creatorBusiness.addToPersonal(manager.getBguUsername(), -1, gt.getGradingTaskId(), Utils.getStumpInfoStage());
            Assert.fail();
        } catch (NotExistException ignored) {
        } catch (FormatException e) {
            Assert.fail();
        }

        try {
            //not exist grading task
            creatorBusiness.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), -1, Utils.getStumpInfoStage());
            Assert.fail();
        } catch (NotExistException ignored) {
        } catch (FormatException e) {
            Assert.fail();
        }

        try {
            //illegal stage
            creatorBusiness.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), gt.getGradingTaskId(), new JSONObject());
            Assert.fail();
        } catch (FormatException ignored) {
        } catch (NotExistException e) {
            Assert.fail();
        }

        try {
            int before = gt.getGeneralExperiment().getStages().size();
            creatorBusiness.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), gt.getGradingTaskId(), Utils.getStumpInfoStage());
            int after = gt.getGeneralExperiment().getStages().size();
            Assert.assertEquals(before + 1, after);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void setStagesToCheck() {
        try {
            //not exist manager
            creatorBusiness.setStagesToCheck("not exist", experiment.getExperimentId(), gt.getGradingTaskId(), List.of(0));
            Assert.fail();
        } catch (NotExistException ignored) {
        }

        try {
            //not exist experiment
            creatorBusiness.setStagesToCheck(manager.getBguUsername(), -1, gt.getGradingTaskId(), List.of(0));
            Assert.fail();
        } catch (NotExistException ignored) {
        }

        try {
            //not exist grading task
            creatorBusiness.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), -1, List.of(0));
            Assert.fail();
        } catch (NotExistException ignored) {
        }

        try {
            //illegal stage
            creatorBusiness.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), gt.getGradingTaskId(), List.of(-2));
            Assert.fail();
        } catch (NotExistException ignored) {
        }

        try {
            creatorBusiness.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), gt.getGradingTaskId(), List.of(0));
            Assert.assertEquals(1,gt.getStages().size());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void addAllies() {
        //setAlliePermissions(researcherName, expId, allieMail, List<String> permissions) throws NotExistException
        String ally_mail = "fucky@post.bgu.ac.il";
        cache.addManager(new ManagementUser("ally", "123", ally_mail));
        try {
            //not exist manager
            creatorBusiness.setAlliePermissions("not exist", experiment.getExperimentId(), ally_mail, "view", List.of("PERMISSION"));
            Assert.fail();
        } catch (NotExistException ignored) {
        }

        try {
            //not exist experiment
            creatorBusiness.setAlliePermissions(manager.getBguUsername(), -1, ally_mail, "edit", List.of("PERMISSION"));
            Assert.fail();
        } catch (NotExistException ignored) {
        }

        try {
            creatorBusiness.setAlliePermissions(manager.getBguUsername(), experiment.getExperimentId(), ally_mail, "view", List.of("PERMISSION"));
            Assert.assertEquals(cache.getManagerByEMail(ally_mail).getPermissions().size(), 1);

            creatorBusiness.setAlliePermissions(manager.getBguUsername(), experiment.getExperimentId(), ally_mail, "edit", List.of("PERMISSION", "ADMIN?"));
            Assert.assertEquals(cache.getManagerByEMail(ally_mail).getPermissions().size(), 2);
        } catch (NotExistException e) {
            Assert.fail();
        }
    }

    @Test
    public void addExperimentee() {
        String expee_mail = "fucky@post.bgu.ac.il";
        String expee_code = "rrrr";
        try {
            //not exist manager
            creatorBusiness.addExperimentee("not exist", experiment.getExperimentId(), expee_code, expee_mail);
            Assert.fail();
        } catch (NotExistException ignored) {
        } catch (ExistException fuck) {
            Assert.fail();
        }

        try {
            //not exist experiment
            creatorBusiness.addExperimentee(manager.getBguUsername(), -1, expee_code, expee_mail);
            Assert.fail();
        } catch (NotExistException ignored) {
        } catch (ExistException fuck) {
            Assert.fail();
        }

        //checking that there isn't any expee with that access code
        Assert.assertFalse(cache.isExpeeInExperiment(expee_code));

        try {
            creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), expee_code, expee_mail);
            Assert.assertTrue(cache.isExpeeInExperiment(expee_code));
        } catch (Exception fuck) {
            Assert.fail();
        }


        try {
            //exist experimentee
            creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), expee_code, expee_mail);
            Assert.fail();
        } catch (NotExistException fuck) {
            Assert.fail();
        } catch (ExistException ignore) {
        }
    }

    @Test
    public void addExpeeToGrader(){
        String grader_mail = "grader@post.bgu.ac.il";
        String grader_code = "123";
        String expee_code = expee.getAccessCode();
        try {
            creatorBusiness.addGraderToGradingTask(manager.getBguUsername(), experiment.getExperimentId(), gt.getGradingTaskId(), grader_mail, grader_code);
        }
        catch (Exception e){
            System.out.println(e);
        }
        try {
            //not exist manager
            creatorBusiness.addExpeeToGrader("not exist", experiment.getExperimentId(), gt.getGradingTaskId(),grader_mail, expee_code);
            Assert.fail();
        } catch (NotExistException | CodeException ignored) {
        } catch (ExistException e) {
            Assert.fail();
        }

        try {
            //not exist experiment
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), -1, gt.getGradingTaskId(), grader_mail, expee_code);
            Assert.fail();
        } catch (NotExistException | CodeException ignored) {
        } catch (ExistException e) {
            Assert.fail();
        }

        try {
            //not exist grading task
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), -1, grader_mail,expee_code);
            Assert.fail();
        } catch (NotExistException | CodeException ignored) {
        } catch (ExistException e) {
            Assert.fail();
        }


        try {
            //not exist expee
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), gt.getGradingTaskId(), grader_mail,"not exist");
            Assert.fail();
        } catch (NotExistException | CodeException ignored) {
        } catch (ExistException e) {
            Assert.fail();
        }

        try {
            //not exist grader
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), gt.getGradingTaskId(), "not exist",expee_code);
            Assert.fail();
        } catch (NotExistException | CodeException ignored) {
        } catch (ExistException e) {
            Assert.fail();
        }

        try{
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), gt.getGradingTaskId(), grader_mail,expee_code);
        } catch (Exception e) {
            Assert.fail();
        }

        try {
            //expee already in grader's participants
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), gt.getGradingTaskId(), grader_mail,expee_code);
            Assert.fail();
        } catch (NotExistException | CodeException e) {
            Assert.fail();
        } catch (ExistException ignore) {}
    }
}