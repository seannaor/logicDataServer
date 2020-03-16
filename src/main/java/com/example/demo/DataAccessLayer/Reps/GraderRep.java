package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.Grader;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GraderRep extends JpaRepository<Grader, String> {
}
