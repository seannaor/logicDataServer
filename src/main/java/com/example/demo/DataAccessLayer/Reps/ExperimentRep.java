package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperimentRep extends JpaRepository<Experiment, Integer> {
}
