package com.example.demo.Reps;

import com.example.demo.Entities.Grader;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GraderRep extends JpaRepository<Grader, String> {
}
