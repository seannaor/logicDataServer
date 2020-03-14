package com.example.demo.Reps;

import com.example.demo.Entities.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRep extends JpaRepository<Participant, Integer> {
}
