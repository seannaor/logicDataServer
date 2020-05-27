package com.example.demo.BusinessLayer.Entities;

import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "experiments")
public class Experiment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "experiment_id")
    private int experimentId;
    @Column(name = "experiment_name")
    private String experimentName;
    @Column(name = "published")
    private boolean published = false;
    @Column(name = "is_grading_task_exp")
    private boolean isGradingTaskExp = false;
    @OneToMany(mappedBy = "experiment")
    private List<ManagementUserToExperiment> managementUserToExperiments = new ArrayList<>();
    @OneToMany(mappedBy = "experiment")
    private List<Participant> participants = new ArrayList<>();
    @OneToMany(mappedBy = "experiment")//, fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Stage> stages = new ArrayList<>();

    public Experiment() {
    }

    public Experiment(String experimentName) {
        this.experimentName = experimentName;
    }

    public Experiment(String experimentName, ManagementUser creator) {
        this.experimentName = experimentName;
        ManagementUserToExperiment m = new ManagementUserToExperiment(creator, this, "creator");
    }

    public int getExperimentId() {
        return experimentId;
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName(String experiment_name) {
        this.experimentName = experiment_name;
    }

    public List<ManagementUserToExperiment> getManagementUserToExperiments() {
        return managementUserToExperiments;
    }

    public void setManagementUserToExperiments(List<ManagementUserToExperiment> managementUserToExperiments) {
        this.managementUserToExperiments = managementUserToExperiments;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public void addParticipant(Participant participant) {
        this.participants.add(participant);
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    public Stage getStage(int idx) throws NotExistException {
        for (Stage s : stages) {
            if (s.getStageID().getStageIndex() == idx) return s;
        }
        throw new NotExistException("stage", idx + "");
    }

    public boolean getPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    //=========================== end of setters getters ===============================

    public void addStage(Stage stage) {
        stage.setExp(this);
        stages.add(stage);
    }

    public void addManagementUserToExperiment(ManagementUserToExperiment m) {
        if (!this.managementUserToExperiments.contains(m))
            this.managementUserToExperiments.add(m);
    }

    public boolean containsManger(ManagementUser manger) {
        for (ManagementUserToExperiment m : managementUserToExperiments) {
            if (m.getManagementUser().getBguUsername().equals(manger.getBguUsername())) {
                return true;
            }
        }
        return false;
    }

    public boolean isGradingTaskExp() {
        return isGradingTaskExp;
    }

    public void setGradingTaskExp(boolean gradingTaskExp) {
        isGradingTaskExp = gradingTaskExp;
    }
}
