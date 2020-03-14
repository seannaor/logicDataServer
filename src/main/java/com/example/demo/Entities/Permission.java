package com.example.demo.Entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int permission_id;
    private String permission_name;
    @ManyToMany(mappedBy = "permissions")
    private Set<ManagementUser> managementUsers = new HashSet<>();

    public Permission() {
    }

    public Permission(String permission_name) {
        this.permission_name = permission_name;
    }

    public int getPermission_id() {
        return permission_id;
    }

    public void setPermission_id(int permission_id) {
        this.permission_id = permission_id;
    }

    public String getPermission_name() {
        return permission_name;
    }

    public void setPermission_name(String permission_name) {
        this.permission_name = permission_name;
    }

    public Set<ManagementUser> getManagementUsers() {
        return managementUsers;
    }

    public void setManagementUsers(Set<ManagementUser> managementUsers) {
        this.managementUsers = managementUsers;
    }
}
