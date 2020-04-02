package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Results.RequirementTag;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "requirements")
public class Requirement {

    @Embeddable
    public static class RequirementID implements Serializable {
        @Column(name = "requirement_index")
        private int requirementIndex;
        private Stage.StageID stageID;

        public RequirementID() { }

        public RequirementID(int requirementIndex, Stage.StageID stageID) {
            this.requirementIndex = requirementIndex;
            this.stageID = stageID;
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

    @ManyToMany(mappedBy = "requirements")
    private Set<RequirementTag> requirementTags = new HashSet<>();

    public Requirement() { }

    public Requirement(CodeStage codeStage, String text) {
        this.requirementID = new RequirementID(codeStage.getRequirements().size(), codeStage.getStageID());
        this.codeStage = codeStage;
        this.text = text;
    }

    public RequirementID getRequirementID() {
        return requirementID;
    }

    public Set<RequirementTag> getRequirementTags() {
        return requirementTags;
    }

    public void setRequirementTags(Set<RequirementTag> requirementTags) {
        this.requirementTags = requirementTags;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
