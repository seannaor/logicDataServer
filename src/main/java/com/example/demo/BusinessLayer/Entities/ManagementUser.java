package com.example.demo.BusinessLayer.Entities;

import com.example.demo.BusinessLayer.Exceptions.ExistException;
import com.example.demo.BusinessLayer.Exceptions.NotExistException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    private List<Permission> permissions = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "management_users_to_experiments",
            joinColumns = {@JoinColumn(name = "bgu_username")},
            inverseJoinColumns = {@JoinColumn(name = "experiment_id")}
    )
    private List<Experiment> experiments = new ArrayList<>();

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

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<Experiment> getExperiments() {
        return experiments;
    }

    public void setExperiments(List<Experiment> experiments) {
        this.experiments = experiments;
    }

    //======================= end of setters and getters =======================

    public Experiment getExperiment(int id) throws NotExistException {
        for (Experiment exp : experiments) {
            if (exp.getExperimentId() == id) return exp;
        }
        throw new NotExistException("experiment",String.valueOf(id));
    }

    public Experiment getExperimentByName(String name) throws NotExistException {
        for (Experiment exp : experiments) {
            if (exp.getExperimentName().equals(name)) return exp;
        }
        throw new NotExistException("experiment",name);
    }

    public void addExperiment(Experiment exp) {
        experiments.add(exp);
        exp.addManagementUser(this);
    }

    public void removeExp(String expName) {
        Experiment toRemove = null;
        for (Experiment exp : experiments)
            if (exp.getExperimentName().equals(expName)) {
                experiments.remove(exp);
                break;
            }
    }
}
