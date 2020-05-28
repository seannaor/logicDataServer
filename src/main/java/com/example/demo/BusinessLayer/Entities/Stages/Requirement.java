package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Results.RequirementTag;
import com.example.demo.BusinessLayer.Entities.Results.TaggingResult;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "requirements")
public class Requirement {

    @Embeddable
    public static class RequirementID implements Serializable {
        @Column(name = "requirement_index")
        private int requirementIndex;
        @Column(name = "stage_index")
        private int stageIndex;
        @Column(name = "experiment_id")
        private int experimentId;

        public RequirementID() {
        }

        public RequirementID(int requirementIndex, int stageIndex, int experimentId) {
            this.requirementIndex = requirementIndex;
            this.stageIndex = stageIndex;
            this.experimentId = experimentId;
        }

        public int getRequirementIndex() {
            return requirementIndex;
        }
    }

    @EmbeddedId
    private RequirementID requirementID;
    @MapsId("stageID")
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "stage_index", referencedColumnName = "stage_index"),
            @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id")
    })
    private CodeStage codeStage;

    @Lob
    @Column(name = "text")
    private String text;

    public Requirement() {
    }

    public Requirement(CodeStage codeStage, String text) {
        this.requirementID = new RequirementID(codeStage.getRequirements().size(), codeStage.getStageID().getStageIndex(), codeStage.getExperiment().getExperimentId());
        this.codeStage = codeStage;
        this.text = text;
    }

    public RequirementID getRequirementID() {
        return requirementID;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public int getIndex() {
        return this.requirementID.requirementIndex;
    }

    public RequirementTag tag(JSONObject data, TaggingResult taggingResult) {
        RequirementTag tag = new RequirementTag((int) data.get("start_loc"), (int) data.get("length"), this, taggingResult);
        return tag;
    }

    public Stage.StageID getStageID() {
        return this.codeStage.getStageID();
    }
}
