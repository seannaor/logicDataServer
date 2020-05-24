package com.example.demo.BusinessLayer.Entities.Results;


import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Stages.Requirement;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Entities.Stages.TaggingStage;
import org.hibernate.annotations.ManyToAny;

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
        @Column(name = "requirement_index")
        private int requirementIndex;
        @Column(name = "participant_id")
        private int participantId;
        @Column(name = "tagging_stage_index")
        private int taggingIndex;
        @Column(name = "code_stage_index")
        private int codeIndex;
        @Column(name = "experiment_id")
        private int experimentId;

        public RequirementTagID() { }

        public RequirementTagID(int startCharLoc, int requirementIndex, int participantId, int taggingIndex, int codeIndex, int experimentId) {
            this.startCharLoc = startCharLoc;
            this.requirementIndex = requirementIndex;
            this.participantId = participantId;
            this.taggingIndex = taggingIndex;
            this.codeIndex = codeIndex;
            this.experimentId = experimentId;
        }
    }

    @EmbeddedId
    private RequirementTagID requirementTagID;

    @MapsId("resultID")
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "tagging_stage_index", referencedColumnName = "stage_index"),
            @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id"),
            @JoinColumn(name = "participant_id", referencedColumnName = "participant_id")
    })
    private TaggingResult taggingResult;

    @MapsId("requirementID")
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "requirement_index", referencedColumnName = "requirement_index"),
            @JoinColumn(name = "code_stage_index", referencedColumnName = "stage_index"),
            @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id")
    })
    private Requirement requirement;

    @Column(name = "length")
    private int length;

    public RequirementTag() { }

    public RequirementTag(int startCharLoc, int length, Requirement requirement, TaggingResult taggingResult) {
        this.requirementTagID = new RequirementTagID(startCharLoc, requirement.getRequirementID().getRequirementIndex(), taggingResult.getParticipant().getParticipantId(), taggingResult.getStage().getStageID().getStageIndex(), requirement.getStageID().getStageIndex(), taggingResult.getStage().getExperiment().getExperimentId());
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
