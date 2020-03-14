package com.example.demo.Reps;

import com.example.demo.Entities.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperimentRep extends JpaRepository<Experiment, Integer> {
}
