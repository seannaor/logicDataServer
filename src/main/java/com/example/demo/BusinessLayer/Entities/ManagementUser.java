package com.example.demo.BusinessLayer.Entities;

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

    @OneToMany(mappedBy = "managementUser")
    private List<ManagementUserToExperiment> managementUserToExperiments = new ArrayList<>();

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

    public List<ManagementUserToExperiment> getManagementUserToExperiments() {
        return managementUserToExperiments;
    }

    public void setManagementUserToExperiments(List<ManagementUserToExperiment> managementUserToExperiments) {
        this.managementUserToExperiments = managementUserToExperiments;
    }

    //======================= end of setters and getters =======================

    public void addPermission(Permission p) {
        this.permissions.add(p);
    }

    public Experiment getExperiment(int expId) throws NotExistException{
        for (ManagementUserToExperiment m : this.managementUserToExperiments) {
            if(m.getExperiment().getExperimentId() == expId) {
                return m.getExperiment();
            }
        }
        throw new NotExistException("experiment",String.valueOf(expId));
    }

    public Experiment getExperimentByName(String expName) throws NotExistException{
        for (ManagementUserToExperiment m : this.managementUserToExperiments) {
            if(m.getExperiment().getExperimentName() == expName) {
                return m.getExperiment();
            }
        }
        throw new NotExistException("experiment",String.valueOf(expName));
    }

    public ManagementUserToExperiment getManagementUserToExperiment(ManagementUserToExperiment managementUserToExperiment) throws NotExistException {
        for (ManagementUserToExperiment m : managementUserToExperiments) {
            if (m.equals(managementUserToExperiment))
                return m;
        }
        throw new NotExistException("managementUserToExperiment",String.valueOf(managementUserToExperiment));
    }

    public void addManagementUserToExperiment(ManagementUserToExperiment m) {
        try{
            getManagementUserToExperiment(m);
            return;
        } catch (NotExistException ignore) {
            managementUserToExperiments.add(m);
            m.getExperiment().addManagementUserToExperiment(m);
        }
    }

    public void removeManagementUserToExperimentById(Experiment e) {
        for (ManagementUserToExperiment m : managementUserToExperiments)
            if (m.getManagementUser().equals(this) && m.getExperiment().equals(e)) {
                managementUserToExperiments.remove(m);
                break;
            }
    }

    public void removeManagementUserToExperiment(ManagementUserToExperiment managementUserToExperiment) {
        for (ManagementUserToExperiment m : managementUserToExperiments)
            if (m.equals(managementUserToExperiment)) {
                managementUserToExperiments.remove(m);
                break;
            }
    }

    public List<Experiment> getExperimentes() {
        List<Experiment> all = new ArrayList<>();
        for(ManagementUserToExperiment userToExp: managementUserToExperiments){
            all.add(userToExp.getExperiment());
        }
        return all;
    }
}
