package com.example.demo.Entities;

import com.example.demo.Entities.Stages.Stage;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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
    private Set<ManagementUser> managementUsers = new HashSet<>();
    @OneToMany(mappedBy = "experiment")
    private Set<Participant> participants = new HashSet<>();
    @OneToMany(mappedBy = "experiment")
    private Set<Stage> stages = new HashSet<>();

    public Experiment() {
    }

    public Experiment(String experimentName) {
        this.experimentName = experimentName;

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

    public Set<ManagementUser> getManagementUsers() {
        return managementUsers;
    }

    public void setManagementUsers(Set<ManagementUser> managementUsers) {
        this.managementUsers = managementUsers;
    }

    public Set<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Participant> participants) {
        this.participants = participants;
    }

    public Set<Stage> getStages() {
        return stages;
    }

    public void setStages(Set<Stage> stages) {
        this.stages = stages;
    }
}
