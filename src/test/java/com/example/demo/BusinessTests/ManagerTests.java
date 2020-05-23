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
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Sql({"/create_database.sql"})
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
    private GradingTask task;
    private Grader grader;
    private String graderCode;

    @BeforeEach
    private void init() throws NotExistException, FormatException, ExistException {
        db.deleteData();
        cache.setCache();
        manager = new ManagementUser("smorad", "sm_pass", "smorad@post.bgu.ac.il");
        cache.addManager(manager);

        experiment = Utils.buildExp(creatorBusiness, manager);

        creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), "gili@post.bgu.ac.il");
        expee = cache.getExpeeByMailAndExp("gili@post.bgu.ac.il", experiment.getExperimentId());

        int gradingTaskId = creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "The Grading Task", new ArrayList<>(), List.of(), new ArrayList<>());
        task = cache.getGradingTaskById(manager.getBguUsername(), experiment.getExperimentId(), gradingTaskId);

        graderCode = creatorBusiness.addGraderToGradingTask(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), "grader@post.bgu.ac.il");
        grader = cache.getGraderByEMail("grader@post.bgu.ac.il");
    }

    @Test // manager login
    public void researcherLoginTest() {
        // not the user password - should fail
        Assert.assertFalse(creatorBusiness.researcherLogin(manager.getBguUsername(), "not the password"));
//        Assert.assertFalse(db.getManagementUserByName(manager.getBguUsername()).getBguPassword().equals("not the password"));

        // real password - should pass
        Assert.assertTrue(creatorBusiness.researcherLogin(manager.getBguUsername(), manager.getBguPassword()));
//        Assert.assertTrue(db.getManagementUserByName(manager.getBguUsername()).getBguPassword().equals(manager.getBguPassword()));

        // username not exist - should fail
        Assert.assertFalse(creatorBusiness.researcherLogin("some not exist username", "a password"));
//        Assert.assertNull(db.getManagementUserByName("some not exist username"));
    }

    @Test
    @Transactional
    public void setAlliesPermissions() throws NotExistException, ExistException {
        int numAllies = creatorBusiness.getAllies(manager.getBguUsername(), experiment.getExperimentId()).size();
        String ally_mail = "fucky@post.bgu.ac.il";
        creatorBusiness.addAlly(manager.getBguUsername(), ally_mail, List.of("PERMISSION"));
//        long currentPerms = db.getNumberOfPermissions();

        creatorBusiness.setAlliePermissions(manager.getBguUsername(), experiment.getExperimentId(), ally_mail, "view", List.of("PERMISSION"));
        Assert.assertEquals(cache.getManagerByEMail(ally_mail).getPermissions().size(), 1);
//            Assert.assertEquals(db.getNumberOfPermissions(), currentPerms + 1);

        creatorBusiness.setAlliePermissions(manager.getBguUsername(), experiment.getExperimentId(), ally_mail, "edit", List.of("PERMISSION", "ADMIN?"));
        Assert.assertEquals(cache.getManagerByEMail(ally_mail).getPermissions().size(), 2);
//            Assert.assertEquals(db.getNumberOfPermissions(), currentPerms + 2);

        Assert.assertEquals(numAllies + 2, creatorBusiness.getAllies(manager.getBguUsername(), experiment.getExperimentId()).size());
    }

    @Test
    public void setPermissionsNoExp() throws NotExistException, ExistException {
        int numAllies = creatorBusiness.getAllies(manager.getBguUsername(), experiment.getExperimentId()).size();
        String ally_mail = "fucky@post.bgu.ac.il";
        creatorBusiness.addAlly(manager.getBguUsername(), ally_mail, List.of("PERMISSION"));
//        long currentPerms = db.getNumberOfPermissions();
        try {
            //not exist experiment
            creatorBusiness.setAlliePermissions(manager.getBguUsername(), -1, ally_mail, "edit", List.of("PERMISSION"));
            Assert.fail();
        } catch (NotExistException ignored) {
//            Assert.assertEquals(db.getNumberOfPermissions(), currentPerms);
        }
        Assert.assertEquals(numAllies, creatorBusiness.getAllies(manager.getBguUsername(), experiment.getExperimentId()).size());
    }

    @Test
    public void setPermissionsNoManager() throws NotExistException, ExistException {
        int numAllies = creatorBusiness.getAllies(manager.getBguUsername(), experiment.getExperimentId()).size();
        String ally_mail = "fucky@post.bgu.ac.il";
        creatorBusiness.addAlly(manager.getBguUsername(), ally_mail, List.of("PERMISSION"));
//        long currentPerms = db.getNumberOfPermissions();
        try {
            //not exist manager
            creatorBusiness.setAlliePermissions("not exist", experiment.getExperimentId(), ally_mail, "view", List.of("PERMISSION"));
            Assert.fail();
        } catch (NotExistException ignored) {
//            Assert.assertEquals(db.getNumberOfPermissions(), currentPerms);
        }
        Assert.assertEquals(numAllies, creatorBusiness.getAllies(manager.getBguUsername(), experiment.getExperimentId()).size());
    }

    @Test
    @Transactional
    public void addAllExperiment() throws NotExistException, FormatException, ExistException {
        String expName = "testExp";
        List<JSONObject> stages = Utils.buildStages();

        //new experiment should pass
        int expNum = manager.getManagementUserToExperiments().size();
        int expId = creatorBusiness.addExperiment(manager.getBguUsername(), expName, stages);
        Assert.assertEquals(manager.getManagementUserToExperiments().size(), expNum + 1);
//            Assert.assertEquals(db.getExperimentById(expId).getExperimentName(), expName);
        Experiment exp = manager.getExperimentByName(expName);
        Assert.assertTrue(exp.containsManger(manager));
//            Assert.assertTrue(db.getExperimentById(expId).containsManger(manager));
        Assert.assertEquals(expNum + 1, manager.getManagementUserToExperiments().size());
    }

    @Test
    public void addAllExperimentSameName() {

//        long currentExps = db.getNumberOfExperiments();
        int expNum = manager.getManagementUserToExperiments().size();
        try {
            //same name should fail
            creatorBusiness.addExperiment(manager.getBguUsername(), experiment.getExperimentName(), new ArrayList<>());
            Assert.fail();
        } catch (FormatException | NotExistException e) {
            Assert.fail();
        } catch (ExistException e) {
//            Assert.assertEquals(db.getNumberOfExperiments(), currentExps);
        }

        Assert.assertEquals(expNum, manager.getManagementUserToExperiments().size());
    }

    @Test
    public void addAllExperimentNoManger() throws NotExistException {
        //        long currentExps = db.getNumberOfExperiments();
        int expNum = manager.getManagementUserToExperiments().size();
        try {
            //creator name not exist should fail
            creatorBusiness.addExperiment("not exist name", "new exp", new ArrayList<>());
            Assert.fail();
        } catch (FormatException | ExistException e) {
            Assert.fail();
        } catch (NotExistException e) {
//            Assert.assertEquals(db.getNumberOfExperiments(), currentExps);
        }
        Assert.assertEquals(expNum, manager.getManagementUserToExperiments().size());
    }

    @Test
    @Transactional
    public void createExperiment() {
        String expName = "new exp";
        //new experiment should pass
        int expNum = manager.getManagementUserToExperiments().size();
        try {
            int expId = creatorBusiness.createExperiment(manager.getBguUsername(), expName);
            Assert.assertEquals(manager.getManagementUserToExperiments().size(), expNum + 1);
//            Assert.assertEquals(db.getExperimentById(expId).getExperimentName(), expName);
            Experiment exp = manager.getExperimentByName(expName);
            Assert.assertTrue(exp.containsManger(manager));
//            Assert.assertTrue(db.getExperimentById(expId).containsManger(manager));
            Assert.assertEquals(exp.getStages().size(), 0);
//            Assert.assertEquals(db.getExperimentById(expId).getStages().size(), 0);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void createExperimentSameName() {

//        long currentExps = db.getNumberOfExperiments();
        try {
            //same name should fail
            creatorBusiness.createExperiment(manager.getBguUsername(), experiment.getExperimentName());
            Assert.fail();
        } catch (ExistException ignore) {
//            Assert.assertEquals(db.getNumberOfExperiments(), currentExps);
        } catch (NotExistException e) {
            Assert.fail();
        }
    }

    @Test
    public void createExperimentNoManager() {
        long currentExps = db.getNumberOfExperiments();
        try {
            //creator name not exist should fail
            creatorBusiness.createExperiment("not exist name", "new exp");
            Assert.fail();
        } catch (NotExistException e) {
//            Assert.assertEquals(db.getNumberOfExperiments(), currentExps);
        } catch (ExistException e) {
            Assert.fail();
        }
    }

    @Test
    @Transactional
    public void addStage() throws FormatException, NotExistException {
        // adding legal stages should pass
        int i = experiment.getStages().size();
        for (JSONObject jStage : Utils.buildStages()) {
            creatorBusiness.addStageToExperiment(manager.getBguUsername(), experiment.getExperimentId(), jStage);
//                Assert.assertEquals(db.getExperimentById(expId).getStages().size(), i);
            Assert.assertEquals(experiment.getStages().size(), ++i);
        }
    }

    @Test
    public void addIllegalStage() {
        long currentStages = db.getNumberOfStages();
        try {
            //not a valid stage
            creatorBusiness.addStageToExperiment(manager.getBguUsername(), experiment.getExperimentId(), new JSONObject());
            Assert.fail();
        } catch (NotExistException f) {
            Assert.fail();
        } catch (FormatException ignore) {
//            Assert.assertEquals(db.getNumberOfStages(), currentStages);
        }
    }

    @Test
    public void addStageNoExp() {
        long currentStages = db.getNumberOfStages();
        try {
            //experiment id not exist
            creatorBusiness.addStageToExperiment(manager.getBguUsername(), -1, Utils.getStumpInfoStage());
            Assert.fail();
        } catch (FormatException f) {
            Assert.fail();
        } catch (NotExistException ignore) {
//            Assert.assertEquals(db.getNumberOfStages(), currentStages);
        }
    }

    @Test
    public void addStageNoManager() {
        long currentStages = db.getNumberOfStages();
        try {
            //creator name not exist
            creatorBusiness.addStageToExperiment("not exist name", experiment.getExperimentId(), Utils.getStumpInfoStage());
            Assert.fail();
        } catch (FormatException f) {
            Assert.fail();
        } catch (NotExistException ignore) {
//            Assert.assertEquals(db.getNumberOfStages(), currentStages);
        }
    }

    @Test
    @Transactional
    public void addGradingTask() {
//        long currentGT = db.getNumberOfGradingTasks();
        try {
            int id = creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "grading task", new ArrayList<>(), List.of(2), new ArrayList<>());
//            cache.getGradingTaskById(manager.getBguUsername(), experiment.getExperimentId(), id);
//            Assert.assertEquals(db.getNumberOfGradingTasks(), currentGT + 1);
//            Assert.assertNotNull(db.getGradingTaskById(id));
        } catch (Exception fail) {
            Assert.fail();
        }
    }

    @Test
    public void addGradingTaskIllegalIndexes() {
        try {
            //illegal indexes to check
            creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "grading task", new ArrayList<>(), List.of(-1, -2, -3), new ArrayList<>());
            Assert.fail();
        } catch (FormatException ignored) {
            Assert.fail();
        } catch (NotExistException ignore) {
//            Assert.assertEquals(db.getNumberOfGradingTasks(), currentGT);
        }
    }

    @Test
    public void addGradingTaskIllegalSubExp() {
        try {
            //Illegal personal & result experiments
            List<JSONObject> stagesIllegal = List.of(new JSONObject());
            creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "grading task", stagesIllegal, new ArrayList<>(), stagesIllegal);
            Assert.fail();
        } catch (FormatException ignored) {
//            Assert.assertEquals(db.getNumberOfGradingTasks(), currentGT);
        } catch (NotExistException e) {
            Assert.fail();
        }
    }

    @Test
    public void addGradingTaskNoExp() {
        try {
            //not exist experiment
            creatorBusiness.addGradingTask(manager.getBguUsername(), -1, "grading task", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            Assert.fail();
        } catch (NotExistException ignored) {
//            Assert.assertEquals(db.getNumberOfGradingTasks(), currentGT);
        } catch (FormatException e) {
            Assert.fail();
        }
    }

    @Test
    public void addGradingTaskNoManager() {
        try {
            //not exist manager
            creatorBusiness.addGradingTask("not exist", experiment.getExperimentId(), "grading task", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            Assert.fail();
        } catch (NotExistException ignored) {
//            Assert.assertEquals(db.getNumberOfGradingTasks(), currentGT);
        } catch (FormatException e) {
            Assert.fail();
        }
    }

    @Test
    @Transactional
    public void addStageToGradingTask() {
        //addToPersonal and addToResultsExp are the same so testing on personal exp is sufficient
//        long currentStages = db.getNumberOfStages();

        try {
            int before = task.getGeneralExperiment().getStages().size();
            Assert.assertEquals(before, creatorBusiness.getPersonalStages(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId()).size());
            creatorBusiness.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), Utils.getStumpInfoStage());
            int after = task.getGeneralExperiment().getStages().size();
            Assert.assertEquals(before + 1, after);
            Assert.assertEquals(after, creatorBusiness.getPersonalStages(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId()).size());
//            Assert.assertEquals(db.getNumberOfStages(), currentStages + 1);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void addIllegalStageToGradingTaskFail() {
        try {
            //illegal stage
            creatorBusiness.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), new JSONObject());
            Assert.fail();
        } catch (FormatException ignored) {
//            Assert.assertEquals(db.getNumberOfStages(), currentStages);
        } catch (NotExistException e) {
            Assert.fail();
        }
    }

    @Test
    public void addStageToGradingTaskFailNoTask() {
        try {
            //not exist grading task
            creatorBusiness.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), -1, Utils.getStumpInfoStage());
            Assert.fail();
        } catch (NotExistException ignored) {
//            Assert.assertEquals(db.getNumberOfStages(), currentStages);
        } catch (FormatException e) {
            Assert.fail();
        }
    }

    @Test
    public void addStageToGradingTaskFailNoExp() {
        try {
            //not exist experiment
            creatorBusiness.addToPersonal(manager.getBguUsername(), -1, task.getGradingTaskId(), Utils.getStumpInfoStage());
            Assert.fail();
        } catch (NotExistException ignored) {
//            Assert.assertEquals(db.getNumberOfStages(), currentStages);
        } catch (FormatException e) {
            Assert.fail();
        }
    }

    @Test
    public void addStageToGradingTaskFailNoManager() {
//        long currentStages = db.getNumberOfStages();
        try {
            //not exist manager
            creatorBusiness.addToPersonal("not exist", experiment.getExperimentId(), task.getGradingTaskId(), Utils.getStumpInfoStage());
            Assert.fail();
        } catch (NotExistException ignored) {
//            Assert.assertEquals(db.getNumberOfStages(), currentStages);
        } catch (FormatException e) {
            Assert.fail();
        }
    }

    @Test
    @Transactional
    public void setStagesToCheck() {
        try {
            Assert.assertEquals(0, task.getStages().size());
            creatorBusiness.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), List.of(1));
            Assert.assertEquals(1, task.getStages().size());
