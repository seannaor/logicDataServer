package com.example.demo.Entities.Stages;

import com.example.demo.Entities.Results.RequirementTag;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "requirements")
public class Requirement {
    @Embeddable
    public static class RequirementID implements Serializable {
        private int requirement_index;
        private Stage.StageID stageID;
    }

    @EmbeddedId
    private RequirementID requirementID;
    @MapsId("stageID")
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "stage_index"),
            @JoinColumn(name = "experiment_id")
    })
    private CodeStage codeStage;
    @Lob
    private String text;
//    @ManyToMany(mappedBy = "requirements")
//    private Set<RequirementTag> requirementTags;

}
