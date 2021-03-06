package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.Experimentee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ExperimenteeRep extends JpaRepository<Experimentee, UUID> {
    @Query("select e from Experimentee e where e.experimenteeEmail = ?1")
    Experimentee findByEmail(String email);

    @Query("select e from Experimentee e where e.experimenteeEmail = ?1 and e.participant.experiment.experimentId = ?2")
    Experimentee findByEmailAndExp(String email, int expId);

    @Query("select e from Experimentee e where e.participant.participantId = ?1")
    Experimentee findExperimenteeByParticipantId(int participant_id);
}
