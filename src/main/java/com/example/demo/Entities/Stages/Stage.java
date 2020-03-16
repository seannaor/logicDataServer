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
        private int experimentId;
        private int stageIndex;

        public StageID() {
        }

        public StageID(int stageIndex) {
            this.stageIndex = stageIndex;
        }

        public StageID(int experimentId, int stageIndex) {
            this.experimentId = experimentId;
            this.stageIndex = stageIndex;
        }
    }

    @EmbeddedId
    private StageID stageID;
    @MapsId("experimentId")
    @ManyToOne
    @JoinColumn(name = "experiment_id")
    private Experiment experiment;

    public Stage() {
    }

    public Stage(Experiment experiment, int stage_index) {
//        this.stage_id = new StageID(experiment.getExperiment_id(), stage_index);
        this.stageID = new StageID(stage_index);
        this.experiment = experiment;
    }
}
