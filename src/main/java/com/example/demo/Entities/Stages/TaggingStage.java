package com.example.demo.Entities.Stages;

import com.example.demo.Entities.Experiment;

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

    public TaggingStage(Experiment experiment, int stage_index) {
        super(experiment, stage_index);
    }
}
