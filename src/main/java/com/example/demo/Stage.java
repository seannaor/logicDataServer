package com.example.demo;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "stages")
public class Stage {
    @EmbeddedId
    private StageID stage_id;
    @MapsId("experiment_id")
    @ManyToOne
    @JoinColumn(name = "experiment_id")
    private Experiment experiment;

    @Embeddable
    public static class StageID implements Serializable {
        private int experiment_id;
        private int stage_index;

        public StageID () {}

        public StageID (int experiment_id, int stage_index) {
            this.experiment_id = experiment_id;
            this.stage_index = stage_index;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StageID stageID = (StageID) o;
            return experiment_id == stageID.experiment_id &&
                    stage_index == stageID.stage_index;
        }

        @Override
        public int hashCode() {
            return Objects.hash(experiment_id, stage_index);
        }
    }

    public Stage () {}

    public Stage (int experiment_id, int stage_index) {
        StageID stageID = new StageID(experiment_id, stage_index);
        this.stage_id = stageID;
    }
}