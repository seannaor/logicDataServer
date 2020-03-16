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

    public TaggingStage(Experiment experiment){
        super(experiment);
    }

    public TaggingStage(Experiment experiment, int stage_index) {
        super(experiment, stage_index);
    }
}
