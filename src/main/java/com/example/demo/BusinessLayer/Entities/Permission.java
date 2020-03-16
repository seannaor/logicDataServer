package com.example.demo.BusinessLayer.Entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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
    private Set<ManagementUser> managementUsers = new HashSet<>();

    public Permission() {
    }

    public Permission(String permissionName) {
        this.permissionName = permissionName;
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

    public Set<ManagementUser> getManagementUsers() {
        return managementUsers;
    }

    public void setManagementUsers(Set<ManagementUser> managementUsers) {
        this.managementUsers = managementUsers;
    }
}
