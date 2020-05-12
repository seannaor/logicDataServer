package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GraderToGradingTaskRep extends JpaRepository<GraderToGradingTask, GraderToGradingTask.GraderToGradingTaskID> {
    @Query("select g2gt from GraderToGradingTask g2gt where g2gt.graderAccessCode = ?1")
    GraderToGradingTask findByGradersCode(String code);
}

