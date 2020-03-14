package com.example.demo.Reps;
import com.example.demo.Entities.ManagementUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagementUserRep extends JpaRepository<ManagementUser, String> {
}
