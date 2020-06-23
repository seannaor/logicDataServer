package com.example.demo.BusinessTests;

import com.example.demo.BusinessLayer.DataCache;
import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToParticipant;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.Exceptions.ExistException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import com.example.demo.DBAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Sql({"/create_database.sql"})
@SpringBootTest
public class DataCacheTests {
    @Autowired
    private DBAccess db;
    @Autowired
    private DataCache cache;

    @BeforeEach
    void clean() {
        cache.setCache();
        db.deleteData();
        db.saveManagementUser(new ManagementUser("ADMIN", "13579", "admin@post.bgu.ac.il"));
    }

    @Test
    void getManagerByNameTest() {

        try {
            cache.getManagerByName("sean");
        } catch (NotExistException ok) {
        }
        ManagementUser m = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(m);
        try {
            ManagementUser fromCache = cache.getManagerByName("sean");
            assertEquals(db.getManagementUserByName("sean").getUserEmail(), fromCache.getUserEmail());
        } catch (NotExistException e) {
            fail();
        }
        cache.setCache();
        try {
            ManagementUser fromCache = cache.getManagerByName("sean");
            assertEquals(db.getManagementUserByName("sean").getUserEmail(), fromCache.getUserEmail());
        } catch (NotExistException e) {
            fail();
        }
    }

    @Test
    void getManagerByEMailTest() throws NotExistException {
        assertThrows(NotExistException.class, () -> {
            cache.getManagerByEMail("a@a.a");
        });
        ManagementUser m = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(m);

        ManagementUser fromCache = cache.getManagerByEMail("a@a.a");
        assertEquals(db.getManagementUserByEMail("a@a.a").getBguUsername(), fromCache.getBguUsername());
        cache.setCache();

        fromCache = cache.getManagerByEMail("a@a.a");
        assertEquals(db.getManagementUserByEMail("a@a.a").getBguUsername(), fromCache.getBguUsername());

    }

    @Test
    void getGraderByEMailTest() throws NotExistException {
        try {
            cache.getGraderByEMail("a@a.a");
        } catch (NotExistException ok) {
        }
        ManagementUser creator = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(creator);
        Experiment exp = new Experiment("hi");
        db.saveExperiment(exp, creator);
        Grader g = new Grader("a@a.a");
        cache.addGrader(g);
        Grader fromCache = cache.getGraderByEMail("a@a.a");
        assertEquals(db.getGraderByEmail("a@a.a").getGraderEmail(), fromCache.getGraderEmail());

        cache.setCache();

        fromCache = cache.getGraderByEMail("a@a.a");
        assertEquals(db.getGraderByEmail("a@a.a").getGraderEmail(), fromCache.getGraderEmail());
    }

    @Test
    void getGraderByCodeTest() throws NotExistException, ExistException, CodeException {
        try {
            cache.getGraderByCode(UUID.randomUUID());
        } catch (CodeException ok) {
        }
        ManagementUser creator = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(creator);
        Experiment exp = new Experiment("hi");
        db.saveExperiment(exp, creator);
        Grader g = new Grader("a@a.a");
        cache.addGrader(g);
        Experiment gradingExp = new Experiment("gradingExp");
        db.saveExperiment(gradingExp, creator);
        Experiment personalExp = new Experiment("personalExp", creator);
        db.saveExperiment(personalExp, creator);
        GradingTask gt = new GradingTask("test", exp, personalExp, gradingExp);
        cache.addGradingTask(gt);
        cache.addGraderToGradingTask(gt, g);
        UUID graderCode = cache.getGraderToGradingTask(g, gt).getGraderAccessCode();
        Grader fromCache = cache.getGraderByCode(graderCode);
        assertEquals(db.getGraderToGradingTaskByCode(graderCode).getGrader().getGraderEmail(), fromCache.getGraderEmail());
        cache.setCache();
        fromCache = cache.getGraderByCode(graderCode);
        assertEquals(db.getGraderToGradingTaskByCode(graderCode).getGrader().getGraderEmail(), fromCache.getGraderEmail());

    }

