package com.example.demo.BusinessLayer.Entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private int permissionId;
    @Column(name = "permission_name")
    private String permissionName;
    @ManyToMany(mappedBy = "permissions")
    private List<ManagementUser> managementUsers = new ArrayList<>();

    public Permission() {
    }

    public Permission(String permissionName) {
        this.permissionName = permissionName;
    }

    public Permission(String permissionName,ManagementUser man) {
        this.permissionName = permissionName;
        this.managementUsers.add(man);
        man.addPermission(this);
    }

    public int getPermissionId() {
        return permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permission_name) {
        this.permissionName = permission_name;
    }

    // TODO: maybe no need for managementUsers list here
    public List<ManagementUser> getManagementUsers() {
        return managementUsers;
    }

    public void setManagementUsers(List<ManagementUser> managementUsers) {
        this.managementUsers = managementUsers;
    }
}
