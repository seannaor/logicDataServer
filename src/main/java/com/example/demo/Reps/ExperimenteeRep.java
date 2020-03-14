package com.example.demo.Reps;

import com.example.demo.Entities.Experimentee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperimenteeRep extends JpaRepository<Experimentee, String> {
}