    @Test
    void getG2GTByCodeTest() throws NotExistException, ExistException, CodeException {
        try {
            cache.getTaskByCode(UUID.randomUUID());
        } catch (CodeException ok) {
        }
        ManagementUser creator = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(creator);
        Experiment exp = new Experiment("hi");
        db.saveExperiment(exp, creator);
        Grader g = new Grader("a@a.a");
        cache.addGrader(g);
        Experiment gradingExp = new Experiment("gradingExp");
        db.saveExperiment(gradingExp, creator);
        Experiment personalExp = new Experiment("personalExp", creator);
        db.saveExperiment(personalExp, creator);
        GradingTask gt = new GradingTask("test", exp, personalExp, gradingExp);
        cache.addGradingTask(gt);
        cache.addGraderToGradingTask(gt, g);
        UUID graderCode = cache.getGraderToGradingTask(g, gt).getGraderAccessCode();

        GraderToGradingTask fromCache = cache.getTaskByCode(graderCode);
        assertEquals(db.getGraderToGradingTaskByCode(graderCode).getGrader().getGraderEmail(), fromCache.getGrader().getGraderEmail());

        cache.setCache();

        fromCache = cache.getTaskByCode(graderCode);
        assertEquals(db.getGraderToGradingTaskByCode(graderCode).getGrader().getGraderEmail(), fromCache.getGrader().getGraderEmail());

    }

    @Test
    void getExpeeByEMailTest() throws NotExistException {
        try {
            cache.getExpeeByEMail("a@a.a");
        } catch (NotExistException ok) {
        }
        ManagementUser creator = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(creator);
        Experiment exp = new Experiment("hi");
        db.saveExperiment(exp, creator);
        Experimentee expee = new Experimentee("a@a.a", exp);
        cache.addExperimentee(expee);

        Experimentee fromCache = cache.getExpeeByEMail("a@a.a");
        assertEquals(db.getExperimenteeByEmail("a@a.a").getAccessCode(), fromCache.getAccessCode());

        cache.setCache();

        fromCache = cache.getExpeeByEMail("a@a.a");
        assertEquals(db.getExperimenteeByEmail("a@a.a").getAccessCode(), fromCache.getAccessCode());

    }

    @Test
    void getExpeeByCodeTest() throws CodeException {
        try {
            cache.getExpeeByCode(UUID.randomUUID());
        } catch (CodeException ok) {
        }
        ManagementUser creator = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(creator);
        Experiment exp = new Experiment("hi");
        db.saveExperiment(exp, creator);
        Experimentee expee = new Experimentee("a@a.a", exp);
        cache.addExperimentee(expee);

        Experimentee fromCache = cache.getExpeeByCode(expee.getAccessCode());
        assertEquals(db.getExperimenteeByCode(expee.getAccessCode()).getAccessCode(), fromCache.getAccessCode());

        cache.setCache();

        fromCache = cache.getExpeeByCode(expee.getAccessCode());
        assertEquals(db.getExperimenteeByCode(expee.getAccessCode()).getAccessCode(), fromCache.getAccessCode());

    }

    @Test
    void getExpeeByMailAndExpTest() throws NotExistException {
        ManagementUser creator = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(creator);
        Experiment exp = new Experiment("hi");
        db.saveExperiment(exp, creator);
        try {
            cache.getExpeeByMailAndExp("a@a.a", exp.getExperimentId());
        } catch (NotExistException ok) {
        }
        Experimentee expee = new Experimentee("a@a.a", exp);
        cache.addExperimentee(expee);

        Experimentee fromCache = cache.getExpeeByMailAndExp("a@a.a", exp.getExperimentId());
        assertEquals(db.getExperimenteeByEmailAndExp("a@a.a", exp.getExperimentId()).getAccessCode(), fromCache.getAccessCode());

        cache.setCache();

        fromCache = cache.getExpeeByMailAndExp("a@a.a", exp.getExperimentId());
        assertEquals(db.getExperimenteeByEmailAndExp("a@a.a", exp.getExperimentId()).getAccessCode(), fromCache.getAccessCode());

    }

