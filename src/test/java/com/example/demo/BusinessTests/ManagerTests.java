package com.example.demo.BusinessTests;

import com.example.demo.BusinessLayer.CreatorBusiness;
import com.example.demo.BusinessLayer.DataCache;
import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Exceptions.ExistException;
import com.example.demo.BusinessLayer.Exceptions.FormatException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import com.example.demo.DBAccess;
import com.example.demo.Utils;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql({"/create_database.sql"})
@SpringBootTest
public class ManagerTests {

    private CreatorBusiness creatorBusiness;
    private DataCache cache;
    private DBAccess db;
    private ManagementUser manager;
    private ManagementUser ally;
    private Experiment experiment;
    private Experimentee expee;
    private GradingTask task;
    private Grader grader;
    private String graderCode;
    @Autowired
    public ManagerTests(CreatorBusiness creatorBusiness, DataCache cache, DBAccess db) {
        this.creatorBusiness = creatorBusiness;
        this.cache = cache;
        this.db = db;
    }

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
        assertFalse(creatorBusiness.researcherLogin(manager.getBguUsername(), "not the password"));

        // real password - should pass
        assertTrue(creatorBusiness.researcherLogin(manager.getBguUsername(), manager.getBguPassword()));
        assertEquals(db.getManagementUserByName(manager.getBguUsername()).getBguPassword(), manager.getBguPassword());

