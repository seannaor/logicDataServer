package com.example.demo.Entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "management_users")
public class ManagementUser {
    @Id
    private String bgu_username;
    private String bgu_password;
    private String user_email;
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "management_users_permissions",
            joinColumns = {@JoinColumn(name = "bgu_username")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id")}
    )
    private Set<Permission> permissions = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "management_users_to_experiments",
            joinColumns = {@JoinColumn(name = "bgu_username")},
            inverseJoinColumns = {@JoinColumn(name = "experiment_id")}
    )
    private Set<Experiment> experiments = new HashSet<>();

    public ManagementUser(String bgu_username, String bgu_password, String user_email) {
        this.bgu_username = bgu_username;
        this.bgu_password = bgu_password;
        this.user_email = user_email;
    }

    public ManagementUser() {
    }

    public String getBgu_username() {
        return bgu_username;
    }

    public void setBgu_username(String bgu_username) {
        this.bgu_username = bgu_username;
    }

    public String getBgu_password() {
        return bgu_password;
    }

    public void setBgu_password(String bgu_password) {
        this.bgu_password = bgu_password;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
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
}
