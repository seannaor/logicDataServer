package com.example.demo.BusinessLayer.Entities.Results;


import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Stages.Requirement;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "requirement_tags")
public class RequirementTag {

    @Embeddable
    public static class RequirementTagID implements Serializable {
        @Column(name = "participant_id")
        private int participantId;
        @Column(name = "start_char_loc")
        private int startCharLoc;

        public RequirementTagID() { }

        public RequirementTagID(int participantId, int startCharLoc) {
            this.participantId = participantId;
            this.startCharLoc = startCharLoc;
        }
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
    private List<Requirement> requirements;

    public RequirementTag() { }

    public RequirementTag(int startCharLoc, int length, Participant participant, List<Requirement> requirements) {
        this.requirementTagID = new RequirementTagID(participant.getParticipantId(), startCharLoc);
        this.length = length;
        this.participant = participant;
        this.requirements = requirements;
    }

    public RequirementTagID getRequirementTagID() {
        return requirementTagID;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setStart(int startCharLoc){
        this.requirementTagID.startCharLoc=startCharLoc;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public void setRequirement(Requirement requirement){
        this.requirements.add(requirement);
    }

    public Participant getParticipant() {
        return participant;
    }

    public Stage.StageID getStageID(){
        return this.requirements.get(0).getStageID();
    }
}
