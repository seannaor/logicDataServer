package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.GradingTask.GradingTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradingTaskRep extends JpaRepository<GradingTask, Integer> {
}
