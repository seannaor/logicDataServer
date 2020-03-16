package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "info_stages")
public class InfoStage extends Stage {
    @Lob
    private String info;

    public InfoStage(String info, Experiment experiment) {
        super(experiment);
        this.info =info;
    }
}
