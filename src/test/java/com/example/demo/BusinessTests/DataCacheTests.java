package com.example.demo.BusinessTests;

import com.example.demo.BusinessLayer.DataCache;
import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradersGTToParticipants;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import com.example.demo.DBAccess;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

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
        db.saveManagementUser(new ManagementUser("ADMIN","13579", "admin@post.bgu.ac.il"));
    }

    @Test
    void getManagerByNameTest() {
        try {
            cache.getManagerByName("sean");
        }
        catch (NotExistException ok) { }
        ManagementUser m = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(m);
        try {
            ManagementUser fromCache = cache.getManagerByName("sean");
            Assert.assertEquals(db.getManagementUserByName("sean").getUserEmail(), fromCache.getUserEmail());
        }
        catch (NotExistException e) { Assert.fail(); }
        cache.setCache();
        try {
            ManagementUser fromCache = cache.getManagerByName("sean");
            Assert.assertEquals(db.getManagementUserByName("sean").getUserEmail(), fromCache.getUserEmail());
        }
        catch (NotExistException e) { Assert.fail(); }
    }

    @Test
    void getManagerByEMailTest() {
        try {
            cache.getManagerByEMail("a@a.a");
        }
        catch (NotExistException ok) { }
        ManagementUser m = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(m);
        try {
            ManagementUser fromCache = cache.getManagerByEMail("a@a.a");
            Assert.assertEquals(db.getManagementUserByEMail("a@a.a").getBguUsername(), fromCache.getBguUsername());
        }
        catch (NotExistException e) { Assert.fail(); }
        cache.setCache();
        try {
            ManagementUser fromCache = cache.getManagerByEMail("a@a.a");
            Assert.assertEquals(db.getManagementUserByEMail("a@a.a").getBguUsername(), fromCache.getBguUsername());
        }
        catch (NotExistException e) { Assert.fail(); }
    }

    @Test
    void getGraderByEMailTest() {
        try {
            cache.getGraderByEMail("a@a.a");
        }
        catch (NotExistException ok) { }
        ManagementUser creator = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(creator);
        Experiment exp = new Experiment("hi");
        db.saveExperiment(exp, creator);
        Grader g = new Grader("a@a.a", exp);
        cache.addGrader(g);
        try {
            Grader fromCache = cache.getGraderByEMail("a@a.a");
            Assert.assertEquals(db.getGraderByEmail("a@a.a").getGraderEmail(), fromCache.getGraderEmail());
        }
        catch (NotExistException e) { Assert.fail(); }
        cache.setCache();
        try {
            Grader fromCache = cache.getGraderByEMail("a@a.a");
            Assert.assertEquals(db.getGraderByEmail("a@a.a").getGraderEmail(), fromCache.getGraderEmail());
        }
        catch (NotExistException e) { Assert.fail(); }
    }
    @Test
    void getGraderByCodeTest() {
        try {
            cache.getGraderByCode("123");
        }
        catch (CodeException ok) { }
        ManagementUser creator = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(creator);
        Experiment exp = new Experiment("hi");
        db.saveExperiment(exp, creator);
        Grader g = new Grader("a@a.a", exp);
        cache.addGrader(g);
        Experiment gradingExp = new Experiment("gradingExp");
        db.saveExperiment(gradingExp, creator);
        GradingTask gt = new GradingTask("test", exp, null, gradingExp);
        cache.addGradingTask(gt);
        cache.addGraderToGradingTask(gt , g, "123");
        try {
            Grader fromCache = cache.getGraderByCode("123");
            Assert.assertEquals(db.getGraderToGradingTaskByCode("123").getGrader().getGraderEmail(), fromCache.getGraderEmail());
        }
        catch (CodeException e) { Assert.fail(); }
        cache.setCache();
        try {
            Grader fromCache = cache.getGraderByCode("123");
            Assert.assertEquals(db.getGraderToGradingTaskByCode("123").getGrader().getGraderEmail(), fromCache.getGraderEmail());
        }
        catch (CodeException e) { Assert.fail(); }
    }
    @Test
    void getG2GTByCodeTest() {
        try {
            cache.getTaskByCode("123");
        }
        catch (CodeException ok) { }
        ManagementUser creator = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(creator);
        Experiment exp = new Experiment("hi");
        db.saveExperiment(exp, creator);
        Grader g = new Grader("a@a.a", exp);
        cache.addGrader(g);
        Experiment gradingExp = new Experiment("gradingExp");
        db.saveExperiment(gradingExp, creator);
        GradingTask gt = new GradingTask("test", exp, null, gradingExp);
        cache.addGradingTask(gt);
        cache.addGraderToGradingTask(gt , g, "123");
        try {
            GraderToGradingTask fromCache = cache.getTaskByCode("123");
            Assert.assertEquals(db.getGraderToGradingTaskByCode("123").getGrader().getGraderEmail(), fromCache.getGrader().getGraderEmail());
        }
        catch (CodeException e) { Assert.fail(); }
        cache.setCache();
        try {
            GraderToGradingTask fromCache = cache.getTaskByCode("123");
            Assert.assertEquals(db.getGraderToGradingTaskByCode("123").getGrader().getGraderEmail(), fromCache.getGrader().getGraderEmail());
        }
        catch (CodeException e) { Assert.fail(); }
    }
    @Test
    void getExpeeByEMailTest() {
        try {
            cache.getExpeeByEMail("a@a.a");
        }
        catch (NotExistException ok) { }
        ManagementUser creator = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(creator);
        Experiment exp = new Experiment("hi");
        db.saveExperiment(exp, creator);
        Experimentee expee = new Experimentee("123", "a@a.a", exp);
        cache.addExperimentee(expee);
        try {
            Experimentee fromCache = cache.getExpeeByEMail("a@a.a");
            Assert.assertEquals(db.getExperimenteeByEmail("a@a.a").getAccessCode(), fromCache.getAccessCode());
        }
        catch (NotExistException e) { Assert.fail(); }
        cache.setCache();
        try {
            Experimentee fromCache = cache.getExpeeByEMail("a@a.a");
            Assert.assertEquals(db.getExperimenteeByEmail("a@a.a").getAccessCode(), fromCache.getAccessCode());
        }
        catch (NotExistException e) { Assert.fail(); }
    }
    @Test
    void getExpeeByCodeTest() {
        try {
            cache.getExpeeByCode("1");
        }
        catch (CodeException ok) { }
        ManagementUser creator = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(creator);
        Experiment exp = new Experiment("hi");
        db.saveExperiment(exp, creator);
        Experimentee expee = new Experimentee("123", "a@a.a", exp);
        cache.addExperimentee(expee);
        try {
            Experimentee fromCache = cache.getExpeeByCode("123");
            Assert.assertEquals(db.getExperimenteeByCode("123").getAccessCode(), fromCache.getAccessCode());
        }
        catch (CodeException e) { Assert.fail(); }
        cache.setCache();
        try {
            Experimentee fromCache = cache.getExpeeByCode("123");
            Assert.assertEquals(db.getExperimenteeByCode("123").getAccessCode(), fromCache.getAccessCode());
        }
        catch (CodeException e) { Assert.fail(); }
    }
    @Test
    void getExpeeByMailAndExpTest() {
        ManagementUser creator = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(creator);
        Experiment exp = new Experiment("hi");
        db.saveExperiment(exp, creator);
        try {
            cache.getExpeeByMailAndExp("a@a.a", exp.getExperimentId());
        }
        catch (NotExistException ok) { }
        Experimentee expee = new Experimentee("123", "a@a.a", exp);
        cache.addExperimentee(expee);
        try {
            Experimentee fromCache = cache.getExpeeByMailAndExp("a@a.a", exp.getExperimentId());
            Assert.assertEquals(db.getExperimenteeByEmailAndExp("a@a.a", exp.getExperimentId()).getAccessCode(), fromCache.getAccessCode());
        }
        catch (NotExistException e) { Assert.fail(); }
        cache.setCache();
        try {
            Experimentee fromCache = cache.getExpeeByMailAndExp("a@a.a", exp.getExperimentId());
            Assert.assertEquals(db.getExperimenteeByEmailAndExp("a@a.a", exp.getExperimentId()).getAccessCode(), fromCache.getAccessCode());
        }
        catch (NotExistException e) { Assert.fail(); }
    }
    @Test
    @Transactional
    void getGradingTaskByIdTest() {
        ManagementUser creator = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(creator);
        Experiment exp = new Experiment("hi", creator);
        db.saveExperiment(exp, creator);
        try {
            cache.getGradingTaskById("sean", exp.getExperimentId(), 1);
        } catch (NotExistException ok) { }
        Grader g = new Grader("a@a.a", exp);
        cache.addGrader(g);
        Experiment gradingExp = new Experiment("gradingExp", creator);
        db.saveExperiment(gradingExp, creator);
        GradingTask gt = new GradingTask("test", exp, null, gradingExp);
        cache.addGradingTask(gt);
        try {
            GradingTask fromCache = cache.getGradingTaskById("sean", exp.getExperimentId(), gt.getGradingTaskId());
            Assert.assertEquals(db.getGradingTaskById(gt.getGradingTaskId()).getGradingTaskId(), fromCache.getGradingTaskId());
        } catch (NotExistException e) {
            Assert.fail();
        }
        cache.setCache();
        try {
            GradingTask fromCache = cache.getGradingTaskById("sean", exp.getExperimentId(), gt.getGradingTaskId());
            Assert.assertEquals(db.getGradingTaskById(gt.getGradingTaskId()).getGradingTaskId(), fromCache.getGradingTaskId());
        } catch (NotExistException e) {
            Assert.fail();
        }
    }
    @Test
    @Transactional
    void getGradingToGradingTaskByIdTest() {
        ManagementUser creator = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(creator);
        Experiment exp = new Experiment("hi", creator);
        db.saveExperiment(exp, creator);
        try {
            cache.getGradingTaskById("sean", exp.getExperimentId(), 1);
        } catch (NotExistException ok) { }
        Grader g = new Grader("a@a.a", exp);
        cache.addGrader(g);
        Experiment gradingExp = new Experiment("gradingExp", creator);
        db.saveExperiment(gradingExp, creator);
        GradingTask gt = new GradingTask("test", exp, null, gradingExp);
        cache.addGradingTask(gt);
        cache.addGraderToGradingTask(gt , g, "123");
        try {
            GraderToGradingTask fromCache = cache.getGraderToGradingTask(g, gt);
            Assert.assertEquals(db.getGraderToGradingTaskById(gt.getGradingTaskId(), g.getGraderEmail()).getGraderAccessCode(), fromCache.getGraderAccessCode());
        } catch (NotExistException e) {
            Assert.fail();
        }
        cache.setCache();
        try {
            GraderToGradingTask fromCache = cache.getGraderToGradingTask(g, gt);
            Assert.assertEquals(db.getGraderToGradingTaskById(gt.getGradingTaskId(), g.getGraderEmail()).getGraderAccessCode(), fromCache.getGraderAccessCode());
        } catch (NotExistException e) {
            Assert.fail();
        }
    }
    @Test
    @Transactional
    void getGradersGTToParticipantsTest() throws NotExistException {
        ManagementUser creator = new ManagementUser("sean", "123", "a@a.a");
        cache.addManager(creator);
        Experiment exp = new Experiment("hi", creator);
        db.saveExperiment(exp, creator);
        try {
            cache.getGradingTaskById("sean", exp.getExperimentId(), 1);
        } catch (NotExistException ok) { }
        Grader g = new Grader("a@a.a", exp);
        cache.addGrader(g);
        Experiment gradingExp = new Experiment("gradingExp", creator);
        db.saveExperiment(gradingExp, creator);
        GradingTask gt = new GradingTask("test", exp, null, gradingExp);
        cache.addGradingTask(gt);
        cache.addGraderToGradingTask(gt , g, "123");
        Experimentee expee = new Experimentee("123", "a@a.a", exp);
        cache.addExperimentee(expee);
        GradersGTToParticipants participantInGT = new GradersGTToParticipants(cache.getGraderToGradingTask(g, gt), expee.getParticipant());
        cache.addExpeeToGradingTask(gt, g, participantInGT);
        try {
            GradersGTToParticipants fromCache = cache.getGradersGTToParticipants(cache.getGraderToGradingTask(g, gt), expee.getParticipant());
            Assert.assertEquals(db.getGradersGTToParticipantsById(gt.getGradingTaskId(), g.getGraderEmail(), expee.getParticipant().getParticipantId()).getParticipant().getParticipantId(), fromCache.getParticipant().getParticipantId());
        } catch (NotExistException e) {
            Assert.fail();
        }
        cache.setCache();
        try {
            GradersGTToParticipants fromCache = cache.getGradersGTToParticipants(cache.getGraderToGradingTask(g, gt), expee.getParticipant());
            Assert.assertEquals(db.getGradersGTToParticipantsById(gt.getGradingTaskId(), g.getGraderEmail(), expee.getParticipant().getParticipantId()).getParticipant().getParticipantId(), fromCache.getParticipant().getParticipantId());
        } catch (NotExistException e) {
            Assert.fail();
        }
    }
}
