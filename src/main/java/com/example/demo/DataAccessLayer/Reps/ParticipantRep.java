package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRep extends JpaRepository<Participant, Integer> {
}
