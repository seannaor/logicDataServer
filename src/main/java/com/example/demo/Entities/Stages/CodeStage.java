package com.example.demo.Entities.Stages;

import com.example.demo.Entities.Experiment;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "code_stages")
public class CodeStage extends Stage {

    @Lob
    private String description;
    @Lob
    private String template;

    public CodeStage() {
    }

    public CodeStage(Experiment experiment, int stage_index) {
        super(experiment, stage_index);
    }
}
