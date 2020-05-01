package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.GradingTask.GradersGTToParticipants;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradersGTToParticipantsRep extends JpaRepository<GradersGTToParticipants, GradersGTToParticipants.GradersGTToParticipantsID> {
}
