package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.*;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradersGTToParticipants;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Exceptions.CodeException;
import com.example.demo.BusinessLayer.Exceptions.ExistException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;

import java.util.ArrayList;
import java.util.List;

public class DataCache {

    /*
    This class purpose is to be the channel witch we can add or get data about the main entities.
    If data not found, we should look for it in the database.
     */

    private static DataCache instance = null;
    private List<ManagementUser> managers;
    private List<Experimentee> experimentees;
    private List<Grader> graders;
    private List<GradingTask> gradingTasks;
    private List<GraderToGradingTask> graderToGradingTasks;
    private List<GradersGTToParticipants> gradersGTToParticipants;
    private List<ManagementUserToExperiment> managementUserToExperiments;


    private DataCache() {
        managers = new ArrayList<>();
        managers.add(new ManagementUser("ADMIN","13579", "admin@post.bgu.ac.il"));
        experimentees = new ArrayList<>();
        graders = new ArrayList<>();
        gradingTasks = new ArrayList<>();
        graderToGradingTasks = new ArrayList<>();
        gradersGTToParticipants = new ArrayList<>();
        managementUserToExperiments = new ArrayList<>();
    }

    public static DataCache getInstance() {
        if (instance == null)
            instance = new DataCache();

        return instance;
    }

    public ManagementUser getManagerByName(String name) throws NotExistException {
        for (ManagementUser manager : managers)
            if (manager.getBguUsername().equals(name)) return manager;
        throw new NotExistException("user", name);
    }

    public ManagementUser getManagerByEMail(String email) throws NotExistException {
        for (ManagementUser manager : managers)
            if (manager.getUserEmail().equals(email)) return manager;
        throw new NotExistException("user", email);
    }

    public Grader getGraderByEMail(String email) throws NotExistException {
        for (Grader grader : graders)
            if (grader.getGraderEmail().equals(email)) return grader;
        throw new NotExistException("grader", email);
    }

    public Grader getGraderByCode(String code) throws CodeException {
        for (GraderToGradingTask g2gt : graderToGradingTasks)
            if (g2gt.getGraderAccessCode().equals(code)) return g2gt.getGrader();
        throw new CodeException(code);
    }

    public GraderToGradingTask getTaskByCode(String code) throws CodeException {
        for (GraderToGradingTask g2gt : graderToGradingTasks)
            if (g2gt.getGraderAccessCode().equals(code)) return g2gt;
        throw new CodeException(code);
    }

    public Experimentee getExpeeByEMail(String email) throws NotExistException {
        for (Experimentee expee : experimentees)
            if (expee.getExperimenteeEmail().equals(email)) return expee;
        throw new NotExistException("experimentee", email);
    }

    public Experimentee getExpeeByCode(String code) throws CodeException {
        for (Experimentee expee : experimentees)
            if (expee.getAccessCode().equals(code)) return expee;
        throw new CodeException(code);
    }

    public Experimentee getExpeeByMailAndExp(String email, int expId) throws NotExistException {
        for (Experimentee expee : experimentees)
            if (expee.getExperimenteeEmail().equals(email) &&
                    expee.getExperiment().getExperimentId() == expId)
                return expee;
        throw new NotExistException("experimentee", email);
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

    public GradingTask getGradingTaskById(String researcherName, int expId, int id) throws NotExistException {
        ManagementUser man = getManagerByName(researcherName);
        Experiment exp = man.getExperiment(expId);
        for (GradingTask gt : gradingTasks) {
            if (gt.getBaseExperiment().equals(exp) && gt.getGradingTaskId()==id)
                return gt;
        }
        throw new NotExistException("grading task", ""+id);
    }

    public List<GradingTask> getAllGradingTasks(String researcherName, int expId) throws NotExistException {
        ManagementUser man = getManagerByName(researcherName);
        Experiment exp = man.getExperiment(expId);
        List<GradingTask> ret = new ArrayList<>();
        for (GradingTask gt : gradingTasks) {
            if (gt.getBaseExperiment().equals(exp))
                ret.add(gt);
        }
        return ret;
    }

    public GraderToGradingTask getGraderToGradingTask(Grader grader, GradingTask gradingTask) throws NotExistException {
        for (GraderToGradingTask g : graderToGradingTasks) {
            if (g.getGraderToGradingTaskID().getGraderEmail().equals(grader.getGraderEmail()) && g.getGraderToGradingTaskID().getGradingTaskId()==gradingTask.getGradingTaskId())
                return g;
        }
        throw new NotExistException("grading task", ""+gradingTask.getGradingTaskId());
    }

    public GradersGTToParticipants getGradersGTToParticipants(GraderToGradingTask graderToGradingTask, Participant participant) {
        for (GradersGTToParticipants g : gradersGTToParticipants) {
            if (g.getGraderToGradingTask().equals(graderToGradingTask) && g.getParticipant().equals(participant)) {
                return g;
            }
        }
        return null;
    }


    //=======================================================================

    public void addManager(ManagementUser manager) {
        managers.add(manager);
    }

    public void addGrader(Grader grader) {
        graders.add(grader);
    }

    public void addExperimentee(Experimentee expee) {
        expee.setAccessCode(""+experimentees.size());
        experimentees.add(expee);
    }

    public void addGradingTask(GradingTask gt) {
        gradingTasks.add(gt);
    }

    public void addGraderToGradingTask(GradingTask gt, Grader g, String code) {
        GraderToGradingTask gtgt = new GraderToGradingTask(gt, g, code);
        graderToGradingTasks.add(gtgt);
        //TODO: maybe should add new one after checking that there's no GraderToGradingTask with gt and g
    }

    public void addExpeeToGradingTask(GradingTask gt, Grader grader, GradersGTToParticipants participantInGradingTask) throws ExistException {
        for (GraderToGradingTask gtgt : graderToGradingTasks) {
            if (gtgt.getGrader().equals(grader) && gtgt.getGradingTask().equals(gt)) {
                gradersGTToParticipants.add(participantInGradingTask);

                return;
            }
        }
        GraderToGradingTask gtgt = new GraderToGradingTask(gt, grader);
        gradersGTToParticipants.add(participantInGradingTask);
    }

    public void addManagementUserToExperiment(ManagementUserToExperiment m) {
        managementUserToExperiments.add(m);
    }
    //=======================================================================

    public boolean isExpeeInExperiment(String email, int expId) {
        for (Experimentee expee : experimentees) {
            if (expee.getExperimenteeEmail().equals(email) && expee.getExperiment().getExperimentId() == expId)
                return true;
        }
        return false;
    }

    public boolean isGraderInTask(String email, int expId, int taskId){
        return false;
    }

    public void flash() {
        instance = new DataCache();
    }
}
