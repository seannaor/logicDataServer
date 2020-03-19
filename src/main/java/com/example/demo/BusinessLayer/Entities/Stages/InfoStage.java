package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "info_stages")
public class InfoStage extends Stage {
    @Lob
    @Column(name = "info")
    private String info;

    public InfoStage() {
    }

    public InfoStage(String info, Experiment experiment) {
        super(experiment);
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}