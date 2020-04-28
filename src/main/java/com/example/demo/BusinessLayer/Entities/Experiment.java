package com.example.demo.BusinessLayer.Entities;

import com.example.demo.BusinessLayer.Entities.Stages.Stage;

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
    @ManyToMany(mappedBy = "experiments")
    private List<ManagementUser> managementUsers = new ArrayList<>();
    @OneToMany(mappedBy = "experiment")
    private List<Participant> participants = new ArrayList<>();
    @OneToMany(mappedBy = "experiment")
    private List<Stage> stages = new ArrayList<>();

    public Experiment() {
    }

    public Experiment(String experimentName) {
        this.experimentName = experimentName;
    }

    public Experiment(String experimentName,ManagementUser creator) {
        this.experimentName = experimentName;
        this.managementUsers.add(creator);
    }

    public int getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(int experiment_id) {
        this.experimentId = experiment_id;
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName(String experiment_name) {
        this.experimentName = experiment_name;
    }

    public List<ManagementUser> getManagementUsers() {
        return managementUsers;
    }

    public void setManagementUsers(List<ManagementUser> managementUsers) {
        this.managementUsers = managementUsers;
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

    //=========================== end of setters getters ===============================

    public void addStage(Stage stage) {
        stage.setExp(this);
        stages.add(stage);
    }

    public void addManagementUser(ManagementUser mu){
        this.managementUsers.add(mu);
    }

}
