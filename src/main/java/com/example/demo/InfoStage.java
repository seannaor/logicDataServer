package com.example.demo;

import javax.persistence.*;

@Entity
@Table(name = "info_stages")
public class InfoStage extends Stage {
    @Lob
    private String info;

    public InfoStage() {
    }

    public InfoStage (int experiment_id, int stage_index) {
        super(experiment_id, stage_index);
    }

    public InfoStage(String info) {
        this.info = info;
    }

    public InfoStage(int experiment_id, int stage_index, String info) {
        super(experiment_id, stage_index);
        this.info = info;
    }
}
