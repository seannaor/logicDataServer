package com.example.demo.BusinessLayer.Entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "management_users")
public class ManagementUser {
    @Id
    @Column(name = "bgu_username")
    private String bguUsername;
    @Column(name = "bgu_password")
    private String bguPassword;
    @Column(name = "user_email")
    private String userEmail;
    @ManyToMany
    @JoinTable(
            name = "management_users_permissions",
            joinColumns = {@JoinColumn(name = "bgu_username")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id")}
    )
    private Set<Permission> permissions = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "management_users_to_experiments",
            joinColumns = {@JoinColumn(name = "bgu_username")},
            inverseJoinColumns = {@JoinColumn(name = "experiment_id")}
    )
    private Set<Experiment> experiments = new HashSet<>();

    public ManagementUser(String bguUsername, String bguPassword, String userEmail) {
        this.bguUsername = bguUsername;
        this.bguPassword = bguPassword;
        this.userEmail = userEmail;
    }

    public ManagementUser() {
    }

    public String getBguUsername() {
        return bguUsername;
    }

    public void setBguUsername(String bgu_username) {
        this.bguUsername = bgu_username;
    }

    public String getBguPassword() {
        return bguPassword;
    }

    public void setBguPassword(String bgu_password) {
        this.bguPassword = bgu_password;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String user_email) {
        this.userEmail = user_email;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Experiment> getExperiments() {
        return experiments;
    }

    public void setExperiments(Set<Experiment> experiments) {
        this.experiments = experiments;
    }

    public Experiment getExperiment(String name) {
        for (Experiment exp : experiments) {
            if(exp.getExperimentName().equals(name)) return exp;
        }
        return null;
    }
}
