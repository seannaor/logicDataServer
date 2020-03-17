package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;

import com.example.demo.Entities.Experiment;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "info_stages")
public class InfoStage extends Stage {
    @Lob
    private String info;

<<<<<<< Updated upstream:src/main/java/com/example/demo/BusinessLayer/Entities/Stages/InfoStage.java
    public InfoStage(String info, Experiment experiment) {
        super(experiment);
        this.info =info;
=======
    public InfoStage() {
    }

    public InfoStage(Experiment experiment, int stage_index) {
        super(experiment, stage_index);
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
>>>>>>> Stashed changes:src/main/java/com/example/demo/Entities/Stages/InfoStage.java
    }
}
