package com.example.demo.BusinessLayer.Entities.Results;


import com.example.demo.BusinessLayer.Entities.Stages.Requirement;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

@Entity
@Table(name = "requirement_tags")
public class RequirementTag {

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

    public RequirementTag() {
    }

    public RequirementTag(int startCharLoc, int length, Requirement requirement) {
        this.requirementTagID = new RequirementTagID();
        this.requirementTagID.setStartCharLoc(startCharLoc);
        this.requirementTagID.setRequirementIndex(requirement.getRequirementID().getRequirementIndex());
//        this.requirementTagID.setCodeIndex(requirement.getStageID().getStageIndex());

        this.length = length;
        this.requirement = requirement;
    }

    public void setCodeStageIdx(int i) {
        this.requirementTagID.setCodeIndex(i);
    }

    // Getters
    public int getStart() {
        return this.requirementTagID.startCharLoc;
    }

    public void setStart(int startCharLoc) {
        this.requirementTagID.startCharLoc = startCharLoc;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public TaggingResult getTaggingResult() {
        return taggingResult;
    }

    public void setTaggingResult(TaggingResult taggingResult) {
        this.taggingResult = taggingResult;
        this.requirementTagID.setParticipantId(taggingResult.getParticipant().getParticipantId());
        this.requirementTagID.setTaggingIndex(taggingResult.getStage().getStageID().getStageIndex());
        this.requirementTagID.setExperimentId(taggingResult.getStage().getExperiment().getExperimentId());
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public Stage.StageID getStageID() {
        return this.requirement.getStageID();
    }

    public RequirementTagID getRequirementTagID() {
        return requirementTagID;
    }

    public Map<String, Object> getAsMap() {
        return Map.of("start Char", this.getRequirementTagID().getStartCharLoc(),
                "length", length, "requirement index", this.requirement.getIndex());
    }

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

        public RequirementTagID() {
        }

        public void setRequirementIndex(int requirementIndex) {
            this.requirementIndex = requirementIndex;
        }

        public void setParticipantId(int participantId) {
            this.participantId = participantId;
        }

        public void setTaggingIndex(int taggingIndex) {
            this.taggingIndex = taggingIndex;
        }

        public void setCodeIndex(int codeIndex) {
            this.codeIndex = codeIndex;
        }

        public void setExperimentId(int experimentId) {
            this.experimentId = experimentId;
        }

        public int getStartCharLoc() {
            return this.startCharLoc;
        }

        public void setStartCharLoc(int startCharLoc) {
            this.startCharLoc = startCharLoc;
        }
    }
}
