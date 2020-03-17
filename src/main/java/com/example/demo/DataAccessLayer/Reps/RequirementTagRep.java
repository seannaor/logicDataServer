package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.Results.RequirementTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequirementTagRep extends JpaRepository<RequirementTag, RequirementTag.RequirementTagID> {
}