    @Test
    @Transactional
    void getGradingTaskByIdTest() throws NotExistException {
        ManagementUser creator = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(creator);
        Experiment exp = new Experiment("hi", creator);
        db.saveExperiment(exp, creator);
        try {
            cache.getGradingTaskById("sean", exp.getExperimentId(), 1);
        } catch (NotExistException ok) {
        }
        Grader g = new Grader("a@a.a");
        cache.addGrader(g);
        Experiment gradingExp = new Experiment("gradingExp", creator);
        db.saveExperiment(gradingExp, creator);
        Experiment personalExp = new Experiment("personalExp", creator);
        db.saveExperiment(personalExp, creator);
        GradingTask gt = new GradingTask("test", exp, personalExp, gradingExp);
        cache.addGradingTask(gt);

        GradingTask fromCache = cache.getGradingTaskById("sean", exp.getExperimentId(), gt.getGradingTaskId());
        assertEquals(db.getGradingTaskById(gt.getGradingTaskId()).getGradingTaskId(), fromCache.getGradingTaskId());

        cache.setCache();

        fromCache = cache.getGradingTaskById("sean", exp.getExperimentId(), gt.getGradingTaskId());
        assertEquals(db.getGradingTaskById(gt.getGradingTaskId()).getGradingTaskId(), fromCache.getGradingTaskId());

    }

    @Test
    @Transactional
    void getGradingToGradingTaskByIdTest() throws ExistException, NotExistException {
        ManagementUser creator = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(creator);
        Experiment exp = new Experiment("hi", creator);
        db.saveExperiment(exp, creator);
        try {
            cache.getGradingTaskById("sean", exp.getExperimentId(), 1);
        } catch (NotExistException ok) {
        }
        Grader g = new Grader("a@a.a");
        cache.addGrader(g);
        Experiment gradingExp = new Experiment("gradingExp", creator);
        db.saveExperiment(gradingExp, creator);
        Experiment personalExp = new Experiment("personalExp", creator);
        db.saveExperiment(personalExp, creator);
        GradingTask gt = new GradingTask("test", exp, personalExp, gradingExp);
        cache.addGradingTask(gt);
        cache.addGraderToGradingTask(gt, g);
        GraderToGradingTask fromCache = cache.getGraderToGradingTask(g, gt);
        assertEquals(db.getGraderToGradingTaskById(gt.getGradingTaskId(), g.getGraderEmail()).getGraderAccessCode(), fromCache.getGraderAccessCode());
        cache.setCache();

        fromCache = cache.getGraderToGradingTask(g, gt);
        assertEquals(db.getGraderToGradingTaskById(gt.getGradingTaskId(), g.getGraderEmail()).getGraderAccessCode(), fromCache.getGraderAccessCode());

    }

    @Test
    @Transactional
    void getGraderToPArticipantTest() throws NotExistException, ExistException {
        ManagementUser creator = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(creator);
        Experiment exp = new Experiment("hi", creator);
        db.saveExperiment(exp, creator);
        try {
            cache.getGradingTaskById("sean", exp.getExperimentId(), 1);
        } catch (NotExistException ok) {
        }
        Grader g = new Grader("a@a.a");
        cache.addGrader(g);
        Experiment gradingExp = new Experiment("gradingExp", creator);
        db.saveExperiment(gradingExp, creator);
        Experiment personalExp = new Experiment("personalExp", creator);
        db.saveExperiment(personalExp, creator);
        GradingTask gt = new GradingTask("test", exp, personalExp, gradingExp);
        cache.addGradingTask(gt);
        cache.addGraderToGradingTask(gt, g);
        Experimentee expee = new Experimentee("a@a.a", exp);
        cache.addExperimentee(expee);
        GraderToParticipant participantInGT = new GraderToParticipant(cache.getGraderToGradingTask(g, gt), expee.getParticipant());
        cache.addExpeeToGradingTask(gt, g, participantInGT);

        GraderToParticipant fromCache = cache.getGraderToParticipants(cache.getGraderToGradingTask(g, gt), expee.getParticipant());
        assertEquals(db.getGraderToParticipantById(gt.getGradingTaskId(), g.getGraderEmail(), expee.getParticipant().getParticipantId()).getExpeeParticipant().getParticipantId(), fromCache.getExpeeParticipant().getParticipantId());

        cache.setCache();

        fromCache = cache.getGraderToParticipants(cache.getGraderToGradingTask(g, gt), expee.getParticipant());
        assertEquals(db.getGraderToParticipantById(gt.getGradingTaskId(), g.getGraderEmail(), expee.getParticipant().getParticipantId()).getExpeeParticipant().getParticipantId(), fromCache.getExpeeParticipant().getParticipantId());

    }
}
