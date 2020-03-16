package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.Experimentee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperimenteeRep extends JpaRepository<Experimentee, String> {
}
