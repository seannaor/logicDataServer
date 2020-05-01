package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.ManagementUserToExperiment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagementUserToExperimentRep extends JpaRepository<ManagementUserToExperiment, ManagementUserToExperiment.ManagementUserToExperimentID> {
}
