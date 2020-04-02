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
        managementUsers.add(man);
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permission_id) {
        this.permissionId = permission_id;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permission_name) {
        this.permissionName = permission_name;
    }

    public List<ManagementUser> getManagementUsers() {
        return managementUsers;
    }

    public void setManagementUsers(List<ManagementUser> managementUsers) {
        this.managementUsers = managementUsers;
    }
}
