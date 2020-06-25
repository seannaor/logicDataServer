package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToParticipant;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.Exceptions.ExistException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import com.example.demo.DBAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DataCache {

    /*
    This class purpose is to be the channel witch we can add or get data about the main entities.
    If data not found, we should look for it in the database.
     */

    @Autowired
    private DBAccess db;
    private List<ManagementUser> managers = new ArrayList<>();
    private List<Experimentee> experimentees = new ArrayList<>();
    private List<Grader> graders = new ArrayList<>();
    private List<GradingTask> gradingTasks = new ArrayList<>();
    private List<GraderToGradingTask> graderToGradingTasks = new ArrayList<>();
    private List<GraderToParticipant> graderToParticipants = new ArrayList<>();

    public void setCache() {
        managers = new ArrayList<>();
        addManager(new ManagementUser("ADMIN", "13579", "admin@post.bgu.ac.il"));
        experimentees = new ArrayList<>();
        graders = new ArrayList<>();
        gradingTasks = new ArrayList<>();
        graderToGradingTasks = new ArrayList<>();
        graderToParticipants = new ArrayList<>();
    }

    public ManagementUser getManagerByName(String name) throws NotExistException {
        for (ManagementUser manager : managers)
            if (manager.getBguUsername().equals(name)) return manager;
        ManagementUser manager = db.getManagementUserByName(name);
        if (manager != null) {
            managers.add(manager);
            return manager;
        }
        throw new NotExistException("user", name);
    }

    public ManagementUser getManagerByEMail(String email) throws NotExistException {
        for (ManagementUser manager : managers)
            if (manager.getUserEmail().equals(email)) return manager;
        ManagementUser manager = db.getManagementUserByEMail(email);
        if (manager != null) {
            managers.add(manager);
            return manager;
        }
        throw new NotExistException("user", email);
    }

    public Grader getGraderByEMail(String email) throws NotExistException {
        for (Grader grader : graders)
            if (grader.getGraderEmail().equals(email)) return grader;
        Grader grader = db.getGraderByEmail(email);
        if (grader != null) {
            graders.add(grader);
            return grader;
        }
        throw new NotExistException("grader", email);
    }

    public Grader getGraderByCode(UUID code) throws CodeException {
        for (GraderToGradingTask g2gt : graderToGradingTasks)
            if (g2gt.getGraderAccessCode().equals(code)) return g2gt.getGrader();
        GraderToGradingTask g2gt = db.getGraderToGradingTaskByCode(code);
        if (g2gt != null) {
            graderToGradingTasks.add(g2gt);
            return g2gt.getGrader();
        }
        throw new CodeException(code.toString());
    }

    public GraderToGradingTask getTaskByCode(UUID code) throws CodeException {
        for (GraderToGradingTask g2gt : graderToGradingTasks)
            if (g2gt.getGraderAccessCode().equals(code)) return g2gt;
        GraderToGradingTask g = db.getGraderToGradingTaskByCode(code);
        if (g != null) {
            graderToGradingTasks.add(g);
            return g;
        }
        throw new CodeException(code.toString());
    }

    public Experimentee getExpeeByEMail(String email) throws NotExistException {
        for (Experimentee expee : experimentees)
            if (expee.getExperimenteeEmail().equals(email)) return expee;
        Experimentee expee = db.getExperimenteeByEmail(email);
        if (expee != null) {
            experimentees.add(expee);
            return expee;
        }
        throw new NotExistException("experimentee", email);
    }

    public Experimentee getExperimenteeById(int id) throws NotExistException {
        for (Experimentee experimentee : this.experimentees) {
            if(experimentee.getParticipant().getParticipantId()==id) return experimentee;
        }
        Experimentee expee = db.getExperimenteeByParticipantId(id);
        if (expee != null) {
            experimentees.add(expee);
            return expee;
        }
        throw new NotExistException("participant", id+"");
    }

    public Experimentee getExpeeByCode(UUID code) throws CodeException {
        for (Experimentee expee : experimentees)
            if (expee.getAccessCode().equals(code)) return expee;
        Experimentee expee = db.getExperimenteeByCode(code);
        if (expee != null) {
            experimentees.add(expee);
            return expee;
        }
        throw new CodeException(code.toString());
    }

    public Experimentee getExpeeByMailAndExp(String email, int expId) throws NotExistException {
        for (Experimentee expee : experimentees)
            if (expee.getExperimenteeEmail().equals(email) &&
                    expee.getExperiment().getExperimentId() == expId)
                return expee;
        Experimentee expee = db.getExperimenteeByEmailAndExp(email, expId);
        if (expee != null) {
            experimentees.add(expee);
            return expee;
        }
        throw new NotExistException("experimentee", email);
    }

    public GradingTask getGradingTaskById(String researcherName, int expId, int id) throws NotExistException {
        ManagementUser man = getManagerByName(researcherName);
        Experiment exp = man.getExperiment(expId);
        for (GradingTask gt : gradingTasks) {
            if (gt.getBaseExperiment().equals(exp) && gt.getGradingTaskId() == id)
                return gt;
        }
        GradingTask gt = db.getGradingTaskById(id);
        if (gt != null) {
            gradingTasks.add(gt);
            return gt;
        }
        throw new NotExistException("grading task", "" + id);
    }

    public List<GradingTask> getAllGradingTasks(String researcherName, int expId) throws NotExistException {
        ManagementUser man = getManagerByName(researcherName);
        Experiment exp = man.getExperiment(expId);
        List<GradingTask> ret = new ArrayList<>();
        // no need to go over cache because anyway we need to go over all grading tasks in db to insure we are not missing some in cache
        for (GradingTask gt : db.getAllGradingTasks()) {
            if (gt.getBaseExperiment().getExperimentId() == exp.getExperimentId())
                ret.add(gt);
        }
        return ret;
        // TODO: maybe change db.getAllGradingTasks() to something like db.getGradingTasks(int baseId)
    }

    public GraderToGradingTask getGraderToGradingTask(Grader grader, GradingTask gradingTask) throws NotExistException {
        for (GraderToGradingTask g : graderToGradingTasks) {
            if (g.getGraderToGradingTaskID().getGraderEmail().equals(grader.getGraderEmail()) && g.getGraderToGradingTaskID().getGradingTaskId() == gradingTask.getGradingTaskId())
                return g;
        }
        GraderToGradingTask g2gt = db.getGraderToGradingTaskById(gradingTask.getGradingTaskId(), grader.getGraderEmail());
        if (g2gt != null) {
            graderToGradingTasks.add(g2gt);
            return g2gt;
        }
        throw new NotExistException("grading task", "" + gradingTask.getGradingTaskId());
    }

    public GraderToParticipant getGraderToParticipants(GraderToGradingTask graderToGradingTask, Participant participant) {
        for (GraderToParticipant g : graderToParticipants) {
            if (g.getGraderToGradingTask().equals(graderToGradingTask) && g.getExpeeParticipant().equals(participant)) {
                return g;
            }
        }
        GraderToParticipant g = db.getGraderToParticipantById(graderToGradingTask.getGradingTask().getGradingTaskId(), graderToGradingTask.getGrader().getGraderEmail(), participant.getParticipantId());
        if (g != null) {
            graderToParticipants.add(g);
            return g;
        }
        return null;
    }

//    public GradingTask getGradingTaskByName(String researcherName, int expId, int taskId) throws NotExistException {
//        ManagementUser man = getManagerByName(researcherName);
//        Experiment exp = man.getExperiment(expId);
//        for (GradingTask gt : gradingTasks) {
//            if (gt.getBaseExperiment().equals(exp) && gt.getGradingTaskId() == taskId)
//                return gt;
//        }
//        throw new NotExistException("grading task", ""+taskId);
//    }

    //=======================================================================

    public void addManager(ManagementUser manager) {
        for (ManagementUser m : managers)
            if (m.getBguUsername().equals(manager.getBguUsername()))
                return;
        db.saveManagementUser(manager);
        managers.add(manager);
    }

    public void addGrader(Grader grader) {
        for (Grader g : graders)
            if (g.getGraderEmail().equals(grader.getGraderEmail()))
                return;
        db.saveGrader(grader);
        graders.add(grader);
    }

    public void addExperimentee(Experimentee expee) {
        db.saveExperimentee(expee);
        experimentees.add(expee);
    }

    public void addGradingTask(GradingTask gt) {
        for (GradingTask g : gradingTasks)
            if (g.getGradingTaskId() == gt.getGradingTaskId())
                return;
        db.saveGradingTask(gt);
        gradingTasks.add(gt);
    }

    public UUID addGraderToGradingTask(GradingTask gt, Grader g) throws ExistException {
        for (GraderToGradingTask g2gt : graderToGradingTasks)
            if (g2gt.getGrader().getGraderEmail().equals(g.getGraderEmail()) && g2gt.getGradingTask().getGradingTaskId() == gt.getGradingTaskId())
                throw new ExistException("grader " + g.getGraderEmail(), "grading task " + gt.getGradingTaskName());
        GraderToGradingTask gtgt = new GraderToGradingTask(gt, g);
        UUID id = UUID.randomUUID();
        gtgt.setGraderAccessCode(id);
        db.saveGraderToGradingTask(gtgt);
        graderToGradingTasks.add(gtgt);
        return id;
        //TODO: maybe should add new one after checking that there's no GraderToGradingTask with gt and g
    }

    public void addExpeeToGradingTask(GradingTask gt, Grader grader, GraderToParticipant participantInGradingTask) {
        for (GraderToParticipant g : graderToParticipants)
            if (g.getExpeeParticipant().getParticipantId() == participantInGradingTask.getExpeeParticipant().getParticipantId() &&
                    g.getGraderToGradingTask().getGrader().getGraderEmail().equals(participantInGradingTask.getGraderToGradingTask().getGrader().getGraderEmail())
                    && g.getGraderToGradingTask().getGradingTask().getGradingTaskId() == participantInGradingTask.getGraderToGradingTask().getGradingTask().getGradingTaskId()) {
                return;
            }
        db.saveGraderToParticipant(participantInGradingTask);
        graderToParticipants.add(participantInGradingTask);
    }

    //=======================================================================

    public boolean isExpeeInExperiment(String email, int expId) {
        for (Experimentee expee : db.getAllExperimentees()) {
            if (expee.getExperimenteeEmail().equals(email) && expee.getExperiment().getExperimentId() == expId)
                return true;
        }
        return false;
    }

    public boolean isGraderInTask(String email, int expId, int taskId) {
        return false;
    }

    //Update cache data when changes were saved to db

//    public void updateManagementUser (ManagementUser m) {
//        for(ManagementUser mu : managers) {
//            if(mu.getBguUsername().equals(m.getBguUsername())) {
//                managers.remove(mu);
//                managers.add(m);
//            }
//        }
//    }
//    public void updateGrader (Grader g) {
//        for(Grader current : graders) {
//            if(current.getGraderEmail().equals(g.getGraderEmail())) {
//                graders.remove(current);
//                graders.add(g);
//            }
//        }
//    }
//    public void updateGradingTask (GradingTask gt) {
//        for(GradingTask current : gradingTasks) {
//            if(current.getGradingTaskId() == gt.getGradingTaskId()) {
//                gradingTasks.remove(current);
//                gradingTasks.add(gt);
//            }
//        }
//    }
}
