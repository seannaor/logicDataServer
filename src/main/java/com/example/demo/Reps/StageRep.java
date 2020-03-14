package com.example.demo.Reps;

import com.example.demo.Entities.Stage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StageRep extends JpaRepository<Stage, Stage.StageID> {
}
