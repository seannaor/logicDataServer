package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.ManagementUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ManagementUserRep extends JpaRepository<ManagementUser, String> {
    @Query("select m from ManagementUser m where m.userEmail = ?1")
    ManagementUser findByEmail(String email);
}
