package com.example.demo.BusinessLayer.Entities.Results;


import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Stages.Requirement;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "requirement_tags")
public class RequirementTag {
    @Embeddable
    public static class RequirementTagID implements Serializable {
        @Column(name = "participant_id")
        private int participantId;
        @Column(name = "start_char_loc")
        private int startCharLoc;
    }

    @EmbeddedId
    private RequirementTagID requirementTagID;
    @MapsId("participantId")
    @ManyToOne
    @JoinColumn(name = "participant_id")
    private Participant participant;
    @Column(name = "length")
    private int length;
    @ManyToMany
    @JoinTable(
            name = "requirements_to_requirement_tags",
            joinColumns = {
                    @JoinColumn(name = "participant_id", referencedColumnName = "participant_id"),
                    @JoinColumn(name = "start_char_loc", referencedColumnName = "start_char_loc")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "requirement_index", referencedColumnName = "requirement_index"),
                    @JoinColumn(name = "stage_index", referencedColumnName = "stage_index"),
                    @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id")}
    )
    private Set<Requirement> requirements;
}
