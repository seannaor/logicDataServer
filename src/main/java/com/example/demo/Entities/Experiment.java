package com.example.demo.Entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "experiments")
public class Experiment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int experiment_id;
    private String experiment_name;
    @ManyToMany(mappedBy = "experiments")
    private Set<ManagementUser> managementUsers = new HashSet<>();
    @OneToMany(mappedBy = "experiment")
    private Set<Participant> participants = new HashSet<>();
    @OneToMany(mappedBy = "experiment")
    private Set<Stage> stages = new HashSet<>();

    public Experiment() {
    }

    public Experiment(String experiment_name) {
        this.experiment_name = experiment_name;

    }

    public int getExperiment_id() {
        return experiment_id;
    }

    public void setExperiment_id(int experiment_id) {
        this.experiment_id = experiment_id;
    }

    public String getExperiment_name() {
        return experiment_name;
    }

    public void setExperiment_name(String experiment_name) {
        this.experiment_name = experiment_name;
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
