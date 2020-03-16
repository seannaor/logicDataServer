package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRep extends JpaRepository<Permission, Integer> {
}
