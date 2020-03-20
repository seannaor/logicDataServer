package com.example.demo.BusinessLayer;

import com.example.demo.BusinessLayer.Entities.Experiment;
import com.example.demo.BusinessLayer.Entities.Experimentee;
import com.example.demo.BusinessLayer.Entities.Grader;
import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import com.example.demo.BusinessLayer.Entities.ManagementUser;

import java.util.ArrayList;
import java.util.List;

public class DataCache {

    /*
    This class purpose is to be the channel witch we can add or get data.
    If data not found, we should look for it in the database.
     */

    private static DataCache instance;
    private List<ManagementUser> managers;
    private List<Experimentee> experimentees;
    private List<Grader> graders;
    private List<GraderToGradingTask> gradingTasks;

    private DataCache() {
        managers = new ArrayList<>();
        experimentees = new ArrayList<>();
        graders = new ArrayList<>();
        gradingTasks = new ArrayList<>();
    }

    public static DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }
        return instance;
    }

    public ManagementUser getManagerByName(String name) {
        for (ManagementUser manager : managers)
            if (manager.getBguUsername().equals(name)) return manager;
        return null;
    }

    public ManagementUser getManagerByEMail(String email) {
        for (ManagementUser manager : managers)
            if (manager.getUserEmail().equals(email)) return manager;
        return null;
    }

    public Grader getGraderByEMail(String email) {
        for (Grader grader : graders)
            if (grader.getGraderEmail().equals(email)) return grader;
        return null;
    }

    public Grader getGraderByCode(String code) {
        for (GraderToGradingTask g2gt : gradingTasks)
            if (g2gt.getGraderAccessCode().equals(code)) return g2gt.getGrader();

        return null;
    }

    public Experimentee getExpeeByEMail(String email) {
        for (Experimentee expee : experimentees)
            if (expee.getExperimenteeEmail().equals(email)) return expee;
        return null;
    }

    public Experimentee getExpeeByCode(String code) {
        for (Experimentee expee : experimentees)
            if (expee.getAccessCode().equals(code)) return expee;
        return null;
    }

    public GradingTask getGradingTaskByName(String researcherName, int expId, String gradName) {
        ManagementUser man = getManagerByName(researcherName);
        Experiment exp = man.getExperiment(expId);
        for (GraderToGradingTask gtgt : gradingTasks) {
            GradingTask gt = gtgt.getGradingTask();
            if(gt.getBaseExperiment().equals(exp))
                return gt;
        }
        return null;
    }

    public void addManager(ManagementUser manager) {
        managers.add(manager);
    }

    public void addGrader(Grader grader) {
        graders.add(grader);
    }

    public void addExperimentee(Experimentee expee) {
        experimentees.add(expee);
    }

    public void addGradingTask(GraderToGradingTask gtgt) {
        gradingTasks.add(gtgt);
    }

    public void flash() {
        instance = new DataCache();
    }
}
