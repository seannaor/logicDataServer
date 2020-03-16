package com.example.demo.BusinessLayer.Entities.Stages;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "requirements")
public class Requirement {
    @Embeddable
    public static class RequirementID implements Serializable {
        private int requirementIndex;
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
