package com.example.demo.BusinessLayer.Entities.Stages;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "requirements")
public class Requirement {
    @Embeddable
    public static class RequirementID implements Serializable {
        @Column(name = "requirement_index")
        private int requirementIndex;
        private Stage.StageID stageID;
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
//    @ManyToMany(mappedBy = "requirements")
//    private Set<RequirementTag> requirementTags;

}