//            Assert.assertEquals(db.getGradingTaskById(task.getGradingTaskId()).getStages().size(), 1);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void setIllegalStagesToCheckFail() {
        try {
            //illegal stage
            creatorBusiness.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), List.of(-2));
            Assert.fail();
        } catch (NotExistException ignored) {
//            Assert.assertEquals(db.getGradingTaskById(task.getGradingTaskId()).getStages().size(), 1);
        } catch (FormatException fuck) {
            Assert.fail();
        }
    }

    @Test
    public void setStagesToCheckFailNoTask() {
        try {
            //not exist grading task
            creatorBusiness.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), -1, List.of(0));
            Assert.fail();
        } catch (NotExistException ignored) {
//            Assert.assertEquals(db.getGradingTaskById(task.getGradingTaskId()).getStages().size(), 1);
        } catch (FormatException fuck) {
            Assert.fail();
        }
    }

    @Test
    public void setStagesToCheckFailNoExp() {
        try {
            //not exist experiment
            creatorBusiness.setStagesToCheck(manager.getBguUsername(), -1, task.getGradingTaskId(), List.of(0));
            Assert.fail();
        } catch (NotExistException ignored) {
//            Assert.assertEquals(db.getGradingTaskById(task.getGradingTaskId()).getStages().size(), 1);
        } catch (FormatException fuck) {
            Assert.fail();
        }
    }

    @Test
    public void setStagesToCheckFailNoManager() {
        try {
            //not exist manager
            creatorBusiness.setStagesToCheck("not exist", experiment.getExperimentId(), task.getGradingTaskId(), List.of(0));
            Assert.fail();
        } catch (NotExistException ignored) {
//            Assert.assertEquals(db.getGradingTaskById(task.getGradingTaskId()).getStages().size(), 1);
        } catch (FormatException fuck) {
            Assert.fail();
        }
    }

    @Test
    @Transactional
    public void addExperimentee() {
        String expee_mail = "fucky@post.bgu.ac.il";

        try {
            creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), expee_mail);
            Assert.assertTrue(cache.isExpeeInExperiment(expee_mail, experiment.getExperimentId()));
