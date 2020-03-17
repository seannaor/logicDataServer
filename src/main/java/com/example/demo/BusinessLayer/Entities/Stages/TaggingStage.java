package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;

import javax.persistence.*;

@Entity
@Table(name = "tagging_stages")
public class TaggingStage extends Stage {

    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "stage_index"),
            @JoinColumn(name = "experiment_id")
    })
    private CodeStage codeStage;

    public TaggingStage() {
    }

    public TaggingStage(CodeStage codeStage, Experiment experiment){
        super(experiment);
        this.codeStage = codeStage;
    }

    public TaggingStage(Experiment experiment, int stage_index) {
        super(experiment, stage_index);
    }

    public CodeStage getCodeStage() {
        return codeStage;
    }

    public void setCodeStage(CodeStage codeStage) {
        this.codeStage = codeStage;
    }
}
