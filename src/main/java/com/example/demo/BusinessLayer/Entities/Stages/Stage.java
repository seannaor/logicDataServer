package com.example.demo.BusinessLayer.Entities.Stages;

import com.example.demo.BusinessLayer.Entities.Experiment;

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

    public Stage(Experiment experiment){
        this.experiment = experiment;
        this.stageID = new StageID(experiment.getStages().size());
        experiment.addStage(this);
    }

    public Stage(Experiment experiment, int stage_index) {
<<<<<<< Updated upstream:src/main/java/com/example/demo/BusinessLayer/Entities/Stages/Stage.java
//        this.stage_id = new StageID(experiment.getExperiment_id(), stage_index);
        this.stageID = new StageID(stage_index);
        this.experiment = experiment;
        experiment.addStage(this);
    }

    public StageID getStageID() {
        return stageID;
    }

    public void setStageID(StageID stageID) {
        this.stageID = stageID;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
=======
        this.stage_id = new StageID(experiment.getExperiment_id(), stage_index);
 //       this.stage_id = new StageID(stage_index);
>>>>>>> Stashed changes:src/main/java/com/example/demo/Entities/Stages/Stage.java
        this.experiment = experiment;
    }
}
