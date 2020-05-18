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

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
public class ManagerTests {

    private CreatorBusiness creatorBusiness;
    private DataCache cache;
    private DBAccess db;

    @Autowired
    public ManagerTests(CreatorBusiness creatorBusiness, DataCache cache, DBAccess db) {
        this.creatorBusiness = creatorBusiness;
        this.cache = cache;
        this.db = db;
    }

    private ManagementUser manager;
    private Experiment experiment;
    private Experimentee expee;
    private int gradingTaskId;
    private GradingTask gt;

    @BeforeEach
    private void init() throws NotExistException, FormatException, ExistException {
        cache.setCache();
        db.deleteData();
        manager = new ManagementUser("smorad", "sm_pass", "smorad@post.bgu.ac.il");
        cache.addManager(manager);
        List<JSONObject> stages = Utils.buildStages();
        creatorBusiness.addExperiment(manager.getBguUsername(), "The Experiment", stages);
        experiment = manager.getExperimentByName("The Experiment");
        expee = new Experimentee( "gili@post.bgu.ac.il",experiment);
        cache.addExperimentee(expee);
        gradingTaskId = creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "The Grading Task", new ArrayList<>(), List.of(2), new ArrayList<>());
        gt = cache.getGradingTaskById(manager.getBguUsername(), experiment.getExperimentId(), gradingTaskId);
    }

    @Test // manager login
    public void researcherLoginTest() {
        Assert.assertFalse(creatorBusiness.researcherLogin(manager.getBguUsername(), "not the password"));
        Assert.assertTrue(db.getManagementUserByName(manager.getBguUsername()).getBguPassword() != "not the password");
        Assert.assertTrue(creatorBusiness.researcherLogin(manager.getBguUsername(), manager.getBguPassword()));
        Assert.assertTrue(db.getManagementUserByName(manager.getBguUsername()).getBguPassword().equals(manager.getBguPassword()));
    }

    @Test
    @Transactional
    public void addAllExperiment() {
        String expName = "testExp";
        List<JSONObject> stages = Utils.buildStages();

        //new experiment should pass
        int expNum = manager.getManagementUserToExperiments().size();
        try {
            int expId = creatorBusiness.addExperiment(manager.getBguUsername(), expName, stages);
            Assert.assertEquals(manager.getManagementUserToExperiments().size(), expNum + 1);
            Assert.assertEquals(db.getExperimentById(expId).getExperimentName(), expName);
            Experiment exp = manager.getExperimentByName(expName);
            Assert.assertTrue(exp.containsManger(manager));
            Assert.assertTrue(db.getExperimentById(expId).containsManger(manager));

        } catch (Exception e) {
            Assert.fail();
        }
        long currentExps = db.getNumberOfExperiments();
        try {
            //same name should fail
            creatorBusiness.addExperiment(manager.getBguUsername(), expName, new ArrayList<>());
            Assert.fail();
        } catch (FormatException | NotExistException e) {
            Assert.fail();
        } catch (ExistException e) {
            Assert.assertEquals(db.getNumberOfExperiments(), currentExps);
        }

        try {
            //creator name not exist should fail
            creatorBusiness.addExperiment("not exist name", expName, new ArrayList<>());
            Assert.fail();
        } catch (FormatException | ExistException e) {
            Assert.fail();
        } catch (NotExistException e) {
            Assert.assertEquals(db.getNumberOfExperiments(), currentExps);
        }
    }

    @Test
    @Transactional
    public void createExperiment() {
        String expName = "testExp";

        //new experiment should pass
        int expNum = manager.getManagementUserToExperiments().size();
        try {
            int expId = creatorBusiness.createExperiment(manager.getBguUsername(), expName);
            Assert.assertEquals(manager.getManagementUserToExperiments().size(), expNum + 1);
            Assert.assertEquals(db.getExperimentById(expId).getExperimentName(), expName);
            Experiment exp = manager.getExperimentByName(expName);
            Assert.assertTrue(exp.containsManger(manager));
            Assert.assertTrue(db.getExperimentById(expId).containsManger(manager));
            Assert.assertEquals(exp.getStages().size(), 0);
            Assert.assertEquals(db.getExperimentById(expId).getStages().size(), 0);
        } catch (Exception e) {
            Assert.fail();
        }
        long currentExps = db.getNumberOfExperiments();
        try {
            //same name should fail
            creatorBusiness.createExperiment(manager.getBguUsername(), expName);
            Assert.fail();
        } catch (ExistException ignore) {
            Assert.assertEquals(db.getNumberOfExperiments(), currentExps);
        } catch (NotExistException e) {
            Assert.fail();
        }

        try {
            //creator name not exist should fail
            creatorBusiness.createExperiment("not exist name", expName);
            Assert.fail();
        } catch (NotExistException e) {
            Assert.assertEquals(db.getNumberOfExperiments(), currentExps);
        } catch (ExistException e) {
            Assert.fail();
        }
    }

    @Test
    @Transactional
    public void addStage() {
        String expName = "testExp";
        Experiment exp = new Experiment();
        List<JSONObject> stages = Utils.buildStages();
        try {
            int expId = creatorBusiness.createExperiment(manager.getBguUsername(), expName);
            exp = manager.getExperimentByName(expName);

            // adding legal stages should pass
            int i = 1;
            for (JSONObject jStage : stages) {
                creatorBusiness.addStageToExperiment(manager.getBguUsername(), exp.getExperimentId(), jStage);
                Assert.assertEquals(db.getExperimentById(expId).getStages().size(), i);
                Assert.assertEquals(exp.getStages().size(), i);
                i++;
            }
        } catch (Exception e) {
            Assert.fail();
        }

        //VVVVVVVV should fails VVVVVVVV
        long currentStages = db.getNumberOfStages();
        try {
            //creator name not exist
            creatorBusiness.addStageToExperiment("not exist name", exp.getExperimentId(), stages.get(0));
            Assert.fail();
        } catch (FormatException f) {
            Assert.fail();
        } catch (NotExistException ignore) {
            Assert.assertEquals(db.getNumberOfStages(), currentStages);
        }


        try {
            //experiment id not exist
            creatorBusiness.addStageToExperiment(manager.getBguUsername(), -1, stages.get(0));
            Assert.fail();
        } catch (FormatException f) {
            Assert.fail();
        } catch (NotExistException ignore) {
            Assert.assertEquals(db.getNumberOfStages(), currentStages);
        }


        try {
            //not a valid stage
            creatorBusiness.addStageToExperiment(manager.getBguUsername(), exp.getExperimentId(), new JSONObject());
            Assert.fail();
        } catch (NotExistException f) {
            Assert.fail();
        } catch (FormatException ignore) {
            Assert.assertEquals(db.getNumberOfStages(), currentStages);
        }
    }

    @Test
    @Transactional
    public void addGradingTask() {
        long currentGT = db.getNumberOfGradingTasks();
        try {
            //not exist manager
            creatorBusiness.addGradingTask("not exist", experiment.getExperimentId(), "grading task", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(db.getNumberOfGradingTasks(), currentGT);
        } catch (FormatException e) {
            Assert.fail();
        }

        try {
            //not exist experiment
            creatorBusiness.addGradingTask(manager.getBguUsername(), -1, "grading task", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(db.getNumberOfGradingTasks(), currentGT);
        } catch (FormatException e) {
            Assert.fail();
        }


        try {
            //not legal personal & result experiments
            List<JSONObject> stagesIllegal = List.of(new JSONObject());
            creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "grading task", stagesIllegal, new ArrayList<>(), stagesIllegal);
            Assert.fail();
        } catch (FormatException ignored) {
            Assert.assertEquals(db.getNumberOfGradingTasks(), currentGT);
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
            Assert.assertEquals(db.getNumberOfGradingTasks(), currentGT);
        }


        try {
            int id = creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "grading task", new ArrayList<>(), List.of(2), new ArrayList<>());
            cache.getGradingTaskById(manager.getBguUsername(), experiment.getExperimentId(),id);
            Assert.assertEquals(db.getNumberOfGradingTasks(), currentGT + 1);
            Assert.assertTrue(db.getGradingTaskById(id) != null);
        } catch (Exception fail) {
            Assert.fail();
        }
    }

    @Test
    @Transactional
    public void addStageToGradingTask() {
        //addToPersonal and addToResultsExp are the same so testing on personal exp is sufficient
        long currentStages = db.getNumberOfStages();
        try {
            //not exist manager
            creatorBusiness.addToPersonal("not exist", experiment.getExperimentId(), gt.getGradingTaskId(), Utils.getStumpInfoStage());
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(db.getNumberOfStages(), currentStages);
        } catch (FormatException e) {
            Assert.fail();
        }

        try {
            //not exist experiment
            creatorBusiness.addToPersonal(manager.getBguUsername(), -1, gt.getGradingTaskId(), Utils.getStumpInfoStage());
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(db.getNumberOfStages(), currentStages);
        } catch (FormatException e) {
            Assert.fail();
        }

        try {
            //not exist grading task
            creatorBusiness.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), -1, Utils.getStumpInfoStage());
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(db.getNumberOfStages(), currentStages);
        } catch (FormatException e) {
            Assert.fail();
        }

        try {
            //illegal stage
            creatorBusiness.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), gt.getGradingTaskId(), new JSONObject());
            Assert.fail();
        } catch (FormatException ignored) {
            Assert.assertEquals(db.getNumberOfStages(), currentStages);
        } catch (NotExistException e) {
            Assert.fail();
        }

        try {
            int before = gt.getGeneralExperiment().getStages().size();
            creatorBusiness.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), gt.getGradingTaskId(), Utils.getStumpInfoStage());
            int after = gt.getGeneralExperiment().getStages().size();
            Assert.assertEquals(before + 1, after);
            Assert.assertEquals(db.getNumberOfStages(), currentStages + 1);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    @Transactional
    public void setStagesToCheck() {
        try {
            //not exist manager
            creatorBusiness.setStagesToCheck("not exist", experiment.getExperimentId(), gt.getGradingTaskId(), List.of(0));
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(db.getGradingTaskById(gt.getGradingTaskId()).getStages().size(), 1);
        }
        catch (FormatException fuck){
            Assert.fail();
        }

        try {
            //not exist experiment
            creatorBusiness.setStagesToCheck(manager.getBguUsername(), -1, gt.getGradingTaskId(), List.of(0));
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(db.getGradingTaskById(gt.getGradingTaskId()).getStages().size(), 1);
        }
        catch (FormatException fuck){
            Assert.fail();
        }

        try {
            //not exist grading task
            creatorBusiness.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), -1, List.of(0));
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(db.getGradingTaskById(gt.getGradingTaskId()).getStages().size(), 1);
        }
        catch (FormatException fuck){
            Assert.fail();
        }

        try {
            //illegal stage
            creatorBusiness.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), gt.getGradingTaskId(), List.of(-2));
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(db.getGradingTaskById(gt.getGradingTaskId()).getStages().size(), 1);
        }
        catch (FormatException fuck){
            Assert.fail();
        }

        try {
            creatorBusiness.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), gt.getGradingTaskId(), List.of(1));
            Assert.assertEquals(1,gt.getStages().size());
            Assert.assertEquals(db.getGradingTaskById(gt.getGradingTaskId()).getStages().size(), 1);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    @Transactional
    public void addAllies() {
        //setAlliePermissions(researcherName, expId, allieMail, List<String> permissions) throws NotExistException
        String ally_mail = "fucky@post.bgu.ac.il";
        cache.addManager(new ManagementUser("ally", "123", ally_mail));
        long currentPerms = db.getNumberOfPermissions();
        try {
            //not exist manager
            creatorBusiness.setAlliePermissions("not exist", experiment.getExperimentId(), ally_mail, "view", List.of("PERMISSION"));
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(db.getNumberOfPermissions(), currentPerms);
        }

        try {
            //not exist experiment
            creatorBusiness.setAlliePermissions(manager.getBguUsername(), -1, ally_mail, "edit", List.of("PERMISSION"));
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(db.getNumberOfPermissions(), currentPerms);
        }

        try {
            creatorBusiness.setAlliePermissions(manager.getBguUsername(), experiment.getExperimentId(), ally_mail, "view", List.of("PERMISSION"));
            Assert.assertEquals(cache.getManagerByEMail(ally_mail).getPermissions().size(), 1);
            Assert.assertEquals(db.getNumberOfPermissions(), currentPerms + 1);

            creatorBusiness.setAlliePermissions(manager.getBguUsername(), experiment.getExperimentId(), ally_mail, "edit", List.of("PERMISSION", "ADMIN?"));
            Assert.assertEquals(cache.getManagerByEMail(ally_mail).getPermissions().size(), 2);
            Assert.assertEquals(db.getNumberOfPermissions(), currentPerms + 2);
        } catch (NotExistException e) {
            Assert.fail();
        }
    }

    @Test
    @Transactional
    public void addExperimentee() {
        String expee_mail = "fucky@post.bgu.ac.il";
        try {
            //not exist manager
            creatorBusiness.addExperimentee("not exist", experiment.getExperimentId(), expee_mail);
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertTrue(db.getExperimenteeByEmail(expee_mail) == null);
        } catch (ExistException fuck) {
            Assert.fail();
        }

        try {
            //not exist experiment
            creatorBusiness.addExperimentee(manager.getBguUsername(), -1, expee_mail);
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertTrue(db.getExperimenteeByEmail(expee_mail) == null);
        } catch (ExistException fuck) {
            Assert.fail();
        }

        //checking that there isn't any expee with that mail in this experiment
        Assert.assertFalse(cache.isExpeeInExperiment(expee_mail, experiment.getExperimentId()));

        try {
            creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), expee_mail);
            Assert.assertTrue(cache.isExpeeInExperiment(expee_mail, experiment.getExperimentId()));
            Assert.assertTrue(db.getExperimenteeByEmail(expee_mail) != null);
        } catch (Exception fuck) {
            Assert.fail();
        }

        long currentExpees = db.getNumberOfExperimentees();
        try {
            //exist experimentee
            creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), expee_mail);
            Assert.fail();
        } catch (NotExistException fuck) {
            Assert.fail();
        } catch (ExistException ignore) {
            Assert.assertEquals(db.getNumberOfExperimentees(), currentExpees);
        }
    }

    @Test
    @Transactional
    public void addExpeeToGrader(){
        String grader_mail = "grader@post.bgu.ac.il";
        String expee_mail = expee.getExperimenteeEmail();

        try {
            String code = creatorBusiness.addGraderToGradingTask(manager.getBguUsername(), experiment.getExperimentId(), gt.getGradingTaskId(), grader_mail);
            Assert.assertTrue(db.getGraderToGradingTaskByCode(UUID.fromString(code)) != null);
        }
        catch (Exception e){
            System.out.println(e);
        }
        try {
            //not exist manager
            creatorBusiness.addExpeeToGrader("not exist", experiment.getExperimentId(), gt.getGradingTaskId(),grader_mail,expee_mail);
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertTrue(db.getGradersGTToParticipantsById(gt.getGradingTaskId(), grader_mail, db.getExperimenteeByEmail(expee_mail).getParticipant().getParticipantId()) == null);
        } catch (ExistException e) {
            Assert.fail();
        }

        try {
            //not exist experiment
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), -1, gt.getGradingTaskId(), grader_mail,expee_mail);
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertTrue(db.getGradersGTToParticipantsById(gt.getGradingTaskId(), grader_mail, db.getExperimenteeByEmail(expee_mail).getParticipant().getParticipantId()) == null);
        } catch (ExistException e) {
            Assert.fail();
        }

        try {
            //not exist grading task
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), -1, grader_mail,expee_mail);
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertTrue(db.getGradersGTToParticipantsById(gt.getGradingTaskId(), grader_mail, db.getExperimenteeByEmail(expee_mail).getParticipant().getParticipantId()) == null);
        } catch (ExistException e) {
            Assert.fail();
        }


        try {
            //not exist expee
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), gt.getGradingTaskId(), grader_mail,"not exist");
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertTrue(db.getGradersGTToParticipantsById(gt.getGradingTaskId(), grader_mail, db.getExperimenteeByEmail(expee_mail).getParticipant().getParticipantId()) == null);
        } catch (ExistException e) {
            Assert.fail();
        }

        try {
            //not exist grader
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), gt.getGradingTaskId(), "not exist",expee_mail);
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertTrue(db.getGradersGTToParticipantsById(gt.getGradingTaskId(), grader_mail, db.getExperimenteeByEmail(expee_mail).getParticipant().getParticipantId()) == null);
        } catch (ExistException e) {
            Assert.fail();
        }

        try{
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), gt.getGradingTaskId(), grader_mail,expee_mail);
            Assert.assertTrue(db.getGradersGTToParticipantsById(gt.getGradingTaskId(), grader_mail, db.getExperimenteeByEmail(expee_mail).getParticipant().getParticipantId()) != null);
        } catch (Exception e) {
            Assert.fail();
        }

        try {
            //expee already in grader's participants
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), gt.getGradingTaskId(), grader_mail,expee_mail);
            Assert.fail();
        } catch (NotExistException e) {
            Assert.fail();
        } catch (ExistException ignore) {
            Assert.assertTrue(db.getGradersGTToParticipantsById(gt.getGradingTaskId(), grader_mail, db.getExperimenteeByEmail(expee_mail).getParticipant().getParticipantId()) != null);
        }
    }
}