package com.example.demo.BusinessLayer.Entities.Results;


import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Stages.Requirement;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Entities.Stages.TaggingStage;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "requirement_tags")
public class RequirementTag {

    @Embeddable
    public static class RequirementTagID implements Serializable {
        @Column(name = "start_char_loc")
        private int startCharLoc;
        private Requirement.RequirementID requirementID;
        private int participantId;

        public RequirementTagID() { }

        public RequirementTagID(int startCharLoc, Requirement.RequirementID requirementID, int participantId) {
            this.startCharLoc = startCharLoc;
            this.requirementID = requirementID;
            this.participantId = participantId;
        }
    }

    @EmbeddedId
    private RequirementTagID requirementTagID;
    @MapsId("taggingResult")
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "participant_id", referencedColumnName = "participant_id"),
            @JoinColumn(name = "stage_index", referencedColumnName = "stage_index"),
            @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id")
    })
    private TaggingResult taggingResult;

    @Column(name = "length")
    private int length;

    @MapsId("taggingResult")
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "requirement_index", referencedColumnName = "requirement_index"),
            @JoinColumn(name = "stage_index", referencedColumnName = "stage_index"),
            @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id")
    })
    private Requirement requirement;

    public RequirementTag() { }

    public RequirementTag(int startCharLoc, int length, Requirement requirement, TaggingResult taggingResult) {
        this.requirementTagID = new RequirementTagID(startCharLoc, requirement.getRequirementID(), taggingResult.getParticipant().getParticipantId());
        this.length = length;
        this.taggingResult = taggingResult;
        this.requirement = requirement;
        this.taggingResult.addTag(this);
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

    public TaggingResult getTaggingResult() {
        return taggingResult;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public Stage.StageID getStageID(){
        return this.requirement.getStageID();
    }
}