//            Assert.assertNotNull(db.getExperimenteeByEmail(expee_mail));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void addExperimenteeFailSameExpee() throws NotExistException, ExistException {
//        long currentExpees = db.getNumberOfExperimentees();
        try {
            //exist experimentee
            creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), expee.getExperimenteeEmail());
            Assert.fail();
        } catch (NotExistException e) {
            Assert.fail();
        } catch (ExistException ignore) {
//            Assert.assertEquals(db.getNumberOfExperimentees(), currentExpees);
        }
    }

    @Test
    public void addExperimenteeFailNoExp() {
        try {
            //not exist experiment
            creatorBusiness.addExperimentee(manager.getBguUsername(), -1, "fucky@post.bgu.ac.il");
            Assert.fail();
        } catch (NotExistException ignored) {
//            Assert.assertNull(db.getExperimenteeByEmail(expee_mail));
        } catch (ExistException e) {
            Assert.fail();
        }
    }

    @Test
    public void addExperimenteeFailNoManager() {
        try {
            //not exist manager
            creatorBusiness.addExperimentee("not exist", experiment.getExperimentId(), "fucky@post.bgu.ac.il");
            Assert.fail();
        } catch (NotExistException ignored) {
//            Assert.assertTrue(db.getExperimenteeByEmail(expee_mail) == null);
        } catch (ExistException e) {
            Assert.fail();
        }
    }

    @Test
    @Transactional
    public void addAlly() throws NotExistException, ExistException {
        String allyMail = "ally@post";
        Assert.assertFalse(creatorBusiness.researcherLogin(allyMail, "TEMP"));
        creatorBusiness.addAlly(manager.getBguUsername(), allyMail, List.of("PERMISSION"));
        Assert.assertTrue(creatorBusiness.researcherLogin(allyMail, "TEMP"));
    }

    @Test
    public void addAllyFailNoManager() throws NotExistException, ExistException {
        try {
            creatorBusiness.addAlly("not exist username", "ally@post", List.of("PERMISSION"));
            Assert.fail();
        } catch (NotExistException ignore) {
        }
    }

    @Test
    public void addAllyFailSameAlly() throws NotExistException, ExistException {
        String allyMail = "ally@post";
        creatorBusiness.addAlly(manager.getBguUsername(), allyMail, List.of("PERMISSION"));

        try {
            creatorBusiness.addAlly(manager.getBguUsername(), allyMail, List.of("PERMISSION"));
            Assert.fail();
        } catch (ExistException ignore) {
        }
    }

    @Test
    @Transactional
    public void addExpeeToGrader() throws NotExistException, ExistException {
//            Assert.assertNotNull(db.getGraderToGradingTaskByCode(UUID.fromString(graderCode)));

        try {
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), grader.getGraderEmail(), expee.getExperimenteeEmail());
//            Assert.assertNotNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader_mail, db.getExperimenteeByEmail(expee_mail).getParticipant().getParticipantId()));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void addExpeeToGraderFailSameExpee() throws NotExistException, ExistException {
        creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), grader.getGraderEmail(), expee.getExperimenteeEmail());
        //    Assert.assertNotNull(db.getGraderToGradingTaskByCode(UUID.fromString(code)));
