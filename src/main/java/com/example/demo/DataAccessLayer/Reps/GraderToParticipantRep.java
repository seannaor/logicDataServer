package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.GradingTask.GraderToParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GraderToParticipantRep extends JpaRepository<GraderToParticipant, GraderToParticipant.GraderToParticipantID> {
}
