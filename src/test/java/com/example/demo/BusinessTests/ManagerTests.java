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

import static org.jvnet.fastinfoset.EncodingAlgorithmIndexes.UUID;

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
    private ManagementUser ally;
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
        System.out.println(expee.getAccessCode());

        int gradingTaskId = creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(),
                "The Grading Task", new ArrayList<>(), List.of(), new ArrayList<>());
        task = cache.getGradingTaskById(manager.getBguUsername(), experiment.getExperimentId(), gradingTaskId);

        graderCode = creatorBusiness.addGraderToGradingTask(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), "grader@post.bgu.ac.il");
        grader = cache.getGraderByEMail("grader@post.bgu.ac.il");

        String ally_mail = "ally@post.bgu.ac.il";
        creatorBusiness.addAlly(manager.getBguUsername(), ally_mail, List.of());
        ally = cache.getManagerByEMail(ally_mail);
        ally.setBguPassword("ally_pass");
    }

    @Test // manager login
    public void researcherLoginTest() {
        // not the user password - should fail
        Assert.assertFalse(creatorBusiness.researcherLogin(manager.getBguUsername(), "not the password"));

        // real password - should pass
        Assert.assertTrue(creatorBusiness.researcherLogin(manager.getBguUsername(), manager.getBguPassword()));
        Assert.assertTrue(db.getManagementUserByName(manager.getBguUsername()).getBguPassword().equals(manager.getBguPassword()));

        // username not exist - should fail
        Assert.assertFalse(creatorBusiness.researcherLogin("some not exist username", "a password"));
    }

    @Test
    @Transactional
    public void setAlliesPermissions() throws NotExistException, ExistException {
        //TODO: fix, and validate currentPerms (not sure we will have 2 at the end)
        int numAllies = creatorBusiness.getAllies(manager.getBguUsername(), experiment.getExperimentId()).size();
        long currentPerms = db.getNumberOfPermissions();

        creatorBusiness.setAlliePermissions(manager.getBguUsername(), experiment.getExperimentId(), ally.getUserEmail(),
                "view", List.of("PERMISSION"));
        Assert.assertEquals(cache.getManagerByEMail(ally.getUserEmail()).getPermissions().size(), 1);
        Assert.assertEquals(db.getNumberOfPermissions(), currentPerms + 1);

        creatorBusiness.setAlliePermissions(manager.getBguUsername(), experiment.getExperimentId(), ally.getUserEmail(),
                "edit", List.of("PERMISSION", "ADMIN?"));
        Assert.assertEquals(cache.getManagerByEMail(ally.getUserEmail()).getPermissions().size(), 2);
        Assert.assertEquals(db.getNumberOfPermissions(), currentPerms + 2);

        Assert.assertEquals(numAllies + 2, creatorBusiness.getAllies(manager.getBguUsername(), experiment.getExperimentId()).size());
    }

    @Test
    @Transactional
    public void setPermissionsNoExp() throws NotExistException {
        int numAllies = creatorBusiness.getAllies(manager.getBguUsername(), experiment.getExperimentId()).size();
        long currentPerms = db.getNumberOfPermissions();
        try {
            //not exist experiment
            creatorBusiness.setAlliePermissions(manager.getBguUsername(), -1, ally.getUserEmail(), "edit", List.of("PERMISSION"));
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(currentPerms, db.getNumberOfPermissions());
        }
        Assert.assertEquals(numAllies, creatorBusiness.getAllies(manager.getBguUsername(), experiment.getExperimentId()).size());
    }

    @Test
    @Transactional
    public void setPermissionsNoManager() throws NotExistException {
        int numAllies = creatorBusiness.getAllies(manager.getBguUsername(), experiment.getExperimentId()).size();
        long currentPerms = db.getNumberOfPermissions();
        try {
            //not exist manager
            creatorBusiness.setAlliePermissions("not exist", experiment.getExperimentId(), ally.getUserEmail(), "view", List.of("PERMISSION"));
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(db.getNumberOfPermissions(), currentPerms);
        }
        Assert.assertEquals(numAllies, creatorBusiness.getAllies(manager.getBguUsername(), experiment.getExperimentId()).size());
    }

    @Test
    @Transactional
    public void addAlly() throws NotExistException, ExistException {
        String allyMail = "other@post";
        long managerCount = db.getNumberOfManagers();
        Assert.assertFalse(creatorBusiness.researcherLogin(allyMail, "TEMP"));
        creatorBusiness.addAlly(manager.getBguUsername(), allyMail, List.of("PERMISSION"));
        Assert.assertTrue(creatorBusiness.researcherLogin(allyMail, "TEMP"));
        Assert.assertEquals(managerCount + 1, db.getNumberOfManagers());
    }

    @Test
    public void addAllyFailNoManager() throws ExistException {
        long managerCount = db.getNumberOfManagers();
        try {
            creatorBusiness.addAlly("not exist username", "ally@post", List.of("PERMISSION"));
            Assert.fail();
        } catch (NotExistException ignore) {
            Assert.assertNull(db.getManagementUserByEMail("ally@post"));
            Assert.assertEquals(managerCount, db.getNumberOfManagers());
        }

    }

    @Test
    @Transactional
    public void addAllyFailSameAlly() throws NotExistException {
        long managerCount = db.getNumberOfManagers();

        try {
            creatorBusiness.addAlly(manager.getBguUsername(), ally.getUserEmail(), List.of("PERMISSION"));
            Assert.fail();
        } catch (ExistException ignore) {
            Assert.assertEquals(managerCount, db.getNumberOfManagers());
        }
    }

    @Test
    @Transactional
    public void addAllExperiment() throws NotExistException, FormatException, ExistException {
        String expName = "testExp";
        List<JSONObject> stages = Utils.buildStages();

        //new experiment should pass
        int expNum = manager.getManagementUserToExperiments().size();
        int expId = creatorBusiness.addExperiment(manager.getBguUsername(), expName, stages);
        Assert.assertEquals(expNum + 1, manager.getManagementUserToExperiments().size());
        Assert.assertEquals(expName, db.getExperimentById(expId).getExperimentName());
        Experiment exp = manager.getExperimentByName(expName);
        Assert.assertTrue(exp.containsManger(manager));
        Assert.assertTrue(db.getExperimentById(expId).containsManger(manager));
        Assert.assertEquals(expNum + 1, manager.getManagementUserToExperiments().size());
    }

    @Test
    public void addAllExperimentSameName() throws NotExistException, FormatException {
        long currentExps = db.getNumberOfExperiments();
        int expNum = manager.getManagementUserToExperiments().size();
        try {
            //same name should fail
            creatorBusiness.addExperiment(manager.getBguUsername(), experiment.getExperimentName(), new ArrayList<>());
            Assert.fail();
        } catch (ExistException e) {
        }

        Assert.assertEquals(currentExps, db.getNumberOfExperiments());
        Assert.assertEquals(expNum, manager.getManagementUserToExperiments().size());
    }

    @Test
    public void addAllExperimentNoManger() throws NotExistException, FormatException, ExistException {
        long currentExps = db.getNumberOfExperiments();
        int expNum = manager.getManagementUserToExperiments().size();
        try {
            //creator name not exist should fail
            creatorBusiness.addExperiment("not exist name", "new exp", new ArrayList<>());
            Assert.fail();
        } catch (NotExistException e) {
        }

        Assert.assertEquals(expNum, manager.getManagementUserToExperiments().size());
        Assert.assertEquals(currentExps, db.getNumberOfExperiments());
    }

    @Test
    @Transactional
    public void createExperiment() throws NotExistException, ExistException {
        String expName = "new exp";
        //new experiment should pass
        int expNum = manager.getManagementUserToExperiments().size();

        int expId = creatorBusiness.createExperiment(manager.getBguUsername(), expName);
        Assert.assertEquals(expNum + 1, manager.getManagementUserToExperiments().size());
        Assert.assertEquals(expName, db.getExperimentById(expId).getExperimentName());
        Experiment exp = manager.getExperimentByName(expName);
        Assert.assertTrue(exp.containsManger(manager));
        Assert.assertTrue(db.getExperimentById(expId).containsManger(manager));
        Assert.assertEquals(0, exp.getStages().size());
        Assert.assertEquals(0, db.getExperimentById(expId).getStages().size());
    }

    @Test
    public void createExperimentSameName() throws NotExistException {
        long currentExps = db.getNumberOfExperiments();
        try {
            //same name should fail
            creatorBusiness.createExperiment(manager.getBguUsername(), experiment.getExperimentName());
            Assert.fail();
        } catch (ExistException ignore) {
        }
        Assert.assertEquals(currentExps, db.getNumberOfExperiments());
    }

    @Test
    public void createExperimentNoManager() throws ExistException {
        long currentExps = db.getNumberOfExperiments();
        try {
            //creator name not exist should fail
            creatorBusiness.createExperiment("not exist name", "new exp");
            Assert.fail();
        } catch (NotExistException e) {
        }
        Assert.assertEquals(currentExps, db.getNumberOfExperiments());
    }

    @Test
    @Transactional
    public void addStage() throws FormatException, NotExistException {
        // adding legal stages should pass
        int initstagesNum = experiment.getStages().size();
        for (JSONObject jStage : Utils.buildStages()) {
            creatorBusiness.addStageToExperiment(manager.getBguUsername(), experiment.getExperimentId(), jStage);
            Assert.assertEquals(++initstagesNum, db.getExperimentById(experiment.getExperimentId()).getStages().size());
            Assert.assertEquals(initstagesNum, experiment.getStages().size());
        }
    }

    @Test
    public void addIllegalStage() throws NotExistException {
        long currentStages = db.getNumberOfStages();
        try {
            //not a valid stage
            creatorBusiness.addStageToExperiment(manager.getBguUsername(), experiment.getExperimentId(), new JSONObject());
            Assert.fail();
        } catch (FormatException ignore) {
            Assert.assertEquals(currentStages, db.getNumberOfStages());
        }
    }

    @Test
    public void addStageNoExp() throws FormatException {
        long currentStages = db.getNumberOfStages();
        try {
            //experiment id not exist
            creatorBusiness.addStageToExperiment(manager.getBguUsername(), -1, Utils.getStumpInfoStage());
            Assert.fail();
        } catch (NotExistException ignore) {
            Assert.assertEquals(currentStages, db.getNumberOfStages());
        }
    }

    @Test
    public void addStageNoManager() throws FormatException {
        long currentStages = db.getNumberOfStages();
        try {
            //creator name not exist
            creatorBusiness.addStageToExperiment("not exist name", experiment.getExperimentId(), Utils.getStumpInfoStage());
            Assert.fail();
        } catch (NotExistException ignore) {
            Assert.assertEquals(currentStages, db.getNumberOfStages());
        }
    }

    @Test
    @Transactional
    public void addGradingTask() throws NotExistException, FormatException {
        long currentGT = db.getNumberOfGradingTasks();

        int id = creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(),
                "grading task", new ArrayList<>(), List.of(2), new ArrayList<>());
        Assert.assertEquals(currentGT + 1, db.getNumberOfGradingTasks());
        Assert.assertNotNull(db.getGradingTaskById(id));
    }

    @Test
    public void addGradingTaskIllegalIndexes() throws FormatException {
        long currentGT = db.getNumberOfGradingTasks();
        try {
            //illegal indexes to check
            creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "grading task", new ArrayList<>(), List.of(-1, -2, -3), new ArrayList<>());
            Assert.fail();
        } catch (NotExistException ignore) {
            Assert.assertEquals(currentGT, db.getNumberOfGradingTasks());
        }
    }

    @Test
    public void addGradingTaskIllegalSubExp() throws NotExistException {
        long currentGT = db.getNumberOfGradingTasks();
        try {
            //Illegal personal & result experiments
            List<JSONObject> stagesIllegal = List.of(new JSONObject());
            creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "grading task", stagesIllegal, new ArrayList<>(), stagesIllegal);
            Assert.fail();
        } catch (FormatException ignored) {
            Assert.assertEquals(currentGT, db.getNumberOfGradingTasks());
        }
    }

    @Test
    public void addGradingTaskNoExp() throws FormatException {
        long currentGT = db.getNumberOfGradingTasks();
        try {
            //not exist experiment
            creatorBusiness.addGradingTask(manager.getBguUsername(), -1, "grading task", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(currentGT, db.getNumberOfGradingTasks());
        }
    }

    @Test
    public void addGradingTaskNoManager() throws FormatException {
        long currentGT = db.getNumberOfGradingTasks();
        try {
            //not exist manager
            creatorBusiness.addGradingTask("not exist", experiment.getExperimentId(), "grading task", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(db.getNumberOfGradingTasks(), currentGT);
        }
    }

    @Test
    @Transactional
    public void addStageToGradingTask() throws NotExistException, FormatException {
        //addToPersonal and addToResultsExp are the same so testing on personal exp is sufficient
        long currentStages = db.getNumberOfStages();

        int before = task.getGeneralExperiment().getStages().size();
        Assert.assertEquals(before, creatorBusiness.getPersonalStages(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId()).size());
        creatorBusiness.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), Utils.getStumpInfoStage());
        int after = task.getGeneralExperiment().getStages().size();
        Assert.assertEquals(before + 1, after);
        Assert.assertEquals(after, creatorBusiness.getPersonalStages(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId()).size());
        Assert.assertEquals(currentStages + 1, db.getNumberOfStages());
    }

    @Test
    public void addIllegalStageToGradingTaskFail() throws NotExistException {
        long currentStages = db.getNumberOfStages();
        try {
            //illegal stage
            creatorBusiness.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), new JSONObject());
            Assert.fail();
        } catch (FormatException ignored) {
            Assert.assertEquals(currentStages, db.getNumberOfStages());
        }
    }

    @Test
    public void addStageToGradingTaskFailNoTask() throws FormatException {
        long currentStages = db.getNumberOfStages();
        try {
            //not exist grading task
            creatorBusiness.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), -1, Utils.getStumpInfoStage());
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(currentStages, db.getNumberOfStages());
        }
    }

    @Test
    public void addStageToGradingTaskFailNoExp() throws FormatException {
        long currentStages = db.getNumberOfStages();
        try {
            //not exist experiment
            creatorBusiness.addToPersonal(manager.getBguUsername(), -1, task.getGradingTaskId(), Utils.getStumpInfoStage());
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(currentStages, db.getNumberOfStages());
        }
    }

    @Test
    public void addStageToGradingTaskFailNoManager() throws FormatException {
        long currentStages = db.getNumberOfStages();
        try {
            //not exist manager
            creatorBusiness.addToPersonal("not exist", experiment.getExperimentId(), task.getGradingTaskId(), Utils.getStumpInfoStage());
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(currentStages, db.getNumberOfStages());
        }
    }

    @Test
    @Transactional
    public void setStagesToCheck() throws NotExistException, FormatException {
        Assert.assertEquals(0, task.getStages().size());
        creatorBusiness.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), List.of(1));
        Assert.assertEquals(1, task.getStages().size());
        Assert.assertEquals(1, db.getGradingTaskById(task.getGradingTaskId()).getStages().size());
    }

    @Test
    @Transactional
    public void setIllegalStagesToCheckFail() throws FormatException {
        try {
            //illegal stage
            creatorBusiness.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), List.of(-2));
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(0, db.getGradingTaskById(task.getGradingTaskId()).getStages().size());
        }
    }

    @Test
    @Transactional
    public void setInfoStageToCheckFail() throws NotExistException {
        try {
            //illegal stage
            creatorBusiness.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), List.of(0));
            Assert.fail();
        } catch (FormatException ignored) {
            Assert.assertEquals(0, db.getGradingTaskById(task.getGradingTaskId()).getStages().size());
        }
    }

    @Test
    @Transactional
    public void setStagesToCheckFailNoTask() throws FormatException {
        try {
            //not exist grading task
            creatorBusiness.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), -1, List.of(1));
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(0, db.getGradingTaskById(task.getGradingTaskId()).getStages().size());
        }
    }

    @Test
    @Transactional
    public void setStagesToCheckFailNoExp() throws FormatException {
        try {
            //not exist experiment
            creatorBusiness.setStagesToCheck(manager.getBguUsername(), -1, task.getGradingTaskId(), List.of(1));
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(0, db.getGradingTaskById(task.getGradingTaskId()).getStages().size());
        }
    }

    @Test
    @Transactional
    public void setStagesToCheckFailNoManager() throws FormatException {
        try {
            //not exist manager
            creatorBusiness.setStagesToCheck("not exist", experiment.getExperimentId(), task.getGradingTaskId(), List.of(1));
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertEquals(0, db.getGradingTaskById(task.getGradingTaskId()).getStages().size());
        }
    }

    @Test
    @Transactional
    public void addExperimentee() throws NotExistException, ExistException {
        String expee_mail = "fucky@post.bgu.ac.il";

        creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), expee_mail);
        Assert.assertTrue(cache.isExpeeInExperiment(expee_mail, experiment.getExperimentId()));
        Assert.assertNotNull(db.getExperimenteeByEmail(expee_mail));
    }

    @Test
    public void addExperimenteeFailSameExpee() throws NotExistException {
        long currentExpees = db.getNumberOfExperimentees();
        try {
            //exist experimentee
            creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), expee.getExperimenteeEmail());
            Assert.fail();
        } catch (ExistException ignore) {
            Assert.assertEquals(currentExpees, db.getNumberOfExperimentees());
        }
    }

    @Test
    public void addExperimenteeFailNoExp() throws ExistException {
        String expee_mail = "fucky@post.bgu.ac.il";
        try {
            //not exist experiment
            creatorBusiness.addExperimentee(manager.getBguUsername(), -1, expee_mail);
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertNull(db.getExperimenteeByEmail(expee_mail));
        }
    }

    @Test
    public void addExperimenteeFailNoManager() throws ExistException {
        String expee_mail = "fucky@post.bgu.ac.il";
        try {
            //not exist manager
            creatorBusiness.addExperimentee("not exist", experiment.getExperimentId(), expee_mail);
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertNull(db.getExperimenteeByEmail(expee_mail));
        }
    }

    @Test
    @Transactional
    public void addExpeeToGrader() throws NotExistException, ExistException {

        creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(),
                grader.getGraderEmail(), expee.getExperimenteeEmail());

        Assert.assertNotNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader.getGraderEmail(),
                db.getExperimenteeByEmail(expee.getExperimenteeEmail()).getParticipant().getParticipantId()));
    }

    @Test
    public void addExpeeToGraderFailSameExpee() throws NotExistException, ExistException {
        creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(),
                grader.getGraderEmail(), expee.getExperimenteeEmail());

        try {
            //expee already in grader's participants
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(),
                    grader.getGraderEmail(), expee.getExperimenteeEmail());
            Assert.fail();
        } catch (ExistException ignore) {
            Assert.assertNotNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader.getGraderEmail(),
                    db.getExperimenteeByEmail(expee.getExperimenteeEmail()).getParticipant().getParticipantId()));
        }
    }

    @Test
    public void addExpeeToGraderFailNoGrader() throws ExistException {
        try {
            //not exist grader
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(),
                    "not exist", expee.getExperimenteeEmail());
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader.getGraderEmail(),
                    db.getExperimenteeByEmail(expee.getExperimenteeEmail()).getParticipant().getParticipantId()));
        }
    }

    @Test
    public void addExpeeToGraderFailNoExpee() throws ExistException {
        try {
            //not exist expee
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(),
                    grader.getGraderEmail(), "not exist");
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader.getGraderEmail(),
                    db.getExperimenteeByEmail(expee.getExperimenteeEmail()).getParticipant().getParticipantId()));
        }
    }

    @Test
    public void addExpeeToGraderFailNoTask() throws ExistException {
        try {
            //not exist grading task
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), -1,
                    grader.getGraderEmail(), expee.getExperimenteeEmail());
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader.getGraderEmail(),
                    db.getExperimenteeByEmail(expee.getExperimenteeEmail()).getParticipant().getParticipantId()));
        }
    }

    @Test
    public void addExpeeToGraderFailNoExp() throws ExistException {
        try {
            //not exist experiment
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), -1, task.getGradingTaskId(), grader.getGraderEmail(), expee.getExperimenteeEmail());
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader.getGraderEmail(),
                    db.getExperimenteeByEmail(expee.getExperimenteeEmail()).getParticipant().getParticipantId()));
        }
    }

    @Test
    public void addExpeeToGraderFailNoManager() throws ExistException {
        try {
            //not exist manager
            creatorBusiness.addExpeeToGrader("not exist", experiment.getExperimentId(), task.getGradingTaskId(),
                    grader.getGraderEmail(), expee.getExperimenteeEmail());
            Assert.fail();
        } catch (NotExistException ignored) {
            Assert.assertNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader.getGraderEmail(),
                    db.getExperimenteeByEmail(expee.getExperimenteeEmail()).getParticipant().getParticipantId()));
        }
    }

    @Test
    public void getExpeesOfTask() throws NotExistException, ExistException {
        int numExpees = creatorBusiness.getTaskExperimentees(manager.getBguUsername(), experiment.getExperimentId(),
                task.getGradingTaskId()).size();
        creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(),
                grader.getGraderEmail(), expee.getExperimenteeEmail());
        List<Participant> expees = creatorBusiness.getTaskExperimentees(manager.getBguUsername(), experiment.getExperimentId(),
                task.getGradingTaskId());
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
        long numTaskDB = db.getNumberOfGradingTasks();
        int numTasks = creatorBusiness.getGradingTasks(manager.getBguUsername(), experiment.getExperimentId()).size();
        creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "Another Grading Task", new ArrayList<>(), List.of(2), new ArrayList<>());
        Assert.assertEquals(numTasks + 1, creatorBusiness.getGradingTasks(manager.getBguUsername(), experiment.getExperimentId()).size());
        Assert.assertEquals(numTaskDB + 1, db.getNumberOfGradingTasks());
    }

}