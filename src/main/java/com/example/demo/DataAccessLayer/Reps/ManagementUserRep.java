package com.example.demo.DataAccessLayer.Reps;
import com.example.demo.BusinessLayer.Entities.ManagementUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagementUserRep extends JpaRepository<ManagementUser, String> {
}
