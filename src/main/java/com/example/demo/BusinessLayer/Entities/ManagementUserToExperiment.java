package com.example.demo.BusinessLayer.Entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "management_users_to_experiments")
public class ManagementUserToExperiment {
    @Embeddable
    public static class ManagementUserToExperimentID implements Serializable {
        @Column(name = "bgu_username")
        private String bguUsername;
        @Column(name = "experiment_id")
        private int experimentId;

        public ManagementUserToExperimentID() { }

        public ManagementUserToExperimentID(String bguUsername, int experimentId) {
            this.bguUsername = bguUsername;
            this.experimentId = experimentId;
        }

        public void setBguUsername(String bguUsername) {
            this.bguUsername = bguUsername;
        }

        public void setExperimentId(int experimentId) {
            this.experimentId = experimentId;
        }
    }
    @EmbeddedId
    private ManagementUserToExperimentID managementUserToExperimentID;
    @Column(name = "role")
    private String role;
    @ManyToOne
    @MapsId("bguUsername")
    @JoinColumn(name = "bgu_username", referencedColumnName = "bgu_username")
    private ManagementUser managementUser;
    @ManyToOne
    @MapsId("experimentId")
    @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id")
    private Experiment experiment;

    public ManagementUserToExperiment() { }

    public ManagementUserToExperiment(ManagementUser managementUser, Experiment experiment, String role) {
        this.managementUserToExperimentID = new ManagementUserToExperimentID(managementUser.getBguUsername(), experiment.getExperimentId());
        this.managementUser = managementUser;
        this.experiment = experiment;
        this.role = role;
        this.experiment.addManagementUserToExperiment(this);
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
        this.managementUserToExperimentID.setExperimentId(experiment.getExperimentId());

    }

    public ManagementUser getManagementUser() {
        return managementUser;
    }

    public void setManagementUser(ManagementUser managementUser) {
        this.managementUser = managementUser;
        this.managementUserToExperimentID.setBguUsername(managementUser.getBguUsername());
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
