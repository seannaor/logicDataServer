package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToGradingTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GraderToGradingTaskRep extends JpaRepository<GraderToGradingTask, GraderToGradingTask.GraderToGradingTaskID> {
}