        // username not exist - should fail
        assertFalse(creatorBusiness.researcherLogin("some not exist username", "a password"));
    }

    @Test
    @Transactional
    public void setAlliesPermissions() throws NotExistException {
        //TODO: fix, and validate currentPerms (not sure we will have 2 at the end)
        int numAllies = creatorBusiness.getAllies(manager.getBguUsername(), experiment.getExperimentId()).size();
        long currentPerms = db.getNumberOfPermissions();

        creatorBusiness.setAlliePermissions(manager.getBguUsername(), experiment.getExperimentId(), ally.getUserEmail(),
                "view", List.of("PERMISSION"));
        assertEquals(cache.getManagerByEMail(ally.getUserEmail()).getPermissions().size(), 1);
        assertEquals(db.getNumberOfPermissions(), currentPerms + 1);

        creatorBusiness.setAlliePermissions(manager.getBguUsername(), experiment.getExperimentId(), ally.getUserEmail(),
                "edit", List.of("PERMISSION", "ADMIN?"));
        assertEquals(cache.getManagerByEMail(ally.getUserEmail()).getPermissions().size(), 2);
        assertEquals(db.getNumberOfPermissions(), currentPerms + 2);

        assertEquals(numAllies + 2, creatorBusiness.getAllies(manager.getBguUsername(), experiment.getExperimentId()).size());
    }

    @Test
    public void setAllyPermissionsFailNoAlly() {
        String notExistAlly = "no one";
        assertThrows(NotExistException.class, () -> {
            creatorBusiness.setAlliePermissions(manager.getBguUsername(), experiment.getExperimentId(), notExistAlly,
                    "view", List.of("PERMISSION"));
        });
    }

    @Test
    @Transactional
    public void setPermissionsNoExp() throws NotExistException {
        int numAllies = creatorBusiness.getAllies(manager.getBguUsername(), experiment.getExperimentId()).size();
        long currentPerms = db.getNumberOfPermissions();
        assertThrows(NotExistException.class, () -> {
            //not exist experiment
            creatorBusiness.setAlliePermissions(manager.getBguUsername(), -1, ally.getUserEmail(), "edit", List.of("PERMISSION"));
        });
        assertEquals(currentPerms, db.getNumberOfPermissions());
        assertEquals(numAllies, creatorBusiness.getAllies(manager.getBguUsername(), experiment.getExperimentId()).size());
    }

    @Test
    @Transactional
    public void setPermissionsNoManager() throws NotExistException {
        int numAllies = creatorBusiness.getAllies(manager.getBguUsername(), experiment.getExperimentId()).size();
        long currentPerms = db.getNumberOfPermissions();
        assertThrows(NotExistException.class, () -> {
            //not exist manager
            creatorBusiness.setAlliePermissions("not exist", experiment.getExperimentId(), ally.getUserEmail(), "view", List.of("PERMISSION"));
        });
        assertEquals(db.getNumberOfPermissions(), currentPerms);
        assertEquals(numAllies, creatorBusiness.getAllies(manager.getBguUsername(), experiment.getExperimentId()).size());
    }

    @Test
    @Transactional
    public void addAlly() throws NotExistException, ExistException {
        String allyMail = "other@post";
        long managerCount = db.getNumberOfManagers();
        assertFalse(creatorBusiness.researcherLogin(allyMail, "TEMP"));
        creatorBusiness.addAlly(manager.getBguUsername(), allyMail, List.of("PERMISSION"));
        assertTrue(creatorBusiness.researcherLogin(allyMail, "TEMP"));
        assertEquals(managerCount + 1, db.getNumberOfManagers());
    }

    @Test
    public void addAllyFailNoManager() {
        long managerCount = db.getNumberOfManagers();
        assertThrows(NotExistException.class, () -> {
            creatorBusiness.addAlly("not exist username", "ally@post", List.of("PERMISSION"));
        });
        assertNull(db.getManagementUserByEMail("ally@post"));
        assertEquals(managerCount, db.getNumberOfManagers());
    }

    @Test
    @Transactional
    public void addAllyFailSameAlly() {
        long managerCount = db.getNumberOfManagers();
        assertThrows(ExistException.class, () -> {
            creatorBusiness.addAlly(manager.getBguUsername(), ally.getUserEmail(), List.of("PERMISSION"));
        });
        assertEquals(managerCount, db.getNumberOfManagers());
    }

    @Test
    @Transactional
    public void addAllExperiment() throws NotExistException, FormatException, ExistException {
        String expName = "testExp";
        List<JSONObject> stages = Utils.buildStages();

        //new experiment should pass
        int expNum = manager.getManagementUserToExperiments().size();
        int expId = creatorBusiness.addExperiment(manager.getBguUsername(), expName, stages);
        assertEquals(expNum + 1, manager.getManagementUserToExperiments().size());
        assertEquals(expName, db.getExperimentById(expId).getExperimentName());
        Experiment exp = manager.getExperimentByName(expName);
        assertTrue(exp.containsManger(manager));
        assertTrue(db.getExperimentById(expId).containsManger(manager));
        assertEquals(expNum + 1, manager.getManagementUserToExperiments().size());
    }

    @Test
    public void addAllExperimentSameName() {
        long currentExps = db.getNumberOfExperiments();
        int expNum = manager.getManagementUserToExperiments().size();
        assertThrows(ExistException.class, () -> {
            //same name should fail
            creatorBusiness.addExperiment(manager.getBguUsername(), experiment.getExperimentName(), new ArrayList<>());
        });
        assertEquals(currentExps, db.getNumberOfExperiments());
        assertEquals(expNum, manager.getManagementUserToExperiments().size());
    }

    @Test
    public void addAllExperimentNoManger() {
        long currentExps = db.getNumberOfExperiments();
        int expNum = manager.getManagementUserToExperiments().size();
        assertThrows(NotExistException.class, () -> {
            //creator name not exist should fail
            creatorBusiness.addExperiment("not exist name", "new exp", new ArrayList<>());
        });
        assertEquals(expNum, manager.getManagementUserToExperiments().size());
        assertEquals(currentExps, db.getNumberOfExperiments());
    }

    @Test
    @Transactional
    public void createExperiment() throws NotExistException, ExistException {
        String expName = "new exp";
        int experimentsCount = creatorBusiness.getExperiments(manager.getBguUsername()).size();
        //new experiment should pass
        int expNum = manager.getManagementUserToExperiments().size();

        int expId = creatorBusiness.createExperiment(manager.getBguUsername(), expName);
        assertEquals(expNum + 1, manager.getManagementUserToExperiments().size());
        assertEquals(experimentsCount + 1, creatorBusiness.getExperiments(manager.getBguUsername()).size());
        assertEquals(expName, db.getExperimentById(expId).getExperimentName());
        Experiment exp = manager.getExperimentByName(expName);
        assertTrue(exp.containsManger(manager));
        assertTrue(db.getExperimentById(expId).containsManger(manager));
        assertEquals(0, exp.getStages().size());
        assertEquals(0, db.getExperimentById(expId).getStages().size());
        assertEquals(0, creatorBusiness.getStages(manager.getBguUsername(), expId).size());
    }

    @Test
    public void createExperimentSameName() {
        long currentExps = db.getNumberOfExperiments();
        assertThrows(ExistException.class, () -> {
            //same name should fail
            creatorBusiness.createExperiment(manager.getBguUsername(), experiment.getExperimentName());
        });
        assertEquals(currentExps, db.getNumberOfExperiments());
    }

    @Test
    public void createExperimentNoManager() {
        long currentExps = db.getNumberOfExperiments();
        assertThrows(NotExistException.class, () -> {
            //creator name not exist should fail
            creatorBusiness.createExperiment("not exist name", "new exp");
        });
        assertEquals(currentExps, db.getNumberOfExperiments());
    }

    @Test
    @Transactional
    public void addStage() throws FormatException, NotExistException {
        // adding legal stages should pass
        int initstagesNum = experiment.getStages().size();
        for (JSONObject jStage : Utils.buildStages()) {
            creatorBusiness.addStageToExperiment(manager.getBguUsername(), experiment.getExperimentId(), jStage);
            assertEquals(++initstagesNum, db.getExperimentById(experiment.getExperimentId()).getStages().size());
            assertEquals(initstagesNum, experiment.getStages().size());
        }
    }

    @Test
    public void addIllegalStage() {
        long currentStages = db.getNumberOfStages();
        assertThrows(FormatException.class, () -> {
            //not a valid stage
            creatorBusiness.addStageToExperiment(manager.getBguUsername(), experiment.getExperimentId(), new JSONObject());
        });
        assertEquals(currentStages, db.getNumberOfStages());
    }

    @Test
    public void addStageNoExp() {
        long currentStages = db.getNumberOfStages();
        assertThrows(NotExistException.class, () -> {
            //experiment id not exist
            creatorBusiness.addStageToExperiment(manager.getBguUsername(), -1, Utils.getStumpInfoStage());
        });
        assertEquals(currentStages, db.getNumberOfStages());
    }

    @Test
    public void addStageNoManager() {
        long currentStages = db.getNumberOfStages();
        assertThrows(NotExistException.class, () -> {
            //creator name not exist
            creatorBusiness.addStageToExperiment("not exist name", experiment.getExperimentId(), Utils.getStumpInfoStage());
        });
        assertEquals(currentStages, db.getNumberOfStages());
    }

    @Test
    @Transactional
    public void addGradingTask() throws NotExistException, FormatException {
        long currentGT = db.getNumberOfGradingTasks();
        int experimentsCount = creatorBusiness.getExperiments(manager.getBguUsername()).size();

        int id = creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(),
                "grading task", new ArrayList<>(), List.of(2), new ArrayList<>());
        assertEquals(currentGT + 1, db.getNumberOfGradingTasks());
        assertEquals(experimentsCount, creatorBusiness.getExperiments(manager.getBguUsername()).size());
        assertNotNull(db.getGradingTaskById(id));
    }

    @Test
    public void addGradingTaskIllegalIndexes() {
        long currentGT = db.getNumberOfGradingTasks();
        assertThrows(NotExistException.class, () -> {
            //illegal indexes to check
            creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "grading task", new ArrayList<>(), List.of(-1, -2, -3), new ArrayList<>());
        });
        assertEquals(currentGT, db.getNumberOfGradingTasks());
    }

    @Test
    public void addGradingTaskIllegalSubExp() {
        long currentGT = db.getNumberOfGradingTasks();
        assertThrows(FormatException.class, () -> {
            //Illegal personal & result experiments
            List<JSONObject> stagesIllegal = List.of(new JSONObject());
            creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "grading task", stagesIllegal, new ArrayList<>(), stagesIllegal);
        });
        assertEquals(currentGT, db.getNumberOfGradingTasks());
    }

    @Test
    public void addGradingTaskNoExp() {
        long currentGT = db.getNumberOfGradingTasks();
        assertThrows(NotExistException.class, () -> {
            //not exist experiment
            creatorBusiness.addGradingTask(manager.getBguUsername(), -1, "grading task", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        });
        assertEquals(currentGT, db.getNumberOfGradingTasks());
    }

    @Test
    public void addGradingTaskNoManager() {
        long currentGT = db.getNumberOfGradingTasks();
        assertThrows(NotExistException.class, () -> {
            //not exist manager
            creatorBusiness.addGradingTask("not exist", experiment.getExperimentId(), "grading task", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        });
        assertEquals(db.getNumberOfGradingTasks(), currentGT);
    }

    @Test
    @Transactional
    public void addStageToPersonalTask() throws NotExistException, FormatException {
        long currentStages = db.getNumberOfStages();

        int before = task.getGeneralExperiment().getStages().size();
        assertEquals(before, creatorBusiness.getPersonalStages(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId()).size());
        creatorBusiness.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), Utils.getStumpInfoStage());
        int after = task.getGeneralExperiment().getStages().size();
        assertEquals(before + 1, after);
        assertEquals(after, creatorBusiness.getPersonalStages(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId()).size());
        assertEquals(currentStages + 1, db.getNumberOfStages());
    }

    @Test
    @Transactional
    public void addStageToGradingTask() throws NotExistException, FormatException {
        //addToPersonal and addToResultsExp are the same so testing on personal exp is sufficient
        long currentStages = db.getNumberOfStages();

        int before = task.getGradingExperiment().getStages().size();
        assertEquals(before, creatorBusiness.getEvaluationStages(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId()).size());
        creatorBusiness.addToResultsExp(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), Utils.getStumpInfoStage());
        int after = task.getGradingExperiment().getStages().size();
        assertEquals(before + 1, after);
        assertEquals(after, creatorBusiness.getEvaluationStages(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId()).size());
        assertEquals(currentStages + 1, db.getNumberOfStages());
    }

    @Test
    public void addIllegalStageToGradingTaskFail() {
        long currentStages = db.getNumberOfStages();
        assertThrows(FormatException.class, () -> {
            //illegal stage
            creatorBusiness.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), new JSONObject());
        });
        assertEquals(currentStages, db.getNumberOfStages());
    }

    @Test
    public void addStageToGradingTaskFailNoTask() {
        long currentStages = db.getNumberOfStages();
        assertThrows(NotExistException.class, () -> {
            //not exist grading task
            creatorBusiness.addToPersonal(manager.getBguUsername(), experiment.getExperimentId(), -1, Utils.getStumpInfoStage());
        });
        assertEquals(currentStages, db.getNumberOfStages());
    }

    @Test
    public void addStageToGradingTaskFailNoExp() {
        long currentStages = db.getNumberOfStages();
        assertThrows(NotExistException.class, () -> {
            //not exist experiment
            creatorBusiness.addToPersonal(manager.getBguUsername(), -1, task.getGradingTaskId(), Utils.getStumpInfoStage());
        });
        assertEquals(currentStages, db.getNumberOfStages());
    }

    @Test
    public void addStageToGradingTaskFailNoManager() {
        long currentStages = db.getNumberOfStages();
        assertThrows(NotExistException.class, () -> {
            //not exist manager
            creatorBusiness.addToPersonal("not exist", experiment.getExperimentId(), task.getGradingTaskId(), Utils.getStumpInfoStage());
        });
        assertEquals(currentStages, db.getNumberOfStages());
    }

    @Test
    @Transactional
    public void setStagesToCheck() throws NotExistException, FormatException {
        assertEquals(0, task.getStages().size());
        creatorBusiness.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), List.of(1));
        assertEquals(1, task.getStages().size());
        assertEquals(1, db.getGradingTaskById(task.getGradingTaskId()).getStages().size());
    }

    @Test
    @Transactional
    public void setIllegalStagesToCheckFail() {
        assertThrows(NotExistException.class, () -> {
            //illegal stage
            creatorBusiness.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), List.of(-2));
        });
        assertEquals(0, db.getGradingTaskById(task.getGradingTaskId()).getStages().size());
    }

    @Test
    @Transactional
    public void setInfoStageToCheckFail() {
        assertThrows(FormatException.class, () -> {
            //illegal stage
            creatorBusiness.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), List.of(0));
        });
        assertEquals(0, db.getGradingTaskById(task.getGradingTaskId()).getStages().size());
    }

    @Test
    @Transactional
    public void setStagesToCheckFailNoTask() {
        assertThrows(NotExistException.class, () -> {
            //not exist grading task
            creatorBusiness.setStagesToCheck(manager.getBguUsername(), experiment.getExperimentId(), -1, List.of(1));
        });
        assertEquals(0, db.getGradingTaskById(task.getGradingTaskId()).getStages().size());
    }

    @Test
    @Transactional
    public void setStagesToCheckFailNoExp() {
        assertThrows(NotExistException.class, () -> {
            //not exist experiment
            creatorBusiness.setStagesToCheck(manager.getBguUsername(), -1, task.getGradingTaskId(), List.of(1));
        });
        assertEquals(0, db.getGradingTaskById(task.getGradingTaskId()).getStages().size());
    }

    @Test
    @Transactional
    public void setStagesToCheckFailNoManager() {
        assertThrows(NotExistException.class, () -> {
            //not exist manager
            creatorBusiness.setStagesToCheck("not exist", experiment.getExperimentId(), task.getGradingTaskId(), List.of(1));
        });
        assertEquals(0, db.getGradingTaskById(task.getGradingTaskId()).getStages().size());
    }

    @Test
    @Transactional
    public void addExperimentee() throws NotExistException, ExistException {
        String expee_mail = "guy@post.bgu.ac.il";

        int experimenteesCount = creatorBusiness.getExperimentees(manager.getBguUsername(), experiment.getExperimentId()).size();

        creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), expee_mail);
        assertEquals(experimenteesCount + 1, creatorBusiness.getExperimentees(manager.getBguUsername(), experiment.getExperimentId()).size());
        assertTrue(cache.isExpeeInExperiment(expee_mail, experiment.getExperimentId()));
        assertNotNull(db.getExperimenteeByEmail(expee_mail));
    }

    @Test
    @Transactional
    public void addExperimentees() throws NotExistException, ExistException {
        List<String> mails = List.of("m1@post","m2@post");

        int experimenteesCount = creatorBusiness.getExperimentees(manager.getBguUsername(), experiment.getExperimentId()).size();

        creatorBusiness.addExperimentees(manager.getBguUsername(), experiment.getExperimentId(), mails);
        assertEquals(experimenteesCount + 2, creatorBusiness.getExperimentees(manager.getBguUsername(), experiment.getExperimentId()).size());
        assertTrue(cache.isExpeeInExperiment(mails.get(0), experiment.getExperimentId()));
        assertTrue(cache.isExpeeInExperiment(mails.get(1), experiment.getExperimentId()));
        assertNotNull(db.getExperimenteeByEmail(mails.get(0)));
        assertNotNull(db.getExperimenteeByEmail(mails.get(1)));
    }

    @Test
    public void addExperimenteeFailSameExpee() throws NotExistException {
        long currentExpees = db.getNumberOfExperimentees();
        int experimenteesCount = creatorBusiness.getExperimentees(manager.getBguUsername(), experiment.getExperimentId()).size();
        assertThrows(ExistException.class, () -> {
            //exist experimentee
            creatorBusiness.addExperimentee(manager.getBguUsername(), experiment.getExperimentId(), expee.getExperimenteeEmail());
        });
        assertEquals(experimenteesCount, creatorBusiness.getExperimentees(manager.getBguUsername(), experiment.getExperimentId()).size());
        assertEquals(currentExpees, db.getNumberOfExperimentees());
    }

    @Test
    public void addExperimenteesFailSameExpee() throws NotExistException {
        long currentExpees = db.getNumberOfExperimentees();
        int experimenteesCount = creatorBusiness.getExperimentees(
                manager.getBguUsername(), experiment.getExperimentId()).size();
        assertThrows(ExistException.class, () -> {
            //list contains two identical mails
            creatorBusiness.addExperimentees(manager.getBguUsername(), experiment.getExperimentId(),
                    List.of("mail1","mail1"));
        });
        assertEquals(experimenteesCount, creatorBusiness.getExperimentees(manager.getBguUsername(),
                experiment.getExperimentId()).size());
        assertEquals(currentExpees, db.getNumberOfExperimentees());
    }

    @Test
    public void addExperimenteeFailNoExp() throws NotExistException {
        String expee_mail = "guy@post.bgu.ac.il";
        int experimenteesCount = creatorBusiness.getExperimentees(manager.getBguUsername(), experiment.getExperimentId()).size();
        assertThrows(NotExistException.class, () -> {
            //not exist experiment
            creatorBusiness.addExperimentee(manager.getBguUsername(), -1, expee_mail);
        });
        assertEquals(experimenteesCount, creatorBusiness.getExperimentees(manager.getBguUsername(), experiment.getExperimentId()).size());
        assertNull(db.getExperimenteeByEmail(expee_mail));
    }

    @Test
    public void addExperimenteeFailNoManager() throws NotExistException {
        String expee_mail = "guy@post.bgu.ac.il";
        int experimenteesCount = creatorBusiness.getExperimentees(manager.getBguUsername(), experiment.getExperimentId()).size();
        assertThrows(NotExistException.class, () -> {
            //not exist manager
            creatorBusiness.addExperimentee("not exist", experiment.getExperimentId(), expee_mail);
        });
        assertEquals(experimenteesCount, creatorBusiness.getExperimentees(manager.getBguUsername(), experiment.getExperimentId()).size());
        assertNull(db.getExperimenteeByEmail(expee_mail));
    }

    @Test
    @Transactional
    public void addExpeeToGrader() throws NotExistException, ExistException {

        creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(),
                grader.getGraderEmail(), expee.getExperimenteeEmail());

        assertNotNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader.getGraderEmail(),
                db.getExperimenteeByEmail(expee.getExperimenteeEmail()).getParticipant().getParticipantId()));
    }

    @Test
    public void addExpeeToGraderFailSameExpee() throws NotExistException, ExistException {
        creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(),
                grader.getGraderEmail(), expee.getExperimenteeEmail());

        assertThrows(ExistException.class, () -> {
            //expee already in grader's participants
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(),
                    grader.getGraderEmail(), expee.getExperimenteeEmail());
        });
        assertNotNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader.getGraderEmail(),
                db.getExperimenteeByEmail(expee.getExperimenteeEmail()).getParticipant().getParticipantId()));
    }

    @Test
    public void addExpeeToGraderFailNoGrader() {
        assertThrows(NotExistException.class, () -> {
            //not exist grader
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(),
                    "not exist", expee.getExperimenteeEmail());
        });
        assertNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader.getGraderEmail(),
                db.getExperimenteeByEmail(expee.getExperimenteeEmail()).getParticipant().getParticipantId()));
    }

    @Test
    public void addExpeeToGraderFailNoExpee() {
        assertThrows(NotExistException.class, () -> {
            //not exist expee
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(),
                    grader.getGraderEmail(), "not exist");
        });
        assertNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader.getGraderEmail(),
                db.getExperimenteeByEmail(expee.getExperimenteeEmail()).getParticipant().getParticipantId()));
    }

    @Test
    public void addExpeeToGraderFailNoTask() {
        assertThrows(NotExistException.class, () -> {
            //not exist grading task
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), -1,
                    grader.getGraderEmail(), expee.getExperimenteeEmail());
        });
        assertNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader.getGraderEmail(),
                db.getExperimenteeByEmail(expee.getExperimenteeEmail()).getParticipant().getParticipantId()));
    }

    @Test
    public void addExpeeToGraderFailNoExp() {
        assertThrows(NotExistException.class, () -> {
            //not exist experiment
            creatorBusiness.addExpeeToGrader(manager.getBguUsername(), -1, task.getGradingTaskId(), grader.getGraderEmail(), expee.getExperimenteeEmail());
        });
        assertNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader.getGraderEmail(),
                db.getExperimenteeByEmail(expee.getExperimenteeEmail()).getParticipant().getParticipantId()));
    }

    @Test
    public void addExpeeToGraderFailNoManager() {
        assertThrows(NotExistException.class, () -> {
            //not exist manager
            creatorBusiness.addExpeeToGrader("not exist", experiment.getExperimentId(), task.getGradingTaskId(),
                    grader.getGraderEmail(), expee.getExperimenteeEmail());
        });
        assertNull(db.getGraderToParticipantById(task.getGradingTaskId(), grader.getGraderEmail(),
                db.getExperimenteeByEmail(expee.getExperimenteeEmail()).getParticipant().getParticipantId()));
    }

    @Test
    public void getExpeesOfTask() throws NotExistException, ExistException {
        int numExpees = creatorBusiness.getTaskExperimentees(manager.getBguUsername(), experiment.getExperimentId(),
                task.getGradingTaskId()).size();
        creatorBusiness.addExpeeToGrader(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(),
                grader.getGraderEmail(), expee.getExperimenteeEmail());
        List<Participant> expees = creatorBusiness.getTaskExperimentees(manager.getBguUsername(), experiment.getExperimentId(),
                task.getGradingTaskId());
        assertEquals(numExpees + 1, expees.size());

        for (Participant p : expees)
            if (p.getParticipantId() == expee.getParticipant().getParticipantId()) return;

        fail();
    }

    @Test
    public void getGraders() throws NotExistException, ExistException {
        int numGraders = creatorBusiness.getTaskGraders(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId()).size();
        creatorBusiness.addGraderToGradingTask(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId(), "grader@post");
        List<Grader> graders = creatorBusiness.getTaskGraders(manager.getBguUsername(), experiment.getExperimentId(), task.getGradingTaskId());
        assertEquals(numGraders + 1, graders.size());

        for (Grader g : graders)
            if (g.getGraderEmail().equals("grader@post")) return;

        fail();
    }

    @Test
    public void getGradersFailNoManager() {
        assertThrows(NotExistException.class, () -> {
            //not real manager
            creatorBusiness.getTaskGraders("not exist manager", experiment.getExperimentId(), task.getGradingTaskId());
        });
    }

    @Test
    public void getGradersFailNoExp() {
        assertThrows(NotExistException.class, () -> {
            //not real experiment
            creatorBusiness.getTaskGraders(manager.getBguUsername(), -1, task.getGradingTaskId());
        });
    }

    @Test
    public void getGradersFailNoTask() {
        assertThrows(NotExistException.class, () -> {
            //not real task
            creatorBusiness.getTaskGraders(manager.getBguUsername(), experiment.getExperimentId(), -1);
        });
    }

    @Test
    public void getGradingTasks() throws NotExistException, FormatException {
        long numTaskDB = db.getNumberOfGradingTasks();
        int numTasks = creatorBusiness.getGradingTasks(manager.getBguUsername(), experiment.getExperimentId()).size();
        creatorBusiness.addGradingTask(manager.getBguUsername(), experiment.getExperimentId(), "Another Grading Task", new ArrayList<>(), List.of(2), new ArrayList<>());
        assertEquals(numTasks + 1, creatorBusiness.getGradingTasks(manager.getBguUsername(), experiment.getExperimentId()).size());
        assertEquals(numTaskDB + 1, db.getNumberOfGradingTasks());
    }


}