//            Assert.assertNotNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader_mail, db.getExperimenteeByEmail(expee_mail).getParticipant().getParticipantId()));

        try {
            //expee already in grader's participants
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), grader.getGraderEmail(), expee.getExperimenteeEmail());
            Assert.fail();
        } catch (NotExistException e) {
            Assert.fail();
        } catch (ExistException ignore) {
//            Assert.assertNotNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader_mail, db.getExperimenteeByEmail(expee_mail).getParticipant().getParticipantId()));
        }
    }

    @Test
    public void addExpeeToGraderFailNoGrader() throws NotExistException, ExistException {
        try {
            //not exist grader
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), "not exist", expee.getExperimenteeEmail());
            Assert.fail();
        } catch (NotExistException ignored) {
//            Assert.assertNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader_mail, db.getExperimenteeByEmail(expee_mail).getParticipant().getParticipantId()));
        } catch (ExistException e) {
            Assert.fail();
        }
    }

    @Test
    public void addExpeeToGraderFailNoExpee() throws NotExistException, ExistException {
        try {
            //not exist expee
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), grader.getGraderEmail(), "not exist");
            Assert.fail();
        } catch (NotExistException ignored) {
//            Assert.assertNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader_mail, db.getExperimenteeByEmail(expee_mail).getParticipant().getParticipantId()));
        } catch (ExistException e) {
            Assert.fail();
        }
    }

    @Test
    public void addExpeeToGraderFailNoTask() throws NotExistException, ExistException {
        try {
            //not exist grading task
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), -1, grader.getGraderEmail(), expee.getExperimenteeEmail());
            Assert.fail();
        } catch (NotExistException ignored) {
//            Assert.assertNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader_mail, db.getExperimenteeByEmail(expee_mail).getParticipant().getParticipantId()));
        } catch (ExistException e) {
            Assert.fail();
        }
    }

    @Test
    public void addExpeeToGraderFailNoExp() throws NotExistException, ExistException {
        try {
            //not exist experiment
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), -1, task.getGradingTaskId(), grader.getGraderEmail(), expee.getExperimenteeEmail());
            Assert.fail();
        } catch (NotExistException ignored) {
//            Assert.assertNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader_mail, db.getExperimenteeByEmail(expee_mail).getParticipant().getParticipantId()));
        } catch (ExistException e) {
            Assert.fail();
        }
    }

    @Test
    public void addExpeeToGraderFailNoManager() throws NotExistException, ExistException {
        try {
            //not exist manager
            creatorBusiness.addExpeeToGrader("not exist", experiment.getExperimentId(), task.getGradingTaskId(), grader.getGraderEmail(), expee.getExperimenteeEmail());
            Assert.fail();
        } catch (NotExistException ignored) {
//            Assert.assertNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader_mail, db.getExperimenteeByEmail(expee_mail).getParticipant().getParticipantId()));
        } catch (ExistException e) {
            Assert.fail();
        }
    }

    @Test
    public void getExpeesOfTask() throws NotExistException, ExistException {
        int numExpees = creatorBusiness.getTaskExperimentees(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId()).size();
        creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), grader.getGraderEmail(), expee.getExperimenteeEmail());
        List<Participant> expees = creatorBusiness.getTaskExperimentees(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId());
        Assert.assertEquals(numExpees + 1, expees.size());

        for (Participant p : expees)
            if (p.getParticipantId() == expee.getParticipant().getParticipantId()) return;

        Assert.fail();
    }

    @Test
    public void getGraders() throws NotExistException, ExistException {
        int numGraders = creatorBusiness.getTaskGraders(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId()).size();
        creatorBusiness.addGraderToGradingTask(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), "grader@post");
        List<Grader> graders = creatorBusiness.getTaskGraders(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId());
        Assert.assertEquals(numGraders + 1, graders.size());

        for (Grader g : graders)
            if (g.getGraderEmail().equals("grader@post")) return;

        Assert.fail();
    }

    @Test
    public void getGradersFailNoManager() {
        try {
            //not real manager
            creatorBusiness.getTaskGraders("not exist manager", experiment.getExperimentId(), task.getGradingTaskId());
            Assert.fail();
        } catch (NotExistException ignore) {
        }
    }

    @Test
    public void getGradersFailNoExp() {
        try {
            //not real experiment
            creatorBusiness.getTaskGraders(manager.getBguUsername(), -1, task.getGradingTaskId());
            Assert.fail();
        } catch (NotExistException ignore) {
        }
    }

    @Test
    public void getGradersFailNoTask() {
        try {
            //not real task
            creatorBusiness.getTaskGraders(manager.getBguUsername(), experiment.getExperimentId(), -1);
            Assert.fail();
        } catch (NotExistException ignore) {
        }
    }

    @Test
    public void getGradingTasks() throws NotExistException, FormatException {
        int numTasks = creatorBusiness.getGradingTasks(manager.getBguUsername(), experiment.getExperimentId()).size();
        creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "Another Grading Task", new ArrayList<>(), List.of(2), new ArrayList<>());
        Assert.assertEquals(numTasks + 1, creatorBusiness.getGradingTasks(manager.getBguUsername(), experiment.getExperimentId()).size());
    }

}