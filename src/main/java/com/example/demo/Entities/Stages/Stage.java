package com.example.demo.Entities.Stages;

import com.example.demo.Entities.Experiment;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "stages")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Stage {

    @Embeddable
    public static class StageID implements Serializable {
        private int experiment_id;
        private int stage_index;

        public StageID() {
        }

        public StageID(int stage_index) {
            this.stage_index = stage_index;
        }

        public StageID(int experiment_id, int stage_index) {
            this.experiment_id = experiment_id;
            this.stage_index = stage_index;
        }
    }

    @EmbeddedId
    private StageID stage_id;
    @MapsId("experiment_id")
    @ManyToOne
    @JoinColumn(name = "experiment_id")
    private Experiment experiment;

    public Stage() {
    }

    public Stage(Experiment experiment, int stage_index) {
//        this.stage_id = new StageID(experiment.getExperiment_id(), stage_index);
        this.stage_id = new StageID(stage_index);
        this.experiment = experiment;
    }
}
