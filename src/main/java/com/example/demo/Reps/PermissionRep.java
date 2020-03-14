package com.example.demo.Reps;

import com.example.demo.Entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRep extends JpaRepository<Permission, Integer> {
}
