package com.example.demo.DataAccessLayer.Reps;

import com.example.demo.BusinessLayer.Entities.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExperimentRep extends JpaRepository<Experiment, Integer> {
    // For a given experiment, the query returns for each experimentee that participates in the experiment his tagging result
    // which can be used to get all of his tags. It returns it in a list of object arrays of size 2 the first object is the Experimentee entity
    // and the second is his Tagging Result.
    @Query("SELECT expee, tagRes from Experiment exp INNER JOIN Participant p ON " +
            "exp.experimentId = p.experiment.experimentId " +
            "INNER JOIN Experimentee expee ON expee.participant.participantId = p.participantId " +
            "INNER JOIN TaggingResult tagRes ON tagRes.participant.participantId = p.participantId " +
            "WHERE exp.experimentId = ?1 "
    )
    List<Object[]> getExperimenteesTagsByExperiment(int exp_id);

}
