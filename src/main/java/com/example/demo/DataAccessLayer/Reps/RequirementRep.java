package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.Stages.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequirementRep extends JpaRepository<Requirement, Requirement.RequirementID> {
}